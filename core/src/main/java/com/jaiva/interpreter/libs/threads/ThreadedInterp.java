package com.jaiva.interpreter.libs.threads;

import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.JaivaException;
import com.jaiva.errors.Warnings;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.runtime.Resources;
import com.yetnt.utils.functional.consumer.ThrowableBiConsumer;
import com.yetnt.utils.functional.generic.ThrowableRunnable;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ThreadedInterp extends Thread {

    public enum CleanupCode {
        SUCCESS,
        INTERNAL_ERR,
        ERR
    }

    private final ThrowableRunnable<Exception> runnable;
    private final ThrowableBiConsumer<CleanupCode, Exception, Exception> cleanup;
    private final Resources resources;
    private final IConfig<Object> config;
    private final Scope scope;
    private final int ln;

    public ThreadedInterp(
            IConfig<Object> config,
            ThrowableRunnable<Exception> runnable,
            ThrowableBiConsumer<CleanupCode, Exception, Exception> cleanup,
            Scope scope, int ln
    ) {
        this.runnable = runnable;
        this.cleanup = cleanup;
        this.config = config;
        this.scope = scope;
        this.ln = ln;

        resources = config.getGlobalResources().registerNewThead(this);
    }

    @Override
    public void run() {
        try {
            runnable.run();
            finish(CleanupCode.SUCCESS, null);
        } catch (JaivaException ex) {
            finish(CleanupCode.ERR, ex);
        } catch (Exception ex) {
            finish(CleanupCode.INTERNAL_ERR, ex);
        }
    }

    private void finish(CleanupCode code, Exception ex) {
        try {
            cleanup.apply(code, ex);
            int r = resources.release();
            if (r > 1) {
                // if it's a single one, its the console scanner via mamela() function which all threads
                // get by default.
                // otherwise the user didn't close all their resources.
                Warnings.println(
                        ln,
                        r + " unclosed resources were automatically closed due to thread " +
                                "exit. It's better to close these resources explicitly.",
                        scope, config
                );
            }
            config.getGlobalResources().releaseCurrent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
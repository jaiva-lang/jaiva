package com.jaiva.interpreter.libs.threads;

import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AFuncArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.fn.BaseFunction;
import com.jaiva.interpreter.symbol.fn.CallProperties;
import com.jaiva.interpreter.symbol.fn.ImplementedFunction;
import com.jaiva.tokenizer.jdoc.JDoc;
import io.github.yetnt.utils.builders.AnsiColour;
import io.github.yetnt.utils.functional.consumer.ThrowableBiConsumer;
import io.github.yetnt.utils.functional.generic.ThrowableRunnable;

import java.util.ArrayList;
import java.util.List;

@JaivaLibrary(
        path = "threads",
        description = "Enables threading via function scopes executing on their own thread. Don't ask me" +
                " about 2 threads mutating the same variable... That's ur problem bro."
)
public class ThreadsLib extends BaseLibrary {

    public ThreadsLib() {
        add(new FThread());
    }

    private static class FThread extends ImplementedFunction {
        public FThread() {
            super(
                    FunctionBuilder.start()
                            .name("thread")
                            .setLib("threads")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(
                                                    new AFuncArgument(
                                                            "threaded",
                                                            "The function to execute in it's own thread." +
                                                                    " A function that should take no args.",
                                                            false
                                                    )
                                            )
                                            .add(
                                                    new AFuncArgument(
                                                            "cleanup",
                                                            "The function to execute as cleanup. This" +
                                                                    " function is expected to take 2 string arguments",
                                                            true
                                                    )
                                            )
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc(
                                                    "Executes the given function in it's own separate thread. If the function aught to fail for some" +
                                                            " reason, a string and the error message is passed into a secondary" +
                                                            " cleanup function which is ran on the original thread."
                                            )
                                            .addReturns(
                                                    "The thread Id (Long). Idk what you'd do with this"
                                            )
                                            .addExample("""
                                                    tsea "jaiva/threads"!
                                                    
                                                    kwenza f1() ->
                                                        khuluma("HI")!
                                                        sleep(3000)! @ Sleep for 3 seconds
                                                        khuluma("Damn.")!
                                                    <~
                                                    
                                                    kwenza clean(type, err) ->
                                                        if (err~ > 0) ->
                                                            khuluma("An error occured executing f1!")!
                                                            khuluma(err)!
                                                        <~
                                                    <~
                                                    
                                                    thread(f1, clean)!
                                                    khuluma("OKAY")!
                                                    sleep(2000)!
                                                    khuluma("WOW")!
                                                    
                                                    {
                                                    Output should be:
                                                    "OKAY"
                                                    "HI"
                                                    "WOW"
                                                    "Damn."
                                                    }
                                                    """)
                                            .sinceVersion("6.1.0")
                            )
            );
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            BaseFunction function = fetch("threaded", BaseFunction.class, callProperties);

            BaseFunction cleanup = fetchNullable("cleanup", BaseFunction.class, callProperties, null);

            ThrowableRunnable<Exception> runnable = () -> {
                function.call(
                        new ArrayList<>(), callProperties.config(), callProperties.scope(), callProperties.tFuncCall()
                );
            };

            ThrowableBiConsumer<ThreadedInterp.CleanupCode, Exception, Exception> cleanupHandler = (code, ex) -> {
                // check if clean-up is non-null firs,t otherwise just exit
                if (cleanup == null) return;

                // if the exception belongs to Jaiva remove colours.
                String exceptionMessage =
                        ex != null ?
                        ex.getMessage() != null ? AnsiColour.remove(ex.getMessage()) : "null" : "";

                cleanup.call(
                        new ArrayList<>(List.of(code.toString(), exceptionMessage)),
                        callProperties.config(), callProperties.scope(), callProperties.tFuncCall()
                );
            };

            ThreadedInterp thread = new ThreadedInterp(
                    callProperties.config(), runnable,
                    cleanupHandler, callProperties.scope(),
                    callProperties.getLineNumber()
            );

            thread.start();

            return thread.threadId();

        }
    }
}

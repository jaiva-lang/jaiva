package com.jaiva.interpreter.symbol;

import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public record CallProperties(
        TFuncCall tFuncCall,
        ArrayList<Object> params,
        IConfig<Object> config,
        Scope scope
) {

    @Contract(pure = true)
    public int getLineNumber() {
        return tFuncCall.lineNumber;
    }

    @NotNull
    @Contract(pure = true, value = "->new")
    public BaseFunction.CheckParams toCheckParams() {
        return new BaseFunction.CheckParams(tFuncCall, scope);
    }
}
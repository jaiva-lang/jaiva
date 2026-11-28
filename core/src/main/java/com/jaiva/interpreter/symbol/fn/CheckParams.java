package com.jaiva.interpreter.symbol.fn;

import com.jaiva.interpreter.Scope;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;

import java.util.ArrayList;

public record CheckParams(
        TFuncCall tFuncCall,
        Scope scope,
        ArrayList<Object> params
) {
}

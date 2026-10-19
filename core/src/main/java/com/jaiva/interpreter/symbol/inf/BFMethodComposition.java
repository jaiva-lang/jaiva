package com.jaiva.interpreter.symbol.inf;

import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;
import com.yetnt.utils.functional.function.ThrowableQuadFunction;

import java.util.ArrayList;

public interface BFMethodComposition extends ThrowableQuadFunction<
        ArrayList<Object>,
        IConfig<Object>, Scope,
        TFuncCall,
        Object,
        Exception> {}

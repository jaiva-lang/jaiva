package com.jaiva.interpreter.symbol.inf;


import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;
import io.github.yetnt.utils.functional.function.ThrowableFunction;
import io.github.yetnt.utils.functional.function.ThrowableQuadFunction;
import io.github.yetnt.utils.functional.generic.ThrowableSupplier;

import java.io.IOException;
import java.util.ArrayList;

public interface BFMethodToSupplier<T> extends ThrowableFunction<
        CallProperties,
        ThrowableSupplier<T, IOException>,
        Exception> {
}

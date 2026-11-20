package com.jaiva.interpreter.symbol.inf;

import com.jaiva.interpreter.symbol.CallProperties;
import io.github.yetnt.utils.functional.function.ThrowableFunction;

public interface BFMethod extends ThrowableFunction<
        CallProperties,
        Object,
        Exception> { }

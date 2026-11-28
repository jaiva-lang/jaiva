package com.jaiva.interpreter.symbol.fn.interfaces;

import com.jaiva.interpreter.symbol.fn.CallProperties;
import io.github.yetnt.utils.functional.function.ThrowableFunction;

public interface BFMethod extends ThrowableFunction<
        CallProperties,
        Object,
        Exception> { }

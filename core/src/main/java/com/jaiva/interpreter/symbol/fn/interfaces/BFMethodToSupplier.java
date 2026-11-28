package com.jaiva.interpreter.symbol.fn.interfaces;


import com.jaiva.interpreter.symbol.fn.CallProperties;
import io.github.yetnt.utils.functional.function.ThrowableFunction;
import io.github.yetnt.utils.functional.generic.ThrowableSupplier;

import java.io.IOException;

public interface BFMethodToSupplier<T> extends ThrowableFunction<
        CallProperties,
        ThrowableSupplier<T, IOException>,
        Exception> {
}

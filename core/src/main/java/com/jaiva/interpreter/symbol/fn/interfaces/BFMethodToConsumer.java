package com.jaiva.interpreter.symbol.fn.interfaces;

import com.jaiva.interpreter.symbol.fn.CallProperties;
import io.github.yetnt.utils.functional.consumer.ThrowableConsumer;
import io.github.yetnt.utils.functional.function.ThrowableFunction;

import java.io.IOException;

public interface BFMethodToConsumer<T> extends ThrowableFunction<
        CallProperties,
        ThrowableConsumer<T, IOException>,
        Exception> {
}

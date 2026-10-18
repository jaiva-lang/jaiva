package com.jaiva.interpreter.symbol.inf;

import com.jaiva.interpreter.symbol.CallProperties;
import com.yetnt.utils.functional.consumer.ThrowableConsumer;
import com.yetnt.utils.functional.function.ThrowableFunction;

import java.io.IOException;

public interface BFMethodToConsumer<T> extends ThrowableFunction<
        CallProperties,
        ThrowableConsumer<T, IOException>,
        Exception> {
}

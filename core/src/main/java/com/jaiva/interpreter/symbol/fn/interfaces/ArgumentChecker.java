package com.jaiva.interpreter.symbol.fn.interfaces;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.symbol.fn.CallProperties;
import io.github.yetnt.utils.functional.consumer.ThrowableBiConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface ArgumentChecker<T> extends ThrowableBiConsumer<T, CallProperties, InterpreterException> {
    @NotNull
    @Contract(pure = true)
    public static <T> ArgumentChecker<T> simpleError(Predicate<T> condition, String message) {
        return (a, cp) -> {
            if (condition.test(a))
                throw new InterpreterException.WtfAreYouDoingException(cp.scope(), message, cp.getLineNumber());
        };
    }
}

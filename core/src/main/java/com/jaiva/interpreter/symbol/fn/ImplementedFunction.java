package com.jaiva.interpreter.symbol.fn;

import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.symbol.fn.interfaces.ArgumentChecker;
import com.jaiva.tokenizer.tokens.specific.TFunction;
import com.jaiva.tokenizer.tokens.specific.TVoidValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link ImplementedFunction} is a function which you guessed it, must be implemented!
 * <p>
 *     How it differs from your generic {@link DefinedFunction} in the {@link BaseFunction}
 *     hierarchy is that it's definition is created using a {@link FunctionBuilder}
 *     and hence all libraries use this for the fluent builder. This also
 *     provides helpful methods for fetching well-defined parameters.
 * </p>
 * <p>
 *     The introspection function {@code typeOfFunc} when given an input who's an instance of
 *     {@link ImplementedFunction} returns {@code "library"}
 * </p>
 * @implSpec When extending this class, to actually implement function capabilities you
 * need to override {@link BaseFunction#call(CallProperties)} with your own custom logic. It is also worth
 * noting to avoid throwing custom exceptions. Rather throw any exceptions under {@link JaivaException}
 * but preferably an {@link InterpreterException} subclass. if there is not a subclass with the correct
 * reason or you just want to throw a generic exception use {@link InterpreterException.WtfAreYouDoingException}
 * @see BaseFunction
 * @see CallProperties
 * @see FunctionBuilder
 * @see InterpreterException
 * @see com.jaiva.interpreter.symbol.Symbol
 * @author Lehlogonolo Poole
 */
public non-sealed class ImplementedFunction extends BaseFunction {

    protected final Arguments builtArguments;

    /**
     * Constructs a new ImplementedFunction instance with a function builder
     * @param functionBuilder The function builder object
     */
    public ImplementedFunction(@NotNull FunctionBuilder functionBuilder) {
        super(functionBuilder);
        builtArguments = functionBuilder.getArguments();
    }

    /**
     * Constructs a new ImplementedFunction using raw tokens. This is discouraged and only exists for
     * testing purposes. Rather use {@link #ImplementedFunction(FunctionBuilder)}
     * @param name The name of the function
     * @param token The token representing this function.
     */
    public ImplementedFunction(String name, TFunction token) {
        super(name, token);
        builtArguments = null;
    }

    /**
     * Validates the given value to be of a specific type. This type should only be a type that
     * {@link Primitives#isPrimitive(Object)} accepts or else the error it throws will look like
     * Java leaked internals to the user lol.
     * @param val The object to validate.
     * @param clazz The class to expect it to be
     * @param callProperties The function call properties
     * @param argsCheckers Argument checkers to check and throw after the cast succeeds.
     * @return The input value cast to {@link T}
     * @param <T> The expected type of the object
     * @throws InterpreterException If the object is not an instance of the class or
     * any of the argument checkers fail.
     */
    @SafeVarargs
    @NotNull
    @Contract(pure = true)
    protected final <T> T validate(Object val, @NotNull Class<T> clazz, @NotNull CallProperties callProperties, ArgumentChecker<T>... argsCheckers) throws InterpreterException {
        if (!(clazz.isInstance(val)))
            throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), val, clazz, callProperties.getLineNumber());
        T arg = clazz.cast(val);
        for (ArgumentChecker<T> argumentChecker : argsCheckers) {
            argumentChecker.apply(arg, callProperties);
        }
        return arg;
    }

    /**
     * Fetches a singular argument by index. Where if the argument does not exist it's regarded as a failure.
     * (Although this would be caught later up if {@link #checkParams(CheckParams)} was called.)
     * @param index The index of the parameter
     * @param clazz The class it's expected to be.
     * @param callProperties The function's call properties
     * @param argsCheckers Argument checkers to check and throw after the cast succeeds.
     * @return The object at the index cast to {@link T}
     * @param <T> The expected class of the object
     * @throws InterpreterException Whether the index does not exist within the parameters
     * or if the object is not an instance of the class or any of the argument checkers fail.
     * @implSpec If you defined a parameter as optional, instead use {@link #fetch(int, Class, CallProperties, Object, ArgumentChecker[])}
     * where you can pass a default value.
     * @implNote There is also a {@link #fetch(String, Class, CallProperties, ArgumentChecker[])}
     * which allows fetching by parameter name instead.
     */
    @NotNull
    @SafeVarargs
    @Contract(pure = true)
    protected final <T> T fetch(int index, @NotNull Class<T> clazz, @NotNull CallProperties callProperties, ArgumentChecker<T>... argsCheckers) throws InterpreterException {
        Object val = callProperties.params().get(index);
        if (!(clazz.isInstance(val)))
            throw new InterpreterException.FunctionParametersException(callProperties.scope(), this, String.valueOf(index+1), val, clazz,
                    callProperties.getLineNumber());
        T arg = clazz.cast(val);
        for (ArgumentChecker<T> argumentChecker : argsCheckers) {
            argumentChecker.apply(arg, callProperties);
        }
        return arg;
    }


    /**
     * Fetches a singular argument by index. Where if the argument does not exist the default value will be returned.
     * @param index The index of the parameter
     * @param clazz The class it's expected to be.
     * @param callProperties The function's call properties
     * @param defaultValue The default value to use if: the argument input was not given or the input was {@link TVoidValue}
     * @param argsCheckers Argument checkers to check and throw after the cast succeeds.
     * @return The object at the index cast to {@link T}
     * @param <T> The expected class of the object
     * @throws InterpreterException Whether the index does not exist within the parameters
     * or if the object is not an instance of the class or any of the argument checkers fail.
     */
    @SafeVarargs
    @Contract(pure = true)
    protected final <T> T fetch(int index, @NotNull Class<T> clazz, @NotNull CallProperties callProperties, T defaultValue, ArgumentChecker<T>... argsCheckers) throws InterpreterException {
        if (index > callProperties.params().size() - 1)
            return defaultValue;
        if (callProperties.params().get(index) instanceof TVoidValue)
            return defaultValue;
        return fetch(index, clazz, callProperties, argsCheckers);
    }

    /**
     * Fetches a singular argument by parameter name. Where if the parameter name does not exist
     * within {@link #builtArguments}, an {@link InterpreterException.ImplementationException} is thrown.
     * @param parameterName The name of the parameter as defined in {@link Arguments}
     * @param clazz The class it's expected to be.
     * @param callProperties The function's call properties
     * @param argsCheckers Argument checkers to check and throw after the cast succeeds.
     * @return The object at the resolved parameter index cast to {@link T}
     * @param <T> The expected class of the object
     * @throws InterpreterException If no parameter with the given name exists, the index does not exist
     * within the parameters, the object is not an instance of the class, or any of the argument checkers fail.
     * @implSpec If you defined a parameter as optional, instead use {@link #fetch(String, Class, CallProperties, Object, ArgumentChecker[])}
     * or {@link #fetchNullable(String, Class, CallProperties, Object, ArgumentChecker[])}
     * where you can pass a default value.
     * @implNote This relies on {@link #builtArguments} being populated via {@link #ImplementedFunction(FunctionBuilder)}.
     */
    @NotNull
    @SafeVarargs
    @Contract(pure = true)
    protected final <T> T fetch(String parameterName, @NotNull Class<T> clazz, @NotNull CallProperties callProperties, ArgumentChecker<T>... argsCheckers) throws InterpreterException {
        int i = builtArguments != null ? builtArguments.getIndexFromName(parameterName) : -1;
        if (i == -1)
            throw new InterpreterException.ImplementationException(
                    callProperties.scope(), callProperties.getLineNumber(),
                    "No parameter with the name \""+parameterName+"\" exists."
            );
        return fetch(i, clazz, callProperties, argsCheckers);
    }

    /**
     * Fetches a singular argument by parameter name. Where if the argument does not exist
     * (or {@code parameterName} isn't found in {@link #builtArguments}), the non-null default value will be returned.
     * @param parameterName The name of the parameter as defined in {@link Arguments}
     * @param clazz The class it's expected to be.
     * @param callProperties The function's call properties
     * @param defaultValue The non-null default value to use if: the argument input was not given,
     * the input was {@link TVoidValue}, or the parameter name wasn't found.
     * @param argsCheckers Argument checkers to check and throw after the cast succeeds.
     * @return The object at the resolved parameter index cast to {@link T}, or {@code defaultValue}
     * @param <T> The expected class of the object
     * @throws InterpreterException If the provided argument is not an instance of the class
     * or any of the argument checkers fail.
     * @implNote If you need {@code defaultValue} to be {@code null}, use
     * {@link #fetchNullable(String, Class, CallProperties, Object, ArgumentChecker[])} instead.
     */
    @NotNull
    @SafeVarargs
    @Contract(pure = true)
    protected final <T> T fetch(String parameterName, @NotNull Class<T> clazz, @NotNull CallProperties callProperties, @NotNull T defaultValue, ArgumentChecker<T>... argsCheckers) throws InterpreterException {
        int i = builtArguments != null ? builtArguments.getIndexFromName(parameterName) : -1;
        if (i == -1) return defaultValue;
        return fetch(i, clazz, callProperties, defaultValue, argsCheckers);
    }

    /**
     * Fetches a singular argument by parameter name, allowing {@code null} as the default fallback.
     * Where if the argument does not exist (or {@code parameterName} isn't found in {@link #builtArguments}),
     * {@code defaultValue} will be returned.
     * @param parameterName The name of the parameter as defined in {@link Arguments}
     * @param clazz The class it's expected to be.
     * @param callProperties The function's call properties
     * @param defaultValue The nullable default value to use if: the argument input was not given,
     * the input was {@link TVoidValue}, or the parameter name wasn't found.
     * @param argsCheckers Argument checkers to check and throw after the cast succeeds.
     * @return The object at the resolved parameter index cast to {@link T}, or {@code defaultValue} (which may be {@code null})
     * @param <T> The expected class of the object
     * @throws InterpreterException If the provided argument is not an instance of the class
     * or any of the argument checkers fail.
     * @implNote Useful when an omitted or {@link TVoidValue} optional parameter should map to Java {@code null}
     * rather than a non-null fallback.
     */
    @Nullable
    @SafeVarargs
    @Contract(pure = true)
    protected final <T> T fetchNullable(String parameterName, @NotNull Class<T> clazz, @NotNull CallProperties callProperties, @Nullable T defaultValue, ArgumentChecker<T>... argsCheckers) throws InterpreterException {
        int i = builtArguments != null ? builtArguments.getIndexFromName(parameterName) : -1;
        if (i == -1) return defaultValue;
        return fetch(i, clazz, callProperties, defaultValue, argsCheckers);
    }

}

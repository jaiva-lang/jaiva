package com.jaiva.interpreter.symbol.fn;

import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;
import com.jaiva.tokenizer.tokens.specific.TVoidValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The call properties given to a {@link BaseFunction} when {@link BaseFunction#call(CallProperties)}
 * is invoked.
 * @param tFuncCall The function call token. More than half the time this isn't needed to be used
 *                  unless you need some sort of introspection.
 * @param params The parsed parameters, depending on whether you aren't using {@link BaseFunction#evaluateParamsLater()}
 * @param config The interpreter configuration object which is the same for any scope.
 * @param scope The entire scope along with it's vfs
 * @author Lehlogonolo Poole
 */
public record CallProperties(
        TFuncCall tFuncCall,
        ArrayList<Object> params,
        IConfig<Object> config,
        Scope scope
) {

    /**
     * Wrapper around {@link TFuncCall#lineNumber} for any input that requires a line number
     * @return The line number where this function was invoked.
     */
    @Contract(pure = true)
    public int getLineNumber() {
        return tFuncCall.lineNumber;
    }

    /**
     * Constructs a new object that can be passed into {@link BaseFunction#checkParams(CheckParams)}
     * @return A new CheckParams object
     * @see BaseFunction#checkParams(CheckParams)
     */
    @NotNull
    @Contract(pure = true, value = "->new")
    public CheckParams toCheckParams() {
        return new CheckParams(tFuncCall, scope, params);
    }

    /**
     * Constructs a new {@link CallProperties} with the same properties but using
     * the passed in parameters
     * @param parsedParams The new parameters
     * @return A new CallProperties object
     */
    @NotNull
    @Contract("_ -> new")
    public CallProperties using(@NotNull ArrayList<Object> parsedParams) {
        return new CallProperties(tFuncCall, parsedParams, config, scope);
    }

    /**
     * Constructs a new {@code idk} value on the current line number.
     * @implNote This is a wrapper around {@link Token#voidValue(int)}
     * @return A new TVoidValue token that can be returned by functions or used otherwise.
     */
    @NotNull
    @Contract(value = "->new", pure = true)
    public TVoidValue idk() {
        return Token.voidValue(getLineNumber());
    }
}
package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.TParamsExtendable;
import com.jaiva.tokenizer.tokens.TStatement;
import com.jaiva.tokenizer.tokens.Token;

/**
 * Represents a throw error statement such as {@code cima "Error message!"}
 */
public final class TThrowError extends TokenDefault<TThrowError> implements TStatement, TParamsExtendable {
    /**
     * The error message to be thrown.
     */
    public Object errorMessage;

    /**
     * Constructor for TThrowError
     *
     * @param errorMessage The error message to be thrown.
     * @param ln           The line number.
     */
    public TThrowError(Object errorMessage, int ln) throws JaivaException {
        super("TThrowError", ln);
        this.errorMessage = errorMessage;
        json();
    }

    public void json() throws JaivaException {
        json.set("errorMessage", errorMessage);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TThrowError}
     */
    public Token<TThrowError> toToken() {
        return new Token<>(this);
    }

    @Override
    public TFuncCall get() {
        return checkObject(errorMessage);
    }
}

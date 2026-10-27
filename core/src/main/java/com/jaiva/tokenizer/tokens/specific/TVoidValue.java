package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.tokenizer.tokens.TAtomicValue;
import com.jaiva.tokenizer.tokens.TStatement;
import com.jaiva.tokenizer.tokens.Token;

/**
 * Represents a "null" value such as {@code maak name <- idk!} which is the same
 * as not defining a value at all.
 */
public final class TVoidValue extends TokenDefault<TVoidValue> implements TAtomicValue, TStatement {

    /**
     * Constructor for TVoidValue
     *
     * @param ln The line number.
     */
    public TVoidValue(int ln) {
        super("TVoidValue", ln);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TVoidValue}
     */
    public Token<TVoidValue> toToken() {
        return new Token<>(this);
    }

    @Override
    public String toString() {
        return "idk";
    }
}

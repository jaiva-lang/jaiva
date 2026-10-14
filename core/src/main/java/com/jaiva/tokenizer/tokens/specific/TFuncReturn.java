package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.TParamsExtendable;
import com.jaiva.tokenizer.tokens.TStatement;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;

/**
 * Represents a function return which can only be defined in a function (Duh)
 * such as {@code khulta 10!}
 */
public class TFuncReturn extends TokenDefault<TFuncReturn> implements TStatement, TParamsExtendable {
    /**
     * The value which the function should return.
     */
    public Object value;

    /**
     * Constructor for TFuncReturn
     *
     * @param value The value which the function should return.
     * @param ln    The line number.
     */
    public TFuncReturn(Object value, int ln) throws JaivaException {
        super("TFuncReturn", ln);
        this.value = value;
        json();
    }

    public void json() throws JaivaException {
        json.set("value", value);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TFuncReturn}
     */
    public Token<TFuncReturn> toToken() {
        return new Token<>(this);
    }

    @Override
    public TFuncCall get() {
        return checkObject(value);
    }
}

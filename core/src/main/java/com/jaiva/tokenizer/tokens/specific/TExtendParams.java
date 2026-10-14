package com.jaiva.tokenizer.tokens.specific;


import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.*;

import java.util.ArrayList;

/**
 * Represents a function call which is actually an extension of another function call's parameters
 * @implSpec This token isn't itself meant to be seen by the tokeniser as it gets
 * concatenated with the rest of the parameters of the previous function call
 */
public class TExtendParams extends TokenDefault<TExtendParams> implements TStatement {
    /**
     * The arguments of the extension1
     * <p>
     * This is an arraylist of objects which can be a TStatement, TFuncCall,
     * TVarRef, or a primitive type.
     * </p>
     */
    public ArrayList<Object> args; // can be a TStatement, TFuncCall, TVarRef, or a primitive type

    /**
     * Constructor for TExtendParams
     * @param args The arguments of the extension
     * @param ln   The line number.
     */
    public TExtendParams(ArrayList<Object> args, int ln) {
        super("TExtendParams", ln);
        this.args = args;
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TExtendParams}
     */
    public Token<TExtendParams> toToken() {
        return new Token<>(this);
    }
}

package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.TParamsExtendable;
import com.jaiva.tokenizer.tokens.TStatement;
import com.jaiva.tokenizer.tokens.Token;

import java.util.ArrayList;

/**
 * Represents a reassignment such as {@code x <- 10}
 * @implSpec you can actually also add new modifiers to the thing you're updating. such as
 * {@code [cow] param <- 100} or for the use case of adding a new modifier only {@code [const] param <- param}
 */
public final class TVarReassign extends TokenDefault<TVarReassign> implements TStatement, TParamsExtendable {
    /**
     * The new value of the variable.
     */
    public Object newValue;
    private ArrayList<String> newModifiers = new ArrayList<>();

    /**
     * Constructor for TVarReassign
     *
     * @param name  The name of the variable.
     * @param value The new value of the variable.
     * @param ln    The line number.
     */
    public TVarReassign(String name, Object value, ArrayList<String> newMods, int ln) throws JaivaException {
        super(name, ln);
        this.newValue = value;
        this.newModifiers = newMods;
        json();
    }

    /**
     * Constructor for TVarReassign
     *
     * @param name  The name of the variable.
     * @param value The new value of the variable.
     * @param ln    The line number.
     */
    public TVarReassign(Object name, Object value, int ln) throws JaivaException {
        super((String)name, ln);
        this.newValue = value;
        json();
    }

    public void json() throws JaivaException {
        json.set("value", newValue);
        json.set("newModifiers", newModifiers);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TVarReassign}
     */
    public Token<TVarReassign> toToken() {
        return new Token<>(this);
    }


    @Override
    public TFuncCall get() {
        return checkObject(newValue);
    }

    public ArrayList<String> getNewModifiers() {
        return newModifiers;
    }
}

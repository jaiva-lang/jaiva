package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.lang.Chars;
import com.jaiva.tokenizer.tokens.TReference;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;

/**
 * Represents a variable reference such as {@code name} or {@code age} or
 * {@code array[index]}.
 */
public class TVarRef extends TokenDefault<TVarRef> implements TReference {
    /**
     * The name of the variable being referenced.
     * <p>
     * This is an object due to the fact that it might itself be a TVarRef which
     * retrns a function, or another function that returns a function or any other
     * case like that.
     */
    public Object varName;
    /**
     * The index of the variable reference.
     * <p>
     * This is an object due to the fact that it might itself be a TVarRef which
     * retrns a function, or another function that returns a function or any other
     * case like that.
     */
    public Object index = null;
    /**
     * Indicates whether the variable reference is a length call.
     */
    private boolean getLength = false;
    /**
     * Indicates whether the function call spreads an array as arguments.
     */
    private boolean spreadArr = false;

    /**
     * Constructor for TVarRef
     *
     * @param name The name of the variable being referenced.
     * @param ln   The line number.
     */
    public TVarRef(Object name, int ln, boolean getLength, boolean spreadArr) throws JaivaException {
        super("TVarRef", ln);
        if (name instanceof String n) {
            n = n.endsWith(Chars.LENGTH_CHAR + "") ? n.substring(0, n.length() - 1) : n;
            if (n.endsWith(Chars.SPREAD))
                n = n.substring(0, n.length() - Chars.SPREAD.length());
            this.varName = n;
        } else
            this.varName = name;
        this.getLength = getLength;
        this.spreadArr = spreadArr;
        json();
    }

    /**
     * Constructor for TVarRef
     *
     * @param name      The name of the variable being referenced.
     * @param index     The index of the variable reference.
     * @param ln        The line number.
     * @param getLength Indicates whether the variable reference is a length call.
     */
    public TVarRef(Object name, Object index, int ln, boolean getLength) throws JaivaException {
        super("TVarRef", ln);
        this.index = index;
        if (name instanceof String n)
            this.varName = n.endsWith(Chars.LENGTH_CHAR + "") ? n.substring(0, n.length() - 1) : n;
        else
            this.varName = name;
        this.getLength = getLength;
        json();
    }

    public TVarRef(Object name, Object index, int ln, boolean getLength, boolean spreadArr) throws JaivaException {
        super("TVarRef", ln);
        this.index = index;
        if (name instanceof String n) {
            n = n.endsWith(Chars.LENGTH_CHAR + "") ? n.substring(0, n.length() - 1) : n;
            if (n.endsWith(Chars.SPREAD))
                this.varName = n.substring(0, n.length() - Chars.SPREAD.length());
            this.varName = n;
        } else
            this.varName = name;
        this.getLength = getLength;
        this.spreadArr = spreadArr;
        json();
    }

    public void json() throws JaivaException {
        json.set("varName", varName);
        json.set("getLength", getLength);
        json.set("spreadArr", spreadArr);
        json.set("index", index);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TVarRef}
     */
    public Token<TVarRef> toToken() {
        return new Token<>(this);
    }

    @Override
    public boolean getLength() {
        return getLength;
    }

    @Override
    public boolean getSpreadArr() {
        return spreadArr;
    }
}

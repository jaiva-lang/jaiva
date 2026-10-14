package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.TParamsExtendable;
import com.jaiva.tokenizer.tokens.TReference;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;

import java.util.ArrayList;

/**
 * Represents a function call such as {@code func1(10, 20)} or {@code func2()}
 * or {@code func4(10, 20)!}. Any, if not ALL
 * function calls are possible.
 */
public class TFuncCall extends TokenDefault<TFuncCall> implements TReference, TParamsExtendable {
    /**
     * The name of the function being called.
     * <p>
     * This is an object due to the fact that it might itself be a TVarRef which
     * retrns a function, or another function that returns a function or any other
     * case like that.
     */
    public Object functionName;
    /**
     * The arguments of the function call.
     * <p>
     * This is an arraylist of objects which can be a TStatement, TFuncCall,
     * TVarRef, or a primitive type.
     */
    public ArrayList<Object> args; // can be a TStatement, TFuncCall, TVarRef, or a primitive type
    /**
     * Indicates whether the function call is a length call.
     */
    private boolean getLength = false;
    /**
     * Indicates whether the function call spreads an array as arguments.
     */
    private boolean spreadArr = false;

    /**
     * Constructor for TFuncCall
     *
     * @param name The name of the function being called.
     * @param args The arguments of the function call.
     * @param ln   The line number.
     * @param getL Indicates whether the function call is a length call.
     */
    public TFuncCall(Object name, ArrayList<Object> args, int ln, boolean getL) throws JaivaException {
        super("TFuncCall", ln);
        this.functionName = name;
        this.args = args;
        this.getLength = getL;
        json();
    }

    /**
     * Constructor for TFuncCall with spreadArr
     * @param name The name of the function being called.
     * @param args The arguments of the function call.
     * @param ln  The line number.
     * @param getL Indicates whether the function call is a length call.
     * @param spreadArr Indicates whether the function call spreads an array as arguments.
     */
    public TFuncCall(Object name, ArrayList<Object> args, int ln, boolean getL, boolean spreadArr) throws JaivaException {
        super("TFuncCall", ln);
        this.functionName = name;
        this.args = args;
        this.getLength = getL;
        this.spreadArr = spreadArr;
        json();
    }

    public void json() throws JaivaException {
        json.set("functionName", functionName);
        json.set("getLength", getLength);
        json.set("spreadArr", spreadArr);
        json.set("args", args.toArray());
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TFuncCall}
     */
    public Token<TFuncCall> toToken() {
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

    @Override
    public boolean endsWithFuncCall() {
        return true;
    }

    @Override
    public TFuncCall get() {
        return this;
    }
}

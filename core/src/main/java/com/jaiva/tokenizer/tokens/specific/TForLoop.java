package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.TConditional;
import com.jaiva.tokenizer.tokens.TConstruct;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a for loop, both with an iterator and a condition
 * {@code colonize i <- 0 | i < 10 | + -> ... <~} and a for each loop which
 * iterates over arrays {@code colonize el with array -> ... <~}
 */
public class TForLoop extends TokenDefault<TForLoop> implements TConstruct, TConditional {
    /**
     * The variable used in the for loop.
     */
    public TokenDefault variable;
    /**
     * The array variable used in the for loop.
     */
    public TVarRef array;
    /**
     * The condition of the for loop.
     */
    public Object condition;
    /**
     * The increment of the for loop.
     */
    public Object increment;
    /**
     * The body of the for loop.
     */
    public TCodeblock body;

    /**
     * Constructor for TForLoop (for iterators)
     *
     * @param variable  The variable used in the for loop.
     * @param condition The condition of the for loop.
     * @param increment The increment of the for loop.
     * @param body      The body of the for loop.
     * @param ln        The line number.
     */
    public TForLoop(TokenDefault variable, Object condition, Object increment, TCodeblock body, int ln) throws JaivaException {
        super("TForLoop", ln);
        this.variable = variable;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
        json();
    }

    /**
     * Constructor for TForLoop (for each loop)
     *
     * @param variable The variable used in the for loop.
     * @param array    The array variable used in the for loop.
     * @param body     The body of the for loop.
     * @param ln       The line number.
     */
    public TForLoop(TokenDefault variable, TVarRef array, TCodeblock body, int ln) throws JaivaException {
        super("TForLoop", ln);
        this.variable = variable;
        this.array = array;
        this.body = body;
        json();
    }

    public void json() throws JaivaException {
        json.set("variable", variable);
        json.set("arrayVariable", array);
        json.set("condition", condition);
        json.set("increment", increment);
        json.set("body", body);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TForLoop}
     */
    public Token<TForLoop> toToken() {
        return new Token<>(this);
    }

    @Override
    public Object getConditionToken() {
        return condition;
    }

    @Override
    public ArrayList<TCodeblock> getCodeBlocks() {
        return new ArrayList<>(List.of(body));
    }
}

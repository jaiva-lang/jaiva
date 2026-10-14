package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.TConditional;
import com.jaiva.tokenizer.tokens.TConstruct;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a while loop such as {@code nikhil (i > 10) -> ... <~}
 */
public class TWhileLoop extends TokenDefault<TWhileLoop> implements TConstruct, TConditional {
    /**
     * The condition of the while loop.
     */
    public Object condition;
    /**
     * The body of the while loop.
     */
    public TCodeblock body;

    /**
     * Constructor for TWhileLoop
     *
     * @param condition The condition of the while loop.
     * @param body      The body of the while loop.
     * @param ln        The line number.
     */
    public TWhileLoop(Object condition, TCodeblock body, int ln) throws JaivaException {
        super("TWhileLoop", ln);
        this.condition = condition;
        this.body = body;
        json();
    }

    public void json() throws JaivaException {
        json.set("condition", condition);
        json.set("body", body);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TWhileLoop}
     */
    public Token<TWhileLoop> toToken() {
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

package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.TAtomicValue;
import com.jaiva.tokenizer.tokens.TConditional;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;

/**
 * Represents a ternary expression such as
 * {@code condition => expr1 however expr2}
 */
public class TTernary extends TokenDefault<TTernary> implements TAtomicValue, TConditional {
    /**
     * The condition of the ternary expression.
     */
    public Object condition;
    /**
     * The expression if the condition is true.
     */
    public Object trueExpr;
    /**
     * The expression if the condition is false.
     */
    public Object falseExpr;

    /**
     * Constructor for TTernary
     *
     * @param condition The condition of the ternary expression.
     * @param trueExpr  The expression if the condition is true.
     * @param falseExpr The expression if the condition is false.
     * @param ln        The line number.
     */
    public TTernary(Object condition, Object trueExpr, Object falseExpr, int ln) throws JaivaException {
        super("TTernary", ln);
        this.condition = condition;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
        json();
    }

    public void json() throws JaivaException {
        json.set("condition", condition);
        json.set("trueExpr", trueExpr);
        json.set("falseExpr", falseExpr);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TTernary}
     */
    public Token<TTernary> toToken() {
        return new Token<>(this);
    }

    @Override
    public Object getConditionToken() {
        return condition;
    }
}

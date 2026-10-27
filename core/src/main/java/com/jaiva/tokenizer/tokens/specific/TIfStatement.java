package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.*;

import java.util.ArrayList;

/**
 * Represents an if statement such as {@code if (i > 10) -> ... <~}, also can
 * have chains of else ifs and a last else if.
 * {@code if (i > 10) -> ... <~ mara if (i > 4) -> ... <~ mara -> ... <~}
 * <p>
 * If this is in an else if of a base if statement, it will not contain an else
 * body nor its own chain of elseIfs. only the original if contaisn all the else
 * ifs and the last else block.
 */
public final class TIfStatement extends TokenDefault<TIfStatement> implements TConstruct, TConditional, TParamsExtendable {
    /**
     * The condition of the if statement.
     */
    public Object condition;
    /**
     * The body of the if statement.
     */
    public TCodeblock body;
    /**
     * The else body of the if statement.
     */
    public TCodeblock elseBody = null;
    /**
     * The chain of else if statements.
     */
    public ArrayList<TIfStatement> elseIfs = new ArrayList<>();

    private boolean shorthand = false;

    /**
     * Constructor for TIfStatement
     *
     * @param condition The condition of the if statement.
     * @param body      The body of the if statement.
     * @param ln        The line number.
     */
    public TIfStatement(Object condition, TCodeblock body, int ln) throws JaivaException {
        super("TIfStatement", ln);
        this.condition = condition;
        this.body = body;
        json();
    }

    /**
     * Appends the provided else if body to the current token.
     *
     * @param body the TIfStatement representing the else if body to be appended
     */
    public void appendElseIf(TIfStatement body) throws JaivaException {
        if (elseIfs == null) {
            elseIfs = new ArrayList<>();
            json();
        }
        elseIfs.add(body);
    }

    /**
     * Appends the provided else body to the current token.
     *
     * @param elseBody the TCodeblock representing the else body to be appended
     */
    public void appendElse(TCodeblock elseBody) {
        this.elseBody = elseBody;
    }

    public void json() throws JaivaException {
        json.set("condition", condition);
        json.set("body", body);
        json.nullSet("elseIfs", elseIfs);
        json.nullSet("elseBody", elseBody);
        json.set("isShorthand", shorthand);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TIfStatement}
     */
    public Token<TIfStatement> toToken() {
        return new Token<>(this);
    }

    @Override
    public Object getConditionToken() {
        return condition;
    }

    @Override
    public ArrayList<TCodeblock> getCodeBlocks() {
        ArrayList<TCodeblock> blocks = new ArrayList<>();
        blocks.add(body);
        if (elseBody != null)
            blocks.add(elseBody);
        if (elseIfs != null)
            elseIfs.stream()
                    .map(t -> t.body)
                    .forEach(blocks::add);
        return blocks;
    }

    public void setShorthand(boolean shorthand) {
        this.shorthand = shorthand;
        json.set("isShorthand", shorthand);
    }

    @Override
    public TFuncCall get() {
        if (!shorthand) return null;
        return checkObject(getCodeBlocks().getFirst().lines.getFirst().value());
    }
}

package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.errors.TokenizerException;
import com.jaiva.tokenizer.tokens.TAtomicValue;
import com.jaiva.tokenizer.tokens.TParamsExtendable;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.jaiva.utils.Find;
import com.jaiva.utils.cd.ContextDispatcher;
import com.jaiva.utils.generic.LeastImportantOperator;

/**
 * Represents a statement such as {@code 10 + 1} or {@code true && false}
 * This class usually isn't used directly, but rather as a part of another
 * instance.
 */
public class TExpression extends TokenDefault<TExpression> implements TAtomicValue, TParamsExtendable {
    /**
     * The left hand side of the statement.
     * <p>
     * This is an object due to the fact that it might itself be a TVarRef which
     * retrns a function, or another function that returns a function or any other
     * case like that.
     */
    public Object lHandSide = "null";
    /**
     * The operator of the statement.
     */
    public String op;
    /**
     * The right hand side of the statement.
     * <p>
     * This is an object due to the fact that it might itself be a TVarRef which
     * retrns a function, or another function that returns a function or any other
     * case like that.
     */
    public Object rHandSide = "null";
    /**
     * The statement as a string.
     */
    public String statement;
    /**
     * 0 = boolean logic |
     * 1 = int arithmetic
     */
    public int statementType;

    /**
     * Constructor for TExpression
     *
     * @param ln The line number.
     */
    public TExpression(int ln) {
        super("TExpression", ln);
    }

    /**
     * Lie to the interpreter by changing the statement if this happened.
     *
     * @param s The statement to handle.
     * @return The handled statement.
     */
    public static Object syntaxSugar(Object s) throws TokenizerException.MalformedSyntaxException {
        if (s instanceof TExpression statement) {
            if (statement.rHandSide == null && statement.op.equals("?")) {
                statement.rHandSide = Token.voidValue(statement.lineNumber);
                statement.op = "=";
            } else if (statement.lHandSide == null && statement.op.equals("-")) {
                // turns a statement like [-rhs] into [-1 * rhs] (since rhs can be anything even if its a number already)
                statement.lHandSide = -1;
                statement.op = "*";
            } else if (statement.op.equals("'")) {
                // turns a statement like [lhs'] into [lhs = false] (which negates whatever lhs resolves to)
                if (statement.rHandSide != null)
                    throw new TokenizerException.MalformedSyntaxException(
                            "So like brother. ' means logical NOT. its not supposed to have a right hand side brother.",
                            statement.lineNumber);
                statement.rHandSide = false;
                statement.op = "=";
            } else if (statement.op.equals(";")) {
                // turns a statement like [lhs;] into [lhs = true] (Fuck you C)
                if (statement.rHandSide != null)
                    throw new TokenizerException.MalformedSyntaxException(
                            "Okay so. ; is actually the opposite of logical NOT. It's an operator. Not a line terminator. "
                            + "How could you possibly think that??",
                            statement.lineNumber
                    );
                statement.rHandSide = true;
                statement.op = "=";
            }
            return statement;
        }
        return s;
    }

    public void json() throws JaivaException {
        json.set("lhs", lHandSide);
        json.set("op", op);
        json.set("rhs", rHandSide);
        json.set("statement", statement);
        json.set("statementType", statementType);
    }

    /**
     * Parses a given string. This assumes you've already used braces to
     * indicate the correct order of operations.
     * <p>
     * NOTE : This method will call ContextDispatcher and if needed may switch to
     * using processContext if its instead function call or variable call.
     *
     * @param statement The statement to parse.
     */
    public Object parse(String statement) throws JaivaException {
        statement = statement.trim();

        if (statement.isEmpty()) {
            return null;
        }
        ContextDispatcher d = new ContextDispatcher(statement);
        if (d.getDeligation() == ContextDispatcher.To.PROCESS_CONTENT) {
            return Token.processContext(statement, lineNumber);
        }
        this.statement = statement;

        int lastBraceIndex = Find.lastOuterMostBracePairIndex(statement);
        if ((statement.startsWith("(") && statement.endsWith(")")) && lastBraceIndex == 0) {
            return parse(statement.substring(1, statement.length() - 1).trim());
        }

        LeastImportantOperator info = Find.leastImportantOperator(statement);
        if (info.index == -1) {
            // no operator found, so its a single value
            return Token.processContext(statement, lineNumber);
        }
        if (info.op.equals("=") && statement.charAt(info.index - 1) == '!') {
            info.index--;
            info.op = "!=";
        }
        if (info.op.equals("=") && statement.charAt(info.index - 1) == '<') {
            info.index--;
            info.op = "<=";
        }
        if (info.op.equals("=") && statement.charAt(info.index - 1) == '>') {
            info.index--;
            info.op = ">=";
        }

        lHandSide = syntaxSugar(new TExpression(lineNumber).parse(statement.substring(0, info.index).trim()));
        this.op = info.op.trim();
        rHandSide = syntaxSugar(
                new TExpression(lineNumber).parse(statement.substring(info.index + info.op.length()).trim()));
        statementType = info.tStatementType;
        TExpression expression = ((TExpression) syntaxSugar(this));
        expression.json();
        return expression.toToken();
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TExpression}
     */
    public Token<TExpression> toToken() {
        return new Token<>(this);
    }

    @Override
    public TFuncCall get() {
        return checkObject(rHandSide);
    }
}

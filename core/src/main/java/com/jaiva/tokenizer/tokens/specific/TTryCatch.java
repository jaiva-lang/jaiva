package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.TConstruct;
import com.jaiva.tokenizer.tokens.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a try-catch statement such as
 * {@code zama zama -> ... <~ chaai -> ... <~}
 */
public final class TTryCatch extends TokenDefault<TTryCatch> implements TConstruct {
    /**
     * The try block of the try-catch statement.
     */
    public TCodeblock tryBlock;
    /**
     * The catch block of the try-catch statement.
     */
    public TCodeblock catchBlock;

    /**
     * Constructor for TTryCatchStatement
     *
     * @param tryBlock The try block of the try-catch statement.
     * @param ln       The line number.
     */
    public TTryCatch(TCodeblock tryBlock, int ln) throws JaivaException {
        super("TTryCatchStatement", ln);
        this.tryBlock = tryBlock;
        json();
    }

    /**
     * Appends the provided catch block to the current token.
     *
     * @param catchBlock the TCodeblock representing the catch block to be appended
     */
    public void appendCatchBlock(TCodeblock catchBlock) throws JaivaException {
        this.catchBlock = catchBlock;
        json();
    }

    public void json() throws JaivaException {
        json.set("try", tryBlock);
        json.nullSet("catch", catchBlock);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TTryCatch}
     */
    public Token<TTryCatch> toToken() {
        return new Token<>(this);
    }

    @Override
    public ArrayList<TCodeblock> getCodeBlocks() {
        return new ArrayList<>(List.of(tryBlock, catchBlock));
    }
}

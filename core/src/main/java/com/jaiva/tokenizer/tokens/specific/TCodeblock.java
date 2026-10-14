package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;

import java.util.ArrayList;

/**
 * Represents a code block such as {@code -> maak x <- 10; maak y <- 20! <~}
 * This class usually isn't used directly, but rather as a part of another
 * instance
 */
public class TCodeblock extends TokenDefault<TCodeblock> {
    /**
     * The lines of code in the code block.
     */
    public ArrayList<Token<?>> lines;
    /**
     * The line number where the ending delimiter of the code block is found. (<~)
     */
    public int lineNumberEnd;

    /**
     * Constructor for TCodeblock
     *
     * @param lines The lines of code in the code block.
     * @param ln    The line number where the code block starts.
     * @param endLn The line number where the code block ends.
     */
    public TCodeblock(ArrayList<Token<?>> lines, int ln, int endLn) throws JaivaException {
        super("TCodeblock", ln);
        this.lines = lines;
        this.lineNumberEnd = endLn;
        json();
    }

    public void json() throws JaivaException {
        json.set("lineEnd", lineNumberEnd);
        json.set("lines", lines);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TCodeblock}
     */
    public Token<TCodeblock> toToken() {
        return new Token<>(this);
    }
}

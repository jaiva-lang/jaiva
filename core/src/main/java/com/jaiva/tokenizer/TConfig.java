package com.jaiva.tokenizer;

import com.jaiva.Config;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TIfStatement;
import com.jaiva.utils.generic.BlockChain;
import com.jaiva.utils.generic.MultipleLinesOutput;

import java.util.ArrayList;


/**
 * TConfig class is a configuration class for the Jaiva tokenizer that holds the
 * path to the Jaiva source
 * code.
 * <p>
 */
public class TConfig extends Config {

    public final Flags flags = new Flags();

    /**
     * File origin. It can be a String or Path.
     */
    private final Object fileOrigin;

    /**
     * The constructor for the TConfig class.
     * <p>
     * This constructor is used to set the path to the Jaiva source code.
     */
    public TConfig(Object fileOrigin) {
        super();
        this.fileOrigin = fileOrigin;
    }

    public Object getFileOrigin() {
        return fileOrigin;
    }


    /**
     * Flags class is a utility class that holds static boolean flags used for
     * configuring the behavior of the tokenizer.
     */
    public static class Flags {
        /**
         * Flag used by the tokeniser to indicate, that the subsequent handling of the "imaginary" block
         * made by {@code sif} statement, being its {@link com.jaiva.tokenizer.tokens.specific.TIfStatement} representation
         * as a 3 line statement, should ignore the canonical line number which the normal handling loop keeps track of to instead
         * have the 3 line statement's line number be on the same line.
         * <p>
         *     This is set by {@link Tokenizer#readLine(String, String, MultipleLinesOutput, BlockChain, int, TConfig)}
         *     when handling a {@code sif} such that when it passes on the creation of the {@link TIfStatement}
         *     to {@code Tokenizer#processBlockLines(boolean, String, MultipleLinesOutput, String, ArrayList, String, String[], Token, int, TConfig)}
         *     it can safely set and reset said line number endings when it itself calls readLine for the body
         *     of the if statement.
         * </p>
         * <p>
         *     This is more jsut a bonus, but its also used at the end of all the above handling right before returning
         *     the token to set {@link TIfStatement#setShorthand(boolean)}
         * </p>
         */
        public boolean SHORTHAND_IF = false;
        /**
         * This flag is used by the tokenizer, to indicate to it that the line while being parsed by {@link Tokenizer#readLine(String, String, MultipleLinesOutput, BlockChain, int, TConfig)} should instead of trimming it's contents, preserve it.
         * <p>
         *     This is specifically set by Tokenizer#processBlockLines, when it recursively calls readLine by giving it the entire block to read.
         *     However, new lines need to be preserved for accurate line numbering. This flag is used to tell the tokenizer to not trim anything.
         *     it is, however, reset to false after the line is read.
         * </p>
         */
        public boolean SKIP_READLINE_TRIM = false;
    }
}

package com.jaiva.tokenizer;

import com.jaiva.Config;
import com.jaiva.utils.generic.BlockChain;
import com.jaiva.utils.generic.MultipleLinesOutput;


/**
 * TConfig class is a configuration class for the Jaiva tokenizer that holds the
 * path to the Jaiva source
 * code.
 * <p>
 */
public class TConfig extends Config {

    public Flags flags = new Flags();

    private Object fileOrigin;

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

package com.jaiva.errors;

import com.jaiva.tokenizer.tokens.specific.TIfStatement;
import io.github.yetnt.utils.builders.AnsiColour;

/**
 * Base Syntax Error Exception.
 */
public class TokenizerException extends JaivaException {
    private static final long serialVersionUID = 1L;

    /**
     * Defualt constructor
     * 
     * @param message THe message.
     */
    public TokenizerException(int lineNumber, String message) {
        super(AnsiColour.print(message, AnsiColour.FONT.BOLD, AnsiColour.FONT.ITALIC, AnsiColour.FORE.RED), lineNumber);
    }

    /**
     * Exception to throw when we received malformed input.
     */
    public static class MalformedSyntaxException extends TokenizerException {
        private static final long serialVersionUID = 1L;

        /**
         * Constructor to use with a custom message.
         * 
         * @param message    The message.
         * @param lineNumber The line this error occured on.
         */
        public MalformedSyntaxException(String message, int lineNumber) {
            super(lineNumber, message);
        }
    }

    /**
     * Exception to throw for any "catch all" cases that cannot actually happen but
     * need to have an error thrown as the user can input really anything they want
     * and a switch-case/ if-else chain might not catch it, but the default:/last
     * else block might catch it, then throw this error.
     * <p>
     * Essentialy a fallback exception.
     */
    public static class CatchAllException extends TokenizerException {

        private static final long serialVersionUID = 1L;

        /**
         * Represents a generic exception that is not expected to occur.
         * This exception is used as a fallback for unexpected errors.
         *
         * @param message    A detailed message describing the error.
         * @param lineNumber The line number where the error occurred.
         */
        public CatchAllException(String message, int lineNumber) {
            super(lineNumber, "This error shouldn't happen... (" + message + ")");
        }
    }

    /**
     * Exception to throw when (via type inference) expect the type of some
     * expression or whatever to be a specific type, but it doesnt resolve to that.
     * An example would be the statement {@code if (10 + 3 / 5 = 2)} gets inferred
     * as a boolean as we have a boolean operation. Where is {@code if (10 + 3)}
     * would error as {@link TIfStatement} needs a boolean.
     */
    public static class TypeMismatchException extends TokenizerException {

        private static final long serialVersionUID = 1L;

        /**
         * Exception thrown to indicate a type mismatch error in the code.
         * 
         * @param message    A detailed message describing the type mismatch error.
         * @param lineNumber The line number in the source code where the error
         *                   occurred.
         */
        public TypeMismatchException(String message, int lineNumber) {
            super(lineNumber, message);
        }
    }

    /**
     * Exception to throw when invalid Jaiva Documentation is written for a given symbol.
     * It will throw the line number of the symbol with the malformed JDoc as we can never
     * know the line number of a comment.
     */
    public static class MalformedJDocException extends  TokenizerException {
        private static final long serialVersionUID = 1L;

        /**
         * Exception thrown to indicate a symbol's malformed JDoc
         * @param lineNumber the Line number
         */
        public MalformedJDocException(int lineNumber) {
            super(lineNumber, "This symbol contains invalid JDoc code");
        }

        public MalformedJDocException(int lineNumber, String message) {
            super(lineNumber, message);
        }
    }

    /**
     * Exception to throw when a file or directory cannot be found.
     */
    public static class FileOrDirectoryNotFoundException extends TokenizerException {

        private static final long serialVersionUID = 1L;

        /**
         * Constructs a new exception indicating a missing file or directory.
         *
         * @param message    A detailed message describing the error.
         * @param lineNumber The line number where the error was detected.
         */
        public FileOrDirectoryNotFoundException(String message, int lineNumber) {
            super(lineNumber, message);
        }
    }

}
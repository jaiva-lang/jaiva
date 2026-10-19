package com.jaiva.interpreter.symbol.continuous;

public enum CFuncInput {
    READ(
            "read",
            "Key of the reader function if this continuous function closes over a read/input operation",
            "C_INPUT"
    ),
    WRITE(
            "write",
            "Key of the output function if this continuous function closes over a write/output operation",
            "C_OUTPUT"
    ),
    FLUSH(
            "flush",
            "(Only associated with continuous output functions) Key of the function which " +
                    "flushes the output if the stream can be flushed (In Java terms, is Buffered.)"
    ),
    EOF(
            "eof",
            "(Only associated with continuous input functions) Key of the function which " +
                    "signals that an input continuous function has reached the end of it's data. " +
                    "Specific semantics depend on implementations as not all input continuous functions " +
                    "may make use of this.", "C_END_OF_FILE"
    ),
    CLOSE(
            "close",
            "Key of the function which closes the continuous function, effectively ending it and " +
                    "freeing resources. This always needs to be called to avoid resource leaks.",
            "C_END", "C_FINISH"
    ),
    CLOSED(
            "closed",
            "Key of the function which sets a specific operation to happen for when a given \"something\" is closed.",
            "C_ENDED", "C_FINISHED"
    );

    private final String key;
    private final String doc;
    private final String[] aliases;
    private final char keyChar;
    private CFuncInput(String key, String doc, String ...aliases) {
        this.key = key;
        this.keyChar = key.charAt(0);
        this.doc = doc;
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDoc() {
        return doc;
    }

    public char getKeyChar() {
        return keyChar;
    }

    public String getKey() {
        return key;
    }

    public static CFuncInput fromChar(char c) {
        for (CFuncInput input : CFuncInput.values()) {
            if (input.keyChar == c) {
                return input;
            }
        }
        return null;
    }

    public static boolean isValid(String input) {
        for (CFuncInput input1 : CFuncInput.values()) {
            if (input.equals(input1.getKey()) || (input.charAt(0) == input1.getKeyChar())) {
                return true;
            }
        }
        return false;
    }

}

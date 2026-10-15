package com.jaiva.interpreter;

import com.jaiva.errors.InterpreterException;
import org.jetbrains.annotations.NotNull;

public final class ScopeConfig {
    /**
     * Flags defines whether all symbols defined in this scope are frozen
     * after creation.
     */
    private boolean freezeAll = false;
    /**
     * If true, all warnings elevate to errors. This is mutually exclusive with {@link ScopeConfig#supressWarnings}
     */
    private boolean elevateWarnings = false;
    /**
     * If true, all warnings aren't logged to the console. This is mutually exclusive with {@link ScopeConfig#elevateWarnings}
     */
    private boolean supressWarnings = false;

    public ScopeConfig() {
    }

    public void set(int lineNumber, Scope scope, @NotNull String ...strings) throws InterpreterException.WtfAreYouDoingException {
        for (String s : strings) {
            switch (s.toLowerCase()) {
                case "freezeall", "constant", "freeze": freezeAll = true; break;
                case "elevatewarnings", "ew", "warningsarebad": elevateWarnings = true; break;
                case "suppresswarnings", "sw", "fuckwarnings": supressWarnings = true; break;
                case "strict": freezeAll = true; elevateWarnings = true; break;
            }
        }
        if (elevateWarnings && supressWarnings) {
            throw new InterpreterException.WtfAreYouDoingException(scope, "how the hell are you trying to suppress and elevate warnings?", lineNumber);
        }
    }

    public boolean freezeAll() {
        return freezeAll;
    }

    public boolean elevateWarnings() {
        return elevateWarnings;
    }

    public boolean suppressWarnings() {
        return supressWarnings;
    }
}

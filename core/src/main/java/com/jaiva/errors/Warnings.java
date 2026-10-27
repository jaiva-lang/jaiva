package com.jaiva.errors;

import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.runtime.IConfig;
import com.yetnt.utils.builders.AnsiColour;

public final class Warnings {

    public static class Warning {
        Scope scope;
        String message;
        int lineNumber;
        public Warning(Scope scope, String message, int lineNumber) {
            this.scope = scope;
            this.message = message;
            this.lineNumber = lineNumber;
            
        }

        public Scope getScope() {
            return scope;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getMessage() {
            return message;
        }
    }

    private Warnings() {

    }

    public static void println(int ln, String message, Scope scope, IConfig<Object> config) throws InterpreterException.NoWarningsException {
        if (scope.getConfig().suppressWarnings()) return;
        if (scope.getConfig().elevateWarnings())
            throw new InterpreterException.NoWarningsException(scope, ln, message);
        else {
            if (!config.isStreamer()) {
                System.out.println(AnsiColour.print("[WARNING: line " + ln + "]", AnsiColour.FONT.BOLD, AnsiColour.BACK.BRIGHT_WHITE, AnsiColour.FORE.YELLOW) + " " + AnsiColour.print(message, AnsiColour.FORE.BRIGHT_YELLOW));
            }
            config.addWarning(
                    new Warning(
                            scope, message, ln
                    )
            );
        }
    }
}

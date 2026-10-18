package com.jaiva.errors;

import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.Globals;
import com.jaiva.interpreter.runtime.IConfig;
import com.yetnt.utils.builders.AnsiColour;

/**
 * Generic loading exceptions usually library related where we might or might not have a line number
 */
public class LoadException extends JaivaException {
    public LoadException(String message) {
        this(message, -1);
    }
    public LoadException(String message, int lineNumber) {
        super(AnsiColour.print(message, AnsiColour.FONT.BOLD, AnsiColour.FONT.ITALIC, AnsiColour.FORE.BRIGHT_RED), lineNumber);
    }

    /**
     * Trying to fetch the path or description from the library's symbols.
     */
    public static class LibraryMetadataException extends LoadException {
        public LibraryMetadataException(String message, int ln) {
            super("Failed to fetch metadata from library, " + message, ln);
        }
    }

    /**
     * Exception for when external libraries do not confirm to annotation rules.
     */
    public static class LibraryAnnotationException extends LoadException {
        public LibraryAnnotationException(Class<?> clazz) {
            super(clazz.getCanonicalName() + " does not have the required library annotation .(use @PublicLibrary)");
        }
        public LibraryAnnotationException(String message) {
            super(message);
        }
    }

    /**
     * When {@code BaseLibrary#instantiate(Class, IConfig, Globals)} runs into reflection issues.
     */
    public static class InvalidLibraryClassException extends LoadException {
        public InvalidLibraryClassException(Class<?> clazz, String message) {
            super("Cannot load the " + clazz.getName() + " library: " + AnsiColour.printInline(message, AnsiColour.FORE.RED));
        }
        public InvalidLibraryClassException(Class<?> clazz, Exception e) {
            super("Cannot load the " + clazz.getName() + " library: " + AnsiColour.printInline(e.getMessage(), AnsiColour.FORE.RED));
        }
    }

    /**
     * When caling {@link BaseLibrary#getVfs(IConfig, Globals)} with a library that has export promises.
     */
    public static class ExportException extends LoadException {
        public ExportException(Class<?> clz, String message) {
            super("Failed to export symbols for " + clz.getName() + ": " + message);
        }
    }

    /**
     * Trying to fetch the path or description from the library's symbols.
     */
    public static class LibraryLikeGetException extends LoadException {
        public LibraryLikeGetException(String message) {
            super(message);
        }
    }
}

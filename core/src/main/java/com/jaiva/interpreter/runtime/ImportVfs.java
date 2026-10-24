package com.jaiva.interpreter.runtime;

import java.util.List;

public final class ImportVfs {
    public boolean active;               // true if import mode is on
    public List<String> symbolsToImport; // null or empty = import all

    public ImportVfs(boolean active, List<String> symbolsToImport) {
        this.active = active;
        this.symbolsToImport = symbolsToImport;
    }

    public ImportVfs(boolean active) {
        this.active = active;
        this.symbolsToImport = null;
    }

    /**
     * Determines if a given symbol should be imported based on the current import settings.
     * <ul>
     *     <li>If import mode is inactive, all symbols are imported. And this method returns true.</li>
     *     <li>If import mode is active but no specific symbols are listed, all symbols are imported.</li>
     *     <li>If import mode is active and specific symbols are listed, only those symbols are imported.</li>
     * </ul>
     * @param symbol The name of the symbol to check for import.
     * @return true if the symbol should be imported, false otherwise.
     */
    public boolean shouldImport(String symbol) {
        return !active || symbolsToImport == null || symbolsToImport.isEmpty() || symbolsToImport.contains(symbol);
    }
}

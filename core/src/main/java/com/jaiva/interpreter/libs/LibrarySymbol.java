package com.jaiva.interpreter.libs;

import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.libs.global.Globals;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A library symbol is simply a thin wrapper over a {@link Symbol}, (function/variable) such that
 * a {@link BaseLibrary} when calling {@link BaseLibrary#getVfs(IConfig, Globals)} can map
 * multiple keys to a single {@link Symbol}
 * @author Lehlogonolo Poole
 * @see Vfs
 * @see BaseLibrary
 * @see Symbol
 * @see BaseFunction
 * @see BaseVariable
 */
public final class LibrarySymbol {
    private final Symbol symbol;
    private final ArrayList<String> aliases;

    /**
     * Default Constructor
     * @param symbol The symbol to add
     * @param aliases The list of aliases.
     * @implSpec A symbol's canonical name is enforced to be the part of {@link Vfs} by this
     * constructor adding the symbol name to the alias list if it isn't already in the list.
     */
    public LibrarySymbol(
            Symbol symbol,
            String... aliases
    ) {
        this.symbol = symbol;
        this.aliases = new ArrayList<>(Arrays.asList(aliases));
        if (!this.aliases.contains(symbol.name)) // symbol name is itself an alias
            this.aliases.addFirst(symbol.name);
    }

    /**
     * Gets the symbol
     * @return The Symbol
     */
    public Symbol symbol() {
        return symbol;
    }

    /**
     * Gets the aliases
     * @return The aliases
     */
    public List<String> aliases() {
        return aliases;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LibrarySymbol) obj;
        return Objects.equals(this.symbol, that.symbol) &&
                this.aliases.equals(that.aliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, aliases);
    }

    @Override
    public String toString() {
        return "LibrarySymbol[" +
                "symbol=" + symbol + ", " +
                "aliases=" + aliases + ']';
    }


}

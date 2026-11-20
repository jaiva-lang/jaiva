package com.jaiva.interpreter.symbol;

import com.jaiva.interpreter.runtime.DebugController;
import com.jaiva.interpreter.symbol.mod.DefaultModifiers;
import com.jaiva.interpreter.symbol.mod.SymbolModifier;
import com.jaiva.tokenizer.tokens.TSymbol;
import com.jaiva.tokenizer.tokens.specific.TokenDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for functions and variables
 * <p>
 * It's essentially anything that can be named. if need a value, actually no. I
 * don't need to document this. Your fault if you're trying to understand MY
 * shit.
 */
public class Symbol implements Cloneable {
    /**
     * The name of the symbol
     */
    public String name;
    private UUID id = UUID.randomUUID();
    /**
     * The symbol type
     */
    public SymbolType symbolType;
    /**
     * boolean indicating whether the symbol is "frozen" which means it cannot be
     * modified.
     */
    public boolean isFrozen = false;
    /**
     * The token associated with the symbol.
     */
    public TokenDefault<?> token;

    private Symbol parentSymbol;

    /**
     * Default Constructor which sets this symbol to null.
     */
    public Symbol() {
        this.symbolType = SymbolType.NULL;
    }

    /**
     * Define a symbol without the token
     * 
     * @param name Symbol name
     * @param type Symbol type.
     */
    Symbol(String name, SymbolType type) {
        this.symbolType = type;
        this.name = name;
    }

    /**
     * Define a symbol with the token associated with it.
     * 
     * @param name  Symbol name
     * @param type  Symbol type.
     * @param token Symbol's token.
     */
    Symbol(String name, SymbolType type, TokenDefault<?> token) {
        this.symbolType = type;
        this.token = token;
        this.name = name;
    }

    /**
     * Freezes this symbol so that modificaitons cannot be made. This is usually
     * called in the constructor of a global variable.
     * <p>
     * A frozen symbol cannot be unfrozen.
     */
    public void freeze() {
        this.isFrozen = true;
    }

    public UUID getId() {
        return id;
    }


    public ArrayList<String> getModifiers() {
        return ((TSymbol)token).getModifiers();
    }

    public boolean containsModifier(@NotNull SymbolModifier symbolModifier) {
        return ((TSymbol)token).getModifiers().stream().anyMatch(symbolModifier.getAllAliases()::contains);
    }

    public boolean containsModifier(@NotNull DefaultModifiers symbolModifier) {
        return containsModifier(symbolModifier.getInstance());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(getId(), symbol.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    /**
     * This is the exact same as toString, however it exists such that
     * {@link BaseVariable} can be toStringed inside {@link DebugController}
     */
    public String toDebugString() {
        return "Symbol{name='" + name + "', symbolType=" + symbolType + ", isFrozen=" + isFrozen + ", token=" + token
                + "}";
    }

    @Override
    public String toString() {
        return "Symbol [name=" + name + ", symbolType=" + symbolType + ", isFrozen=" + isFrozen + ", token=" + token
                + "]";
    }

    public void setParentSymbol(@NotNull Symbol parentSymbol) {
        this.parentSymbol = parentSymbol;
    }

    @Nullable
    public Symbol getParentSymbol() {
        return parentSymbol;
    }

    /**
     * Overrides {@link Object#clone()} to provide a clone interface
     * @return A clone of this symbol.
     * @throws CloneNotSupportedException When the instance cannot be cloned.
     */
    public Symbol clone() throws CloneNotSupportedException {
        Symbol s =  (Symbol) super.clone();
        ((TSymbol)s.token).setModifiers(new ArrayList<>(this.getModifiers()));
        return s;
    }

    public void unfreeze() {
        isFrozen = false;
    }
}
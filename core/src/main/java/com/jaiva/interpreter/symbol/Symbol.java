package com.jaiva.interpreter.symbol;

import com.jaiva.interpreter.runtime.DebugController;
import com.jaiva.tokenizer.tokens.TokenDefault;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((symbolType == null) ? 0 : symbolType.hashCode());
        result = prime * result + (isFrozen ? 1231 : 1237);
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The equality is determined by comparing the {@code name}, {@code symbolType},
     * {@code isFrozen}, and {@code token} fields of the {@code Symbol} objects.
     * Two {@code Symbol} objects are considered equal if:
     * <ul>
     * <li>They are the same instance, or</li>
     * <li>The other object is not {@code null}, is of the same class,
     * and all compared fields are equal.</li>
     * </ul>
     *
     * @param obj the reference object with which to compare
     * @return {@code true} if this object is the same as the obj argument;
     *         {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Symbol other = (Symbol) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (symbolType != other.symbolType)
            return false;
        if (isFrozen != other.isFrozen)
            return false;
        if (token == null) {
            return other.token == null;
        } else return token.equals(other.token);
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

    /**
     * Overrides {@link Object#clone()} to provide a clone interface
     * @return A clone of this symbol.
     * @throws CloneNotSupportedException When the instance cannot be cloned.
     */
    public Symbol clone() throws CloneNotSupportedException {
        return (Symbol) super.clone();
    }

    public void unfreeze() {
        isFrozen = false;
    }
}
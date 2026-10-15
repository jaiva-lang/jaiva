package com.jaiva.interpreter;

import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibrarySymbol;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.tokenizer.tokens.TSymbol;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.TArrayVar;
import com.jaiva.tokenizer.tokens.specific.TFunction;
import com.jaiva.tokenizer.tokens.specific.TUnknownScalar;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * "vfs" stands for Variable FunctionS. It's a class that extends HashMap and in term inherits all it's properties.
 * <p>
 *     A vfs instance, holds all the symbols defined in the specific context that the user can use.
 * </p>
 * <p>
 *     The {@link String} is the identifier of a specific symbol in the given scope. Like a nickname, it is not
 *     the symbol's actual name, but what the user has to refer to in order to call the function/use the variable.
 *     e.g.: The symbol named {@code "function"} could have the identifier {@code "foo"}. Calling {@code foo()} will call
 *     the said {@code "function"}. However many times (unless frozen, see {@link Symbol#isFrozen}) the identifier is changed,
 *     it still references the exact same function that it was defined as.
 * </p>
 * <p>
 *     A vfs instance, is usually created only by {@link Scope} as all symbols are scoped constructs.
 *     The only "exceptions" (haha get it), would be {@link BaseLibrary} which creates
 *     a vfs instance to put all the globals into.
 * </p>
 * <p>
 * So much documentation, might as well call this an accumulation. HMM, bars.
 */
public final class Vfs extends LinkedHashMap<String, MapValue> {

    /**
     * Calls {@link HashMap#HashMap()} creating an empty hash map for vfs.
     */
    public Vfs() {
        super();
    }

    /**
     * Creates a new Vfs Object with the given HashMap.
     * @param vfs Given Vfs.
     */
    public Vfs(LinkedHashMap<String, MapValue> vfs) {
        this.putAll(vfs);
    }

    /**
     * Same as {@link HashMap#put(Object, Object)} however this method wraps the Symbol in a MapValue for you.
     * @param key The key
     * @param sym The symbol
     * @return The previous value associated with the key or null. See documentation for {@link HashMap#put(Object, Object)}
     */
    public MapValue put(String key, Symbol sym) {
        return super.put(key, new MapValue(sym));
    }

    @Override
    public Vfs clone() {
        return (Vfs) super.clone();
    }

    /**
     * Unlike {@link HashMap#clone()} which creates a shallow copy of itself and not the contents.
     * This goes through the trouble by doing that for you. (Creates a full copy of every {@link MapValue} and
     * {@link com.jaiva.interpreter.symbol.Symbol} that's in it.
     * @return An exact copy of this instance with every element being a copy.
     */
    @NotNull
    @Contract(" -> new")
    public Vfs fullCopy() {
        LinkedHashMap<String, MapValue> vfs = new LinkedHashMap<>();
        super.forEach((s, mv) -> {
            try {
                vfs.put(s, new MapValue(mv.getValue().clone()));
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        });

        return new Vfs(vfs);
    }

    /**
     * Converts the Vfs into a list of tokens containing every symbol's token.
     * @return An ArrayList of tokens.
     */
    @NotNull
    public ArrayList<Token<?>> toTokenList() {
        ArrayList<Token<?>> tokens = new ArrayList<>();
        this.forEach((s, mv) -> tokens.add(mv.getValue().token.toToken()));
        return tokens;
    }

    /**
     * Sorts the keys of the Vfs in alphabetical order.
     */
    public void sortKeys() {
        ArrayList<String> keys = new ArrayList<>(this.keySet());
        keys.sort(String::compareTo);
        HashMap<String, MapValue> sorted = new HashMap<>();
        for (String key : keys) {
            sorted.put(key, this.get(key));
        }
        this.clear();
        this.putAll(sorted);
    }

    public void putAsSymbolName(@NotNull LibrarySymbol symbol) {
        symbol.aliases().forEach(alias -> put(alias, symbol.symbol()));
    }

    @NotNull
    public static Vfs fromFile(@NotNull ArrayList<Token<?>> tokens) {
        Vfs vfs = new Vfs();
        tokens.forEach(token -> {
            if (token.value() instanceof TSymbol) {
                TokenDefault<?> t = token.value();
                if (t.exportSymbol) {
                    if (t instanceof TFunction func) {
                        String name = func.name.substring(2);
                        vfs.put(name, new BaseFunction(name, func));
                    } else if (t instanceof TUnknownScalar<?, ?> scalar) {
                        vfs.put(scalar.name, new BaseVariable(scalar.name, scalar, scalar.value));
                    } else {
                        // can only be TArrayVar now
                        TArrayVar var = (TArrayVar) t;
                        vfs.put(t.name, new BaseVariable(t.name, t, var.contents));
                    }
                }
            }
        });
        return vfs;
    }
}

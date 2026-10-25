package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.lang.Chars;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.TVariable;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an array variable such as {@code maak name <-| 1, 2, 3, 4!};
 */
public class TArrayVar extends TokenDefault<TArrayVar> implements TVariable {
    /**
     * The contents of the array variable.
     */
    public ArrayList<Object> contents;

    /**
     * Constructor for TArrayVar
     *
     * @param name     The name of the variable.
     * @param contents The contents of the variable.
     * @param ln       The line number.
     */
    public TArrayVar(@NotNull String name, ArrayList<Object> contents, int ln) {
        super(name.startsWith(Character.toString(Chars.EXPORT_SYMBOL)),
                (name.startsWith(Character.toString(Chars.EXPORT_SYMBOL))
                        ? name.replaceFirst(Pattern.quote(Character.toString(Chars.EXPORT_SYMBOL)),
                        Matcher.quoteReplacement(""))
                        : name),
                ln);
        this.contents = contents;
        json();
    }

    /**
     * Constructor for TArrayVar (for exporting globals.)
     *
     * @param name     The name of the variable.
     * @param contents The contents of the variable.
     * @param ln       The line number.
     */
    public TArrayVar(String name, ArrayList<Object> contents, int ln, JDoc customTooltip) {
        super(name, ln, customTooltip);
        this.contents = contents;
        json();
    }

    public void json() {
        json.set("value", contents);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TArrayVar}
     */
    public Token<TArrayVar> toToken() {
        return new Token<>(this);
    }
}

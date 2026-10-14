package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.lang.Chars;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.TParamsExtendable;
import com.jaiva.tokenizer.tokens.TVariable;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a variable where it's type can only be resolved by the interpeter
 * such as {@code maak name <- functionCall()!}; or if they made a variable but
 * didnt declare the value.
 * <p>
 *     This token is a type monster because it serves 2 purposes:
 *     <ol>
 *         <li>
 *             It's the abse class for the concrete {@link TStringVar}, {@link TBooleanVar}, {@link TNumberVar}
 *         </li>
 *         <li>
 *             It is itself also representative of a variable declration who's type cna only be determined at
 *             runtime
 *         </li>
 *     </ol>
 * </p>
 *
 * @param <Type> The type of the variable.
 */
public class TUnknownScalar<Type, K extends TokenDefault<K>> extends TokenDefault<K> implements TVariable, TParamsExtendable {
    /**
     * The value of the variable.
     */
    public Type value;

    /**
     * Constructor for basic.
     *
     * @param name  The name of the variable.
     * @param value The value of the variable.
     * @param ln    The line number.
     */
    public TUnknownScalar(String name, Type value, int ln) throws JaivaException {
        super(name.startsWith(Character.toString(Chars.EXPORT_SYMBOL)),
                (name.startsWith(Character.toString(Chars.EXPORT_SYMBOL))
                        ? name.replaceFirst(Pattern.quote(Character.toString(Chars.EXPORT_SYMBOL)),
                        Matcher.quoteReplacement(""))
                        : name),
                ln);
        this.value = value;
        json();
    }

    /**
     * Constructor for exporting globals.
     *
     * @param name  The name of the variable.
     * @param value The value of the variable.
     * @param ln    The line number.
     */
    public TUnknownScalar(String name, Type value, int ln, JDoc customToolTip) throws JaivaException {
        super(name, ln, customToolTip);
        this.value = value;
        json();
    }

    public void json() throws JaivaException {
        if (!json.keyExists("value"))
            switch (value) {
                case Number n -> json.set("value", n);
                case String s -> json.set("value", s);
                case Boolean b -> json.set("value", b);
                case Token token -> json.set("value", token);
                case TokenDefault tokenDefault -> json.set("value", tokenDefault);
                default -> throw new IllegalArgumentException("Shit.");
            }
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TUnknownScalar}
     */
    public Token<K> toToken() {
        return new Token(this);
    }

    @Override
    public TFuncCall get() {
        return checkObject(value);
    }
}

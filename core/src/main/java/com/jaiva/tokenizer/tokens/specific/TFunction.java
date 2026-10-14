package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.lang.Chars;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.TConstruct;
import com.jaiva.tokenizer.tokens.TSymbol;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a function definition such as
 * {@code kwenza addition(param1, param2) -> khutla (param1 + param2)! <~}
 * This class is
 */
public class TFunction extends TokenDefault<TFunction> implements TSymbol, TConstruct {
    /**
     * The arguments of the function.
     */
    public String[] args;
    /**
     * The body of the function.
     */
    public TCodeblock body;

    /**
     * An arraylist of booleans that specify which arguments are optional.
     */
    public ArrayList<Boolean> isArgOptional = new ArrayList<>();

    /**
     * Indicates whether the function accepts variable arguments (varargs).
     */
    public boolean varArgs = false;

    /**
     * Constructor for TFunction
     *
     * @param name The name of the function.
     * @param args The arguments of the function.
     * @param body The body of the function.
     * @param ln   The line number.
     */
    public TFunction(String name, String[] args, TCodeblock body, int ln) throws JaivaException {
        super(name.startsWith(Character.toString(Chars.EXPORT_SYMBOL)),
                "F~" + (name.startsWith(Character.toString(Chars.EXPORT_SYMBOL)) ? name
                        .replaceFirst(Pattern.quote(Character.toString(Chars.EXPORT_SYMBOL)),
                                Matcher.quoteReplacement(""))
                        : name),
                ln);
        String[] newArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (i == 0 && arg.startsWith(Chars.ASSIGNMENT)) {
                String newArg = arg.substring(2);
                if (!newArg.isBlank()) {
                    varArgs = true;
                    isArgOptional.add(true);
                    newArgs[i] = newArg;
                    break;
                }
            }
            isArgOptional.add(arg.endsWith("?"));
            if (arg.endsWith("?"))
                newArgs[i] = arg.substring(0, arg.length() - 1);
            else newArgs[i] = arg;
        }
        this.args = newArgs;
        this.body = body;
        json();
    }

    /**
     * Constructor for TFunction (for exporting globals.)
     *
     * @param name          The name of the function.
     * @param args          The arguments of the function.
     * @param body          The body of the function.
     * @param ln            The line number.
     * @param customToolTip The custom tooltip for the function.
     */
    public TFunction(String name, String[] args, TCodeblock body, int ln, JDoc customToolTip) throws JaivaException {
        super("F~" + name, ln, customToolTip);

        String[] newArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (i == 0 && arg.startsWith(Chars.ASSIGNMENT)) {
                String newArg = arg.substring(2);
                if (!newArg.isBlank()) {
                    varArgs = true;
                    isArgOptional.add(true);
                    newArgs[i] = newArg;
                    break;
                }
            }
            isArgOptional.add(arg.endsWith("?"));
            if (arg.endsWith("?"))
                newArgs[i] = arg.substring(0, arg.length() - 1);
            else newArgs[i] = arg;
        }
        this.args = newArgs;
        this.body = body;
        json();
    }

    public void json() throws JaivaException {
        json.set("args", args);
        json.set("isArgOptional", isArgOptional);
        json.set("varArgs", varArgs);
        json.set("body", body != null ? body.toJson() : null);
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TFunction}
     */
    public Token<TFunction> toToken() {
        return new Token<>(this);
    }

    /**
     * Converts this function token into a string representation of its definition,
     * including its name, arguments, and whether they are optional or varargs.
     *
     * @return A string representing the function's definition.
     */
    public String toDefinitionString() {
        StringBuilder out = new StringBuilder();
        out.append(name).append("(");
        for (int i = 0; i < this.args.length;i++) {
            String arg = this.args[i];
            if (this.varArgs) {
                out.append("<-").append(arg);
                break;
            }
            out.append(arg).append(
                    this.isArgOptional.get(i) ? "?" : ""
            ).append(
                    i != this.args.length - 1 ? ", " : ""
            );
        }
        return out.append(")").toString();
    }

    @Override
    public ArrayList<TCodeblock> getCodeBlocks() {
        return new ArrayList<>(List.of(body));
    }
}

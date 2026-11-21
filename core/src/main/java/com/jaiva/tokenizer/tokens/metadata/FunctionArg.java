package com.jaiva.tokenizer.tokens.metadata;

import com.jaiva.errors.JaivaException;
import com.jaiva.lang.Chars;
import com.jaiva.tokenizer.Tokenizer;
import com.jaiva.tokenizer.tokens.THasModifiers;
import io.github.yetnt.utils.builders.SimpleJSON;
import io.github.yetnt.utils.builders.ToJSON;
import io.github.yetnt.utils.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public final class FunctionArg implements ToJSON<SimpleJSON, Exception>, THasModifiers {
    private final String originalString;
    private final String name;
    private final boolean varArgs;
    private final boolean optional;
    private final ArrayList<String> modifiers = new ArrayList<>();

    public FunctionArg(@NotNull String arg, int lineNumber) throws JaivaException {
        boolean optional = arg.endsWith("?");
        if (optional)
            arg = arg.substring(0, arg.length()-1);
        this.originalString = arg;
        Pair<String,  ArrayList<String>> psi = Tokenizer.symbolModifiers(arg, lineNumber);
        this.varArgs = psi.getFirst().startsWith(Chars.ASSIGNMENT);
        this.name = varArgs ? psi.getFirst().substring(2) : psi.getFirst();
        this.optional = optional || this.varArgs;
        modifiers.addAll(psi.getSecond());
    }

    public String getName() {
        return name;
    }

    public String getOriginalString() {
        return originalString;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public ArrayList<String> getModifiers() {
        return modifiers;
    }

    public boolean isVarArgs() {
        return varArgs;
    }

    @Override
    public SimpleJSON toJson() throws Exception {
        SimpleJSON json = new SimpleJSON();
        json.set("name", name);
        json.set("modifiers", modifiers);
        json.set("optional", optional);
        json.set("originalString", originalString);
        return json;
    }

    @Override
    public String toString() {
        String name = getName();
        if (varArgs) return "<-" + name + "?";
        return name + (isOptional() ? "?" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FunctionArg that = (FunctionArg) o;
        return isVarArgs() == that.isVarArgs() && isOptional() == that.isOptional() && Objects.equals(getOriginalString(), that.getOriginalString()) && Objects.equals(getName(), that.getName()) && Objects.equals(getModifiers(), that.getModifiers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOriginalString(), getName(), isVarArgs(), isOptional(), getModifiers());
    }

    @NotNull
    public static ArrayList<FunctionArg> from(int lineNumber, @NotNull String... argz) throws JaivaException {
        ArrayList<FunctionArg> args = new ArrayList<>();
        for (String arg : argz) {
            if (arg == null || arg.isEmpty()) continue;
            args.add(new FunctionArg(arg, lineNumber));
        }
        return args;
    }

    public static boolean isVarArgs(@NotNull ArrayList<FunctionArg> args) {
        return args.size() == 1 && args.getFirst().isVarArgs();
    }
}

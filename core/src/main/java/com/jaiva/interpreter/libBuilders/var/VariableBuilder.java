package com.jaiva.interpreter.libBuilders.var;

import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.jdoc.JDocBuilder;
import com.jaiva.tokenizer.tokens.specific.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.TArrayVar;
import com.jaiva.tokenizer.tokens.specific.TBooleanVar;
import com.jaiva.tokenizer.tokens.specific.TNumberVar;
import com.jaiva.tokenizer.tokens.specific.TStringVar;

import java.util.ArrayList;

public class VariableBuilder {
    private String varName;
    private Object value;
    private JDocBuilder builder = JDoc.builder();

    private VariableBuilder() {}
    /**
     * Starts a new variable builder instance.
     * @return A new variable buildr
     */
    public static VariableBuilder start() {
        return new VariableBuilder();
    }

    public String getVarName() {
        return varName;
    }

    public Object getValue() {
        return value;
    }

    public JDocBuilder getBuilder() {
        return builder;
    }

    public VariableBuilder name(String name) {
        this.varName = name;
        return this;
    }

    public VariableBuilder docs(JDocBuilder builder) {
        this.builder = builder;
        return this;
    }

    public VariableBuilder value(Object value) {
        this.value = value;
        return this;
    }

    public TokenDefault<?> toToken() {
        try {
            return switch (value) {
                case ArrayList<?> arr -> new TArrayVar(varName, new ArrayList<>(arr), -1, builder.build());
                case Number n -> new TNumberVar(varName, n, -1, builder.build());
                case String str -> new TStringVar(varName, str, -1, builder.build());
                case Boolean b -> new TBooleanVar(varName, b, -1, builder.build());
                default -> throw new IllegalStateException("Unexpected value: " + value);
            };
        } catch (JaivaException e) {
            throw new RuntimeException(e);
        }
    }

    public BaseVariable.VariableType getType() {
        return value instanceof ArrayList<?> ? BaseVariable.VariableType.ARRAY : BaseVariable.VariableType.SCALAR;
    }

}

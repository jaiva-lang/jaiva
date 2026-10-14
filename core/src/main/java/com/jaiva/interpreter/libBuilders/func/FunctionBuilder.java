package com.jaiva.interpreter.libBuilders.func;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.jdoc.JDocBuilder;
import com.jaiva.tokenizer.tokens.specific.TFunction;

/**
 * FunctionBuilder allows the building of a function definition for library functions. I mean i dont have any more to say.
 * @see Arguments
 * @author Lehlogonolo Poole
 */
public class FunctionBuilder {
    private String name = "";
    private JDocBuilder builder = JDoc.builder();
    private Arguments arguments = new Arguments();

    private FunctionBuilder() {

    }

    /**
     * Starts a new function builder instance.
     * @return
     */
    public static FunctionBuilder start() {
        return new FunctionBuilder();
    }

    public FunctionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FunctionBuilder arguments(Arguments arguments) {
        this.arguments = arguments;
        return this;
    }

    public FunctionBuilder docs(JDocBuilder builder) {
        this.builder = builder;
        return this;
    }

    public String getName() {
        return name;
    }

    public JDocBuilder getBuilder() {
        return builder;
    }

    public Arguments getArguments() {
        return arguments;
    }

    public TFunction toToken() {
        try {
            return new TFunction(
                    getName(),
                    arguments.build(),
                    null, -1,
                    builder.addParams(arguments)
                            .build()
            );
        } catch (JaivaException e) {
            throw new RuntimeException(e);
        }
    }
}

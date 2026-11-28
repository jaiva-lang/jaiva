package com.jaiva.interpreter.libBuilders.func;

import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.jdoc.JDocBuilder;
import com.jaiva.tokenizer.tokens.specific.TFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * FunctionBuilder allows the building of a function definition for library functions.
 * I mean i dont have any more to say.
 * Here is an example taken from {@link com.jaiva.interpreter.libs.math.MathBase}'s {@code m_log}
 * <pre>{@code
 *             super(
 *                     FunctionBuilder.start()
 *                             .name("m_log") // The name of the function
 *                             .setLib("math/base") // The library the function is defined within for tooling purposes.
 *                             .arguments(
 *                             Arguments.getInstance().add(
 *                             // Define the first argument "value" (required)
 *                                     new AArgument("value", "The value to calculate the logarithm of.",
 *                                             false, Argument.Type.NUMBER)
 *                             ).add(
 *                             // Define the second argument "base" (optional)
 *                                     new AArgument("base", "The base of the logarithm, otherwise 10 is used",
 *                                             true, Argument.Type.NUMBER)
 *                             ))
 *                             .docs(
 *                             // Extra documentation
 *                                     JDoc.builder()
 *                                             .addDesc("Calculates the logarithm of a value with the specified base.")
 *                                             .addReturns("The logarithm of the value with the specified base.")
 *                                             .addExample("""
 *                                             khuluma("Log base 10 of 1000 is: " + m_log(1000))!
 *                                             khuluma("Log base 2 of 1024 is: " + m_log(1024, 2))!
 *                                             """)
 *                                             .sinceVersion("5.0.0")
 *                             )
 *             );
 * }</pre>
 * @see Arguments
 * @author Lehlogonolo Poole
 */
public class FunctionBuilder {
    private String name = "";
    private JDocBuilder builder = JDoc.builder();
    private Arguments arguments = new Arguments();
    private String lib = "?";

    private FunctionBuilder() {

    }

    /**
     * Starts a new function builder instance.
     * @return
     */
    @NotNull
    @Contract(" -> new")
    public static FunctionBuilder start() {
        return new FunctionBuilder();
    }

    /**
     * Inputs the given name which will be the canonical name of the function.
     * @implNote If you are using {@link com.jaiva.interpreter.libs.LibrarySymbol}
     * via {@code BaseLibrary#addWithAliases(Symbol, String...)} this applied name is still
     * the canonical nam eof the function whereas it's aliases are just other names it can be found
     * other in the {@link com.jaiva.interpreter.Vfs}
     * @param name The name of the function
     * @return This FunctionBuilder instance for chaining.
     */
    public FunctionBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Inputs the given {@link Arguments} instance.
     * @param arguments The arguments instance
     * @return This FunctionBuilder instance for chaining.
     */
    public FunctionBuilder arguments(Arguments arguments) {
        this.arguments = arguments;
        return this;
    }

    /**
     * Inputs any other useful docs other than parameters.
     * @param builder The doc builder, which can be created via {@link JDoc#builder()}
     * @return This FunctionBuilder instance for chaining.
     */
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

    /**
     * Constructs a new {@link TFunction} token from the given input.
     * @return
     */
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

    /**
     * Sets the library origin of this function
     * @param lib The library, matching the exact path/identifier in the own library's {@link PublicLibrary} or
     *            {@link JaivaLibrary} annotation
     * @return This FunctionBuilder instance for chaining.
     */
    public FunctionBuilder setLib(String lib){
        this.lib = lib;
        return this;
    }

    public String getLib() {
        return lib;
    }
}

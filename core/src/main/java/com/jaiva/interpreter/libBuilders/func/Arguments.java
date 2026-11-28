package com.jaiva.interpreter.libBuilders.func;

import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libBuilders.func.arg.AFuncArgument;
import com.jaiva.interpreter.libBuilders.func.arg.AVarArgument;
import com.jaiva.interpreter.libBuilders.func.arg.NeighbouringArg;
import com.jaiva.interpreter.symbol.fn.BaseFunction;
import com.jaiva.interpreter.symbol.fn.ImplementedFunction;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.specific.TFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Builder to help... building arguments what did you guess lmao?
 * <p>
 *     An example stolen from {@link com.jaiva.interpreter.libs.file.txt.FileApi}'s {@code f_new} which contains
 *     a good chunk of arguments.
 *     <pre>{@code
 *     Arguments.getInstance()
 *          .add(new AArgument(
 *                  "path", "The path to the new file to create. Along with the file name and extension",
 *                  false, Argument.Type.STRING
 *          ))
 *          .add(new AArgument(
 *                  "content", "The content the file should hold. Either a string or an array of strings.",
 *                  false, Argument.Type.ANY
 *          ))
 *          .add(new AArgument(
 *                  "canRead", "Whether or not the file can be read. Defaults to true",
 *                  true, Argument.Type.BOOLEAN
 *          ))
 *          .add(new AArgument(
 *                  "canWrite", "Whether the file can be written to or not. Defaults to true",
 *                  true, Argument.Type.BOOLEAN
 *          ))
 *          .add(new AArgument(
 *                  "canExecute", "Whether the file can be executed or not. Defaults to true.",
 *                  true, Argument.Type.BOOLEAN
 *          ))
 *     }</pre>
 * </p>
 * <p>
 *     Now it is important to note that, majority of the input given for each argument
 *     is purely for
 *     <ol>
 *         <li>1. Generating a well formatted {@link TFunction} token</li>
 *         <li>2. Generating well documented {@link JDoc} to be passed into {@link TFunction}</li>
 *     </ol>
 *     Any type input is not validated and it is purely the function's job to validate types. The type input
 *     is purely for documentation purposes.
 * </p>
 * @author Lehlogonolo
 * @see AArgument
 * @see AFuncArgument
 * @see AVarArgument
 * @see NeighbouringArg
 * @see FunctionBuilder
 * @see ImplementedFunction
 * @see BaseFunction
 */
public class Arguments {
    private final ArrayList<Argument> arguments = new ArrayList<>();
    Arguments() {}

    /**
     * Creates a new Arguments instance.
     * @return A new arguments instance.
     */
    @NotNull
    @Contract(" -> new")
    public static Arguments getInstance() {
        return new Arguments();
    }

    /**
     * Adds any singular argument which can be
     * <ul>
     *     <li>{@link AArgument} instance</li>
     *     <li>{@link AFuncArgument} (which is an AArgument anyway)</li>
     * </ul>
     * @param argument The argument
     * @return This Arguments instance for chaining
     */
    public Arguments add(NeighbouringArg argument) {
        arguments.add((Argument) argument);
        return this;
    }

    /**
     * Adds a singular var args parameter. As per Jaiva's rules on variable arguments which need to be by itself
     * and not mixed with other args.
     * @param argument The argument
     * @return This Arguments instance for chaining
     */
    public Arguments addVarArg(AVarArgument argument) {
        // only one arg there.
        if (!arguments.isEmpty()) {
            throw new RuntimeException("Only one argument (vararg) is allowed when added.");
        }
        arguments.add(argument);
        return this;
    }

    /**
     * Builds the raw string array of arguments for input in {@link TFunction}
     * @return A new String Array
     */
    @Contract(pure = true, value = "->new")
    public String[] build() {
        if (arguments.isEmpty()) {
            return new String[] {};
        }
        String[] result = new String[arguments.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = arguments.get(i).toTokenString();
        }
        return result;
    }

    /**
     * Fetches the input arguments
     * @return A new ArrayList of the arguments
     */
    @Contract("->new")
    public ArrayList<Argument> getArguments() {
        return new ArrayList<>(arguments);
    }

    /**
     * Attempts to retrieve the idex of an argument by it's name.
     * @param name THe name of the argument
     * @return The positional index of this argument, otherwise {@code -1}
     */
    @Contract(pure = true)
    public int getIndexFromName(@NotNull String name) {
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i).name().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}

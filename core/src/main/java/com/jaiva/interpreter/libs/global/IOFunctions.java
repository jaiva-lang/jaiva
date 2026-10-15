package com.jaiva.interpreter.libs.global;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libBuilders.var.VariableBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.runtime.Resources;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.lang.EscapeSequence;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * IOFunctions class holds the functions that are used for input and output in
 * the console.
 */
public class IOFunctions extends BaseLibrary {

    /**
     * Constructs the IOFunctions with the Input/Output functions.
     */
    public IOFunctions(IConfig<Object> config) {
        super();
        add(
                new FKhuluma(), new FMamela(), new FClear(), new VArgs(config), new VUArgs(config)
        );
    }

    /**
     * khuluma("hello world")!
     * This will print the given input to the console.
     */
    static class FKhuluma extends BaseFunction {

        FKhuluma() {
            super(
                    FunctionBuilder.start()
                            .name("khuluma")
                            .arguments(Arguments.getInstance()
                                    .add(
                                            new AArgument(
                                                    "msg", "The message to print.",
                                                    true, Argument.Type.ANY
                                            )
                                    ).add(
                                            new AArgument(
                                                    "removenewLn", "If true, no new line is printed after the message." +
                                                    " Defaults to false.", true, Argument.Type.BOOLEAN
                                            )
                                    )
                            ).docs(
                                    JDoc.builder()
                                            .addDesc("Prints any given input to the console.")
                                            .addReturns("idk")
                                            .sinceVersion("1.0.0-beta.2")
                                            .addExample("""
                                            khuluma("Hello, World!")! @ Prints "Hello, World!" to the console with a new line.
                                            @ Then the following prints "Hello" then "World!" on the same line.
                                            khuluma("Hello, ", true)!
                                            khuluma("World!")!
                                            khuluma()! @ Prints just a new line.
                                            """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            String output;
            Object o = !callProperties.params().isEmpty() ? callProperties.params().getFirst() : null;
            Object newO = o;
            if (o == null || o instanceof TVoidValue) {
                if (callProperties.config().isStreamer())
                    return Token.voidValue(callProperties.getLineNumber());
                System.out.println();
                return Token.voidValue(callProperties.getLineNumber());
            }
            Object v = callProperties.params().size() > 1
                    ? callProperties.params().get(1)
                    : null;
//            if (o instanceof Token<?> || (o instanceof TokenDefault && Interpreter.isVariableToken(o))) {
//                assert o instanceof Token<?>;
//                TokenDefault token = ((Token<?>) o).value();
//                // Only TokenDefault classes have the .toToken() method, but TokenDefualt itself
//                // doesnt, so we kinda need to check for every possible case unfortunately.
//                Object input = token instanceof TExpression || token instanceof TVarRef || token instanceof TFuncCall
//                        ? o
//                        : token;
//                Object value = Interpreter.handleVariables(input, callProperties.config(), callProperties.scope());
//                // System.out.println(value);
//                assert value != null;
//                output = value.toString();
//                newO = value;
//            } else if (o instanceof ThrowIfGlobalContext) {
//                // we dont have context, so just alert the user.
//                // that either, they tried using the "voetsek" or "nevermind" outside of a for
//                // loop context or cima was called with a custom error.
//                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(),
//                        "Cannot use 'voetsek' or 'nevermind' outside of a loop context, or 'cima' was called with a custom error.",
//                        callProperties.getLineNumber());
//            } else {
                output = o.toString();
//            }

            String isJustStr = newO instanceof String && callProperties.config().isPrintStacks() ? "\"" : "";

            if (callProperties.config().isStreamer())
                return Token.voidValue(callProperties.getLineNumber());
            if (v instanceof Boolean && (Boolean) v) {
                System.out.print(
                        isJustStr + EscapeSequence.fromEscape(output, callProperties.getLineNumber()) + isJustStr);
            } else if (newO instanceof ArrayList<?> arr)
            {
                System.out.println(arrToString(arr));
            }else{
                System.out.println(
                        isJustStr + EscapeSequence.fromEscape(output, callProperties.getLineNumber()) + isJustStr);
            }
            return Token.voidValue(callProperties.getLineNumber());
        }

        private String arrToString(ArrayList<?> arr) {
            StringBuilder b = new StringBuilder();
            b.append("[");
            for (int i = 0; i < arr.size(); i++) {
                Object t = arr.get(i);
                b.append(t instanceof String ? "\"" + t + "\"" : t instanceof ArrayList<?> a ? arrToString(a) : t);
                if (i != arr.size() - 1) b.append(", ");
            }
            b.append("]");
            return b.toString();
        }
    }

    /**
     * maak n <- mamela()!
     * Listens for input form the console and returns a string.
     */
    static class FMamela extends BaseFunction {
        FMamela() {
            super(
                    FunctionBuilder.start()
                            .name("mamela")
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Listens for input from the console.")
                                            .addReturns("The input given from the console as a string")
                                            .addNote("GithubBlockQuote: This will pause all execution until input is given.")
                                            .sinceVersion("1.0.0-beta.3")
                                            .addExample("""
                                            khuluma("What is your name?")!
                                            maak name <- mamela()! @ Reads input from the user and stores it in the variable name.
                                            khuluma("Hello, " + name + "!")!
                                            """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            if (callProperties.config().isStreamer())
                return Token.voidValue(callProperties.getLineNumber());

            return callProperties.config().getGlobalResources()
                    .ofCurrentThread()
                    .getResource(Resources.Common.CONSOLE_IN.getUuid(), Scanner.class)
                    .nextLine();
        }
    }

    /**
     * FClear is a built-in function that clears the console output.
     * <p>
     * This function uses ANSI escape codes to clear the terminal screen and move
     * the cursor to the home position.
     * It does not take any parameters and returns void.
     * </p>
     *
     * Usage:
     * 
     * <pre>
     * clear();
     * </pre>
     *
     * Note: The effectiveness of this function depends on the terminal's support
     * for ANSI escape codes.
     */
    static class FClear extends BaseFunction {

        FClear() {
            super(
                    FunctionBuilder.start()
                            .name("clear")
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Clears the console.")
                                            .addReturns("idk")
                                            .addNote("The effectiveness of this function depends on the terminal's support for ANSI escape codes.")
                                            .sinceVersion("1.0.0")
                                            .addExample("""
                                            clear()! @ Clears the console output.
                                            """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws InterpreterException {
            // Clear the console using ANSI escape codes.
            if (callProperties.config().isStreamer())
                return Token.voidValue(callProperties.getLineNumber());
            System.out.print("\033[H\033[2J");
            System.out.flush();
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    /**
     * Represents the "args" variable, which contains the command-line arguments
     * passed to the Jaiva interpreter.
     * <p>
     * This variable is automatically populated with the arguments provided when
     * running the Jaiva command.
     * </p>
     */
    static class VArgs extends BaseVariable {
        VArgs(IConfig<Object> config) {
            super(
                    VariableBuilder.start()
                            .name("args")
                            .value(new ArrayList<>(Arrays.asList(config.getArgs())))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("The command-line arguments passed to the Jaiva command.")
                                            .addReturns("An array of strings, where each string is a command-line argument.")
                                            .sinceVersion("1.0.2")
                                            .addNote("This is the raw arguments given from Main.args[], including Jaiva-specific arguments.")
                                            .addExample("""
                                            khuluma(args[0])! @ Prints "jaiva" if the command was 'jaiva myscript.jv'
                                            khuluma(args[1])! @ Prints "myscript.jv" if the command was 'jaiva myscript.jv'
                                            """)
                            )
            );
            freeze();
        }
    }

    static class VUArgs extends BaseVariable {
        VUArgs(IConfig<Object> config) {
            super(
                    VariableBuilder.start()
                            .name("uArgs")
                            .value(config.getSanitisedArgs())
                            .docs(
                                    JDoc.builder()
                                            .addDesc("The command-line arguments without Jaiva-specific arguments.")
                                            .addReturns("An array of strings, representing the sanitized command-line arguments.")
                                            .sinceVersion("1.0.2")
                                            .addExample("""
                                            khuluma(uargs[0])! @ Prints any other argument given after the Jaiva specific ones.
                                            khuluma(uargs[1])! @ Prints the second user argument if provided.
                                            khuluma(uargs[2])! @ And so on...
                                            """))
            );
            freeze();
        }
    }
}

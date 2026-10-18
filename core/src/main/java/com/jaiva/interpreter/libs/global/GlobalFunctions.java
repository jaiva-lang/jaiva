package com.jaiva.interpreter.libs.global;

import com.jaiva.Main;
import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.MapValue;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libBuilders.func.arg.AVarArgument;
import com.jaiva.interpreter.libBuilders.var.VariableBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.Globals;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.lang.Keywords;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Simply holds all the global functions. THis is a container and is just added into
 * {@link Globals} with the purpose of being here so we dont make GLobals a god class since it also
 * has to handle loading the actual global scope and eveyr other external and jaiva internal library.
 */
public class GlobalFunctions extends BaseLibrary {

    public GlobalFunctions(IConfig<Object> config) {
        add(
                new FGetVarClass(), new VReservedKeywords(), new VJaivaVersion(), new FFlat(),
                new FSleep(), new FTypeOf(), new FTypeOfNumber(), new FArrayLiteral(),
                new FScope(), new FGetCallerValue()
        );
        add(new IOFunctions(config));
    }

    static class FScope extends BaseFunction {
        FScope() {
            super(
                    FunctionBuilder.start()
                            .name("scope")
                            .arguments(
                                    Arguments.getInstance()
                                            .addVarArg(new AVarArgument("string", "variable amount of strings to input."))
                            )
                            .docs(
                                    JDoc.builder()
                                            .addReturns("idk")
                                            .addNote(
                                                    """
                                                    The following are accepted strings:\s
                                                        "sw" to suppress all warnings.\s
                                                        "ew" to elevate all warnings.\s
                                                        "constant" to make all symbols given constant. and\s
                                                        "strict" which toggles "ew" and "constant"
                                                    """
                                            )
                                            .addExample("""
                                    scope("freezeAll")!
                                    kwenza af(a) ->
                                        khutla a!
                                    <~
                                    af <- 10! @ Errors as the variable af cannot be written to.
                                    
                                    @ Or
                                    
                                    scope("ew")!
                                    @* depr $> This fucntion is deprecated.
                                    kwenza af(a) ->
                                        khutla a!
                                    <~
                                    af()! @ Errors as the usual deprecation warning is now a fatal error. (Crashes the interpreter)
                                    """)
                                            .sinceVersion("4.1.1")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            ArrayList<String> s = new ArrayList<>();

            checkParams(callProperties.toCheckParams());
            callProperties.params().forEach(o -> {
                try {
                    if (o instanceof String s2) {
                        s.add(s2);
                    } else {
                        throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "scope() only accepts input of string.", callProperties.getLineNumber());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            callProperties.scope().getConfig().set(callProperties.getLineNumber(), callProperties.scope(), s.toArray(String[]::new));
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    /**
     * getVarClass(variable)
     * Returns the .toString() class representation of a variable's token.
     */
    static class FGetVarClass extends BaseFunction {
        FGetVarClass() {
            super(
                    FunctionBuilder.start()
                            .name("getVarClass")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(
                                                    new AArgument("var", "The value to return it's token symbol for", false, Argument.Type.ANY)
                                            )
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Attempts to return the symbol's corresponding Java class in string form. If you're using this then you def don't know what you're doing.")
                                            .addReturns("The .toString() class representation of the given variable's token")
                                            .sinceVersion("1.0.0-beta.0")
                                            .addExample("""
                                    maak name <- "ayo!"!
                                    khuluma(getVarClass(name))! @ Prints com.jaiva.tokenizer.tokens.Token$TStringVar@(hashcode)
                                    khuluma(getVarClass(reservedKeywords))! @ Prints com.jaiva.tokenizer.tokens.Token$TArrayVar@(hashcode)
                                    """)
                            )
            );
            freeze();
            evaluateParamsLater();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            String name;
            ArrayList<Object> args = callProperties.tFuncCall().args;
            if (args.getFirst() instanceof String str) {
                name = str;
            } else if (args.getFirst() instanceof Token && ((Token<?>) args.getFirst()).value() instanceof TVarRef) {
                name = ((TVarRef) ((Token<?>) args.getFirst()).value()).varName.toString();
            } else {
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(),
                        "getVarClass() only accepts a variable reference or a string, whatever you sent is disgusting.",
                        callProperties.getLineNumber());
            }
            MapValue var = callProperties.scope().getVfs().get(name);
            if (var == null) {
                throw new InterpreterException.UnknownVariableException(callProperties.scope(), name, callProperties.getLineNumber());
            }
            if (!(var.getValue() instanceof Symbol symbol)) {
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(),
                        name + " is not a variable nor a function, wtf. this error shouldn't happen.",
                        callProperties.getLineNumber());
            }
            // We need to convert the named token to a raw token so we can call .toString()
            // on it.
            return symbol.token.getClass().getSimpleName();
        }
    }

    /**
     * reservedKeywords (array) variable.
     * This contains an array of the reserved keywords
     */
    static class VReservedKeywords extends BaseVariable {
        VReservedKeywords() {
            super("reservedKeywords",
                    new TArrayVar("reservedKeywords", new ArrayList<>(Arrays.asList(Keywords.all)), -1,
                            JDoc.builder()
                                    .sinceVersion("1.0.0-beta.0")
                                    .addExample("khuluma(reservedKeywords)! @ Prints all the reserved keywords.")
                                    .addDesc("An array containing jaiva's reserved keywords that you cannot use as symbol names.").build()),
                    new ArrayList<>(Arrays.asList(Keywords.all)));
            freeze();
        }
    }

    /**
     * getCallerValue(variable)
     */
    static class FGetCallerValue extends BaseFunction {
        FGetCallerValue() {
            super(
                    FunctionBuilder.start()
                            .name("getCallerValue")
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the caller value provided to this file if this file was run by another java program.")
                                            .addReturns("The caller value")
                                            .sinceVersion("5.0.2")
                                            .addExample("""
                                    @ Say we are in J3Engine command
                                    maak value <- getCallerValue()!
                                    @ use it
                                    """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            Object value = callProperties.config().getCallerValue();
            return value == null ? Token.voidValue(callProperties.getLineNumber()) : value;
        }
    }

    /**
     * version variable.
     * This holds the current version of jaiva in {@link Main#version}
     */
    static class VJaivaVersion extends BaseVariable {
        VJaivaVersion() {
            super(
                    VariableBuilder.start()
                            .value(Main.version)
                            .name("version")
                            .docs(JDoc.builder()
                                    .addDesc("What do you think this returns.")
                                    .addExample("""
                                    khuluma(version)! @ Prints 5.0.0 (at the time of writing)
                                    """)
                                    .sinceVersion("1.0.0-beta.0"))
            );
            freeze();
        }
    }

    /**
     * flat(array1, array2, array3, array4, ...)
     * flat(<-arrays)
     * Takes in 2 or more arrays and flattens them into a singular array.
     */
    static class FFlat extends BaseFunction {
        FFlat() {
            super(
                    FunctionBuilder.start()
                            .name("flat")
                            .arguments(
                                    Arguments.getInstance()
                                            .addVarArg(new AVarArgument("arrays", "Variable amount of arrays to input"))
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc( "Attempts to flatten (at the top level) the given arrays array1 and array2 into 1 single array.")
                                            .addNote("If there are any type mismatches in array1, it will be ignored and the same check is done to array2 and so on. Therefore this function will **always** return an array, whether or not it was successful.")
                                            .sinceVersion("1.0.0-beta.2")
                                            .addExample("""
                                    maak array1 <-| 1, 2, 3!
                                    maak array2 <-| 4, 5, 6!
                                    maak array3 <- flat(array1, array2)! @ Flattens the two arrays into a new one.
                                    khuluma(array3)! @ Prints [1, 2, 3, 4, 5, 6]
                                    """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {

            checkParams(callProperties.toCheckParams());
            ArrayList<Object> returned = new ArrayList<>();
            callProperties.params().forEach(arg -> {
                if (arg instanceof TVarRef && ((TVarRef) arg).index == null) {
                    String name = ((TVarRef) arg).name;
                    MapValue v = callProperties.scope().getVfs().get(name);
                    if (v == null)
                        return;
                    if (!(v.getValue() instanceof BaseVariable))
                        return;
                    if (((BaseVariable) v.getValue()).a_size() <= 0)
                        return; // technically in this case, it will concat the arrays because this array has
                    // nothing to concat lol.
                    returned.addAll(((BaseVariable) v.getValue()).a_getAll());
                } else {
                    // stuff that need be parsed, parse and pray arraylist is returned.
                    Object parsed = null;
                    try {
                        parsed = arg;
                    } catch (Exception e) {
                        // do nothing.
                    }
                    if (!(parsed instanceof ArrayList))
                        return;

                    returned.addAll((ArrayList) parsed);
                }
            });
            return returned;
        }
    }

    /**
     * Represents a built-in function that pauses the execution of the current
     * thread for a specified number of milliseconds.
     * <p>
     * The function expects a single integer parameter representing the duration to
     * sleep in milliseconds.
     * If the parameter is not an integer, a {@link InterpreterException.WtfAreYouDoingException} is
     * thrown.
     * </p>
     *
     * <p>
     * Usage: <code>sleep(milliseconds)</code>
     * </p>
     *
     */
    static class FSleep extends BaseFunction {
        FSleep() {
            super(
                    FunctionBuilder.start()
                            .name("sleep")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(new AArgument("milliseconds", "The amount of milliseconds to sleep for", false, Argument.Type.NUMBER))
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Pause execution of the interpreter for n amount of milliseconds.")
                                            .addNote("(Keep in mind this function still has to take your value and turn it into a Java primitive and other things, so the delay might not be exact. If you're looking for accuracy maybe remove x amount of ms till it's accurate.)")
                                            .sinceVersion("1.0.0")
                                            .addExample("""
                                    khuluma("yo")!
                                    sleep(1000)! @ pause for 1 second.
                                    khuluma("yo.. again")!
                                    """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object val = callProperties.params().getFirst();
            if (!(val instanceof Integer integer))
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "Bruv, you can't just like, pls put number",
                        callProperties.getLineNumber());

            Thread.sleep(integer);

            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    static class FTypeOfNumber extends BaseFunction {
        FTypeOfNumber() {
            super(FunctionBuilder.start()
                    .name("typeOfNumber")
                    .arguments(
                            Arguments.getInstance()
                                    .add(new AArgument(
                                            "input",
                                            "The input to check the type against",
                                            true,
                                            Argument.Type.ANY
                                    ))
                    )
                    .docs(
                            JDoc.builder()
                                    .addDesc("Returns the type of a given number input. which in Java terms is either an integer, double or long")
                                    .addReturns("Returns the string form of the type, which could be \"integer\", \"double\", \"long\", or the primitive idk.")
                                    .sinceVersion("5.0.4")
                                    .addExample("""
                                    maak b <- 100!
                                    
                                    khuluma(typeOf(b))!                   @ "integer"
                                    khuluma(typeOf(0.34))!                @ "double"
                                    khuluma(typeOf(0.34d))!               @ "double"
                                    khuluma(typeOf())!                    @ idk
                                    khuluma(typeOf(82936741648236817L))!  @ "long"
                                    """)
                    )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {

//            checkParams(callProperties.toCheckParams());

            if (callProperties.params().isEmpty())
                return Token.voidValue(callProperties.getLineNumber());

            return switch (callProperties.params().getFirst()) {
                case Integer ignored2 -> "integer";
                case Double ignored1 -> "double";
                case Long ignored -> "long";
                case null, default ->
// there is no other type to possibly check for.
                        Token.voidValue(callProperties.getLineNumber());
            };
        }
    }

    static class FTypeOf extends BaseFunction {
        FTypeOf() {
            super(
                    FunctionBuilder.start()
                            .name("typeOf")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument(
                                            "input","The input to check the type against",
                                            true, Argument.Type.ANY
                                    )
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the type of any given input.")
                                            .addReturns("Returns the string form of the typ, which could be \"array\", \"string\", \"boolean\", \"number\", \"function\", or the primitive idk. "
                                                    + " If you require a more precise answer than number use typeOfNumber")
                                            .sinceVersion("3.0.0")
                                            .addExample("""
                                            maak b <- 100!
                                            
                                            khuluma(typeOf(b))!                   @ "number"
                                            khuluma(typeOf(typeOf))!                @ "function"
                                            khuluma(typeOf())!                    @ idk
                                            khuluma(typeOf(aowa))!                @ "boolean"
                                            khuluma(typeOf("what the f"))!        @ "string"
                                            khuluma(typeOf(reservedKeywords))!    @ "array"
                                            khuluma(typeOf(idk))!                 @ idk
                                            """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {

//            this.checkParams(callProperties.toCheckParams());
            if (callProperties.params().isEmpty())
                return Token.voidValue(callProperties.getLineNumber());

            return switch (callProperties.params().getFirst()) {
                case ArrayList ignored -> "array";
                case Boolean ignored -> "boolean";
                case Number ignored -> "number";
                case BaseFunction ignored -> "function";
                case String ignored -> "string";
                case null, default ->
// there is no other type to possibly check for.
                        Token.voidValue(callProperties.getLineNumber());
            };
        }
    }

    static class FArrayLiteral extends BaseFunction {
        FArrayLiteral() {
            super(
                    FunctionBuilder.start()
                            .name("arrLit")
                            .arguments(Arguments.getInstance().addVarArg(
                                    new AVarArgument(
                                            "elements", "Variable amount of elements to take in and turn into a single array."
                                    )
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Creates an array literal from the given elements. This is useful if you want to createFunction an array without declaring it to a variable. For example, `arrLit(1, 2, 3)` will return `[1, 2, 3]`. This is needed as Jaiva doesnt have square bracket syntax")
                                            .addReturns("The input given, as an array")
                                            .sinceVersion("3.0.0")
                                            .addExample("""
                                            maak array1 <- arrLit(1, 2, 3, "hello", aowa, idk)! @ Creates an array with mixed types.
                                            maak array2 <-| 1, 2, 3, "hello", aowa, idk! @ Creates an array with mixed types. (Same as above but with maak syntax)
                                            maak array3 <- arrLit()! @ Creates an empty array.
                                            khuluma(array1)! @ Prints [1, 2, 3, "hello", aowa, idk]
                                            khuluma(array3)! @ Prints []
                                            khuluma(array1 = array2)! @ Prints aowa (false) (I am not implementing array equality via `=` operator anytime soon. It is the exact same array though.)
                                            """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            return new ArrayList<>(callProperties.params());
        }
    }

}

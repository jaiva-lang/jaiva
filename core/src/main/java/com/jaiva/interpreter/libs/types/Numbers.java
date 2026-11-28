package com.jaiva.interpreter.libs.types;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libBuilders.func.arg.AVarArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.fn.CallProperties;
import com.jaiva.interpreter.symbol.fn.ImplementedFunction;
import com.jaiva.tokenizer.jdoc.JDoc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@JaivaLibrary(path="types/numbers", description = "More complicated converters")
public class Numbers extends BaseLibrary {
    public Numbers() {
        add(
                new FDoubleToIEE754Long(),
                new FLongToIEEE754Double(),
                new FStringFromByteArray(),
                new FStringToByteArray()
        );
    }

    public static class FDoubleToIEE754Long extends ImplementedFunction {
        public FDoubleToIEE754Long() {
            super(
                    FunctionBuilder.start()
                            .name("t_dToIEEE754Long")
                            .setLib("types/numbers")
                            .arguments(
                                    Arguments.getInstance().add(
                                            new AArgument(
                                                    "double", "The double to get the bits off",
                                                    false, Argument.Type.NUMBER
                                            )
                                    )
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc(
                                                    "Returns the representation of the given double, as a long, where it's bits "
                                                    + "correspond to the IEEE 754 floating-point \"double format\" bit layout."
                                            )
                                            .addReturns("the bits that represent the double")
                                            .sinceVersion("6.0.0-beta.5")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            Double d = fetch(0, Double.class, callProperties);
            return Double.doubleToLongBits(d);
        }

    }

    public static class FLongToIEEE754Double extends ImplementedFunction {
        public FLongToIEEE754Double() {
            super(
                    FunctionBuilder.start()
                            .name("t_lToIEEE754Double")
                            .setLib("types/numbers")
                            .arguments(
                                    Arguments.getInstance().add(
                                            new AArgument(
                                                    "long", "The representation of the double as a long",
                                                    false, Argument.Type.NUMBER
                                            )
                                    )
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc(
                                                    "Returns the double value of which this long is assumed to be a representation of "
                                                    + "the IEEE 754 floating-point \"double format\" bit layout."
                                            )
                                            .addReturns("the double this long's bits represent.")
                                            .sinceVersion("6.0.0-beta.5")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            Long l = fetch(0, Long.class, callProperties);
            return Double.longBitsToDouble(l);
        }
    }

    static class FStringFromByteArray extends ImplementedFunction {
        public FStringFromByteArray() {
            super(
                    FunctionBuilder.start()
                            .name("t_strFromByteArr")
                            .setLib("types/numbers")
                            .arguments(
                                    Arguments.getInstance().addVarArg(
                                            new AVarArgument(
                                                    "arr",
                                                    "A var args byte array (integer values from 0 to 255)"
                                            )
                                    )
                            )
                    .docs(JDoc.builder()
                            .addDesc(
                                    "Converts the given byte array into a UTF-8 (Standard) string"
                            ).addReturns("the string")
                            .sinceVersion("6.1.0")
                            .addExample("""
                                    tsea "jaiva/types/numbers"!
                                    
                                    maak byteArr <-| 72, 101, 108, 108, 111!
                                    
                                    khuluma(t_strFromByteArr(byteArr:::))! @ Prints "Hello"
                                    """)
                    )
            );
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            if (callProperties.params().isEmpty() || (callProperties.params().size() == 1 && callProperties.params().getFirst() == null)) return callProperties.idk();
            byte[] collectedBytss = new byte[callProperties.params().size()];
            for (int i = 0; i < callProperties.params().size(); i++) {
                Object val = callProperties.params().get(i);

                try {
                    if (val instanceof Integer l)
                        collectedBytss[i] = TypeConverter.toByte(l);
                    else if (val instanceof Long l)
                        collectedBytss[i] = TypeConverter.toByte(l);
                    else throw new InterpreterException.WtfAreYouDoingException(
                                callProperties.scope(), "Okay so like " + val + " just isnt something that can be represented as a byte bro.", callProperties.getLineNumber()
                        );
                } catch (IllegalArgumentException e) {
                    throw new InterpreterException.WtfAreYouDoingException(
                            callProperties.scope(), "One of the integer/long values in the array was either lwoert ahn 0 or higher than 255!", callProperties.getLineNumber()
                    );
                }
            }

            return new String(collectedBytss, StandardCharsets.UTF_8);
        }
    }


    static class FStringToByteArray extends ImplementedFunction {
        public FStringToByteArray() {
            super(
                    FunctionBuilder.start()
                            .name("t_strToByteArr")
                            .setLib("types/numbers")
                            .arguments(
                                    Arguments.getInstance().add(
                                            new AArgument(
                                                    "string",
                                                    "The string to convert into UTF-8 bytes",
                                                    false,
                                                    Argument.Type.STRING
                                            )
                                    )
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc(
                                                    "Converts the given string into UTF-8 byte values (0–255)"
                                            )
                                            .addReturns("The UTF-8 bytes as integer values")
                                            .sinceVersion("7.0.0")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());

            String s = fetch(0, String.class, callProperties);

            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            int[] ints = TypeConverter.byteArrToIntArr(bytes);

            ArrayList<Object> ints2 = new ArrayList<>(ints.length);
            for (int anInt : ints) {
                ints2.add(anInt);
            }

            return ints2;
        }
    }
}

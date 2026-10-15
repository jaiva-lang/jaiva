package com.jaiva.interpreter.libs.types;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libBuilders.func.arg.AVarArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;

import java.nio.charset.StandardCharsets;

@JaivaLibrary(path="types/numbers", description = "More complicated converters")
public class Numbers extends BaseLibrary {
    public Numbers() {
        add(
                new FDoubleToIEE754Long(),
                new FLongToIEEE754Double(),
                new FStringFromByteArray()
        );
    }

    public static class FDoubleToIEE754Long extends BaseFunction {
        public FDoubleToIEE754Long() {
            super(
                    FunctionBuilder.start()
                            .name("t_dToIEEE754Long")
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

            Object val = callProperties.params().getFirst();

            if (!(val instanceof Double d))
                throw new InterpreterException.FunctionParametersException(callProperties.scope(), this, "1", val, Double.class,
                        callProperties.getLineNumber());

            return Double.doubleToLongBits(d);
        }
    }

    public static class FLongToIEEE754Double extends BaseFunction {
        public FLongToIEEE754Double() {
            super(
                    FunctionBuilder.start()
                            .name("t_lToIEEE754Double")
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

            Object val = callProperties.params().getFirst();

            if (!(val instanceof Long l))
                throw new InterpreterException.FunctionParametersException(callProperties.scope(), this, "1", val, Long.class,
                        callProperties.getLineNumber());

            return Double.longBitsToDouble(l);
        }
    }

    static class FStringFromByteArray extends BaseFunction {
        public FStringFromByteArray() {
            super(
                    FunctionBuilder.start()
                            .name("t_strFromByteArr")
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
            if (callProperties.params().isEmpty() || (callProperties.params().size() == 1 && callProperties.params().getFirst() == null)) return Token.voidValue(callProperties.getLineNumber());
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
}

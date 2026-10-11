package com.jaiva.interpreter.libs.math;

import com.jaiva.errors.InterpreterException.FunctionParametersException;
import com.jaiva.errors.InterpreterException.WtfAreYouDoingException;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;

import java.util.concurrent.ThreadLocalRandom;

/**
 * MathBase functions ofc
 */
@JaivaLibrary(
        path = "math/base",
        description = "Contains the math functions like sqrt or ceil which don't really have their own home unique to them..."
)
public class MathBase extends BaseLibrary {

    public MathBase() {
        add(
                new FRandom(), new FRound(), new FAbs(), new FSqrt(), new FFloor(),
                new FCeil(), new FLog()
        );
        add(new MathContainer());
    }

    public static Object ifNan(TFuncCall tFuncCall, Number doubleValue) {
        int lineNumber = tFuncCall.lineNumber;
        return Double.isNaN(doubleValue.doubleValue()) ? Token.voidValue(lineNumber) : doubleValue;
    }

    /**
     * {@code m_abs(value)} -> returns the absolute value of the given number.
     */
    static class FAbs extends BaseFunction {
        FAbs() {
            super(
                    FunctionBuilder.start()
                            .name("m_abs")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(new AArgument("value",
                                                            "The value to return the value of.",
                                                            false, Argument.Type.NUMBER
                                            ))
                            ).docs(
                                    JDoc.builder()
                                            .addDesc("Returns the absolute value of a number.")
                                            .addReturns("A positive value.")
                                            .addExample("""
                                            khuluma("The absolute value of -5 is: " + m_abs(-5))!
                                            """)
                                            .sinceVersion("1.0.2")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.tFuncCall(), callProperties.scope());
            Object value = callProperties.params().getFirst();

            return switch (value) {
                case Integer i -> Math.abs(i);
                case Double d -> Math.abs(d);
                case Long l -> Math.abs(l);
                case null, default ->
                        throw new FunctionParametersException(callProperties.scope(), this, "1", value, Number.class, callProperties.tFuncCall().lineNumber);
            };

        }
    }

    /**
     * {@code m_random(lower, upper)} -> returns a random integer with [lower,
     * upper]
     */
    static class FRandom extends BaseFunction {
        FRandom() {
            super(
                    FunctionBuilder.start()
                            .name("m_random")
                            .arguments(Arguments.getInstance()
                                    .add(new AArgument(
                                            "a",
                                            "The highest number possible between `a` and 0, otherwise the lwoest between `a` and `b` (inclusive)",
                                            true, Argument.Type.NUMBER
                                    ))
                                    .add(new AArgument(
                                            "b",
                                            "The highest number possible between a and b (inclusive)",
                                            true, Argument.Type.NUMBER
                                    ))
                            )
                            .docs(JDoc.builder()
                                    .addDesc("Returns a random number in the range of `a` and `b`, If both are omitted, returns a random (double) between 0 and 1.")
                                    .addNote("Unlike other functions, if you provide no arguments, this function returns a double between 0 and 1."
                                            + " If you provide only one argument, it is treated as the upper bound, with the lower bound being 0. "
                                            + "If you're coming from a normal programming lang, don't worry both ints are inclusive")
                                    .addReturns("A random number.")
                                    .addExample("""
                                    khuluma("Random number between 1 and 10: " + m_random(1, 10))!
                                    khuluma("Random number between 0 and 5: " + m_random(5))!
                                    khuluma("Random number between 0 and 1: " + m_random())!
                                    """)
                                    .sinceVersion("1.0.0")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {

            if (callProperties.params().isEmpty())
                return ThreadLocalRandom.current().nextDouble();

            checkParams(callProperties.tFuncCall(), callProperties.scope());

            Object lowerObject = callProperties.params().getFirst();
            Object upperObject = callProperties.params().size() > 1 ?callProperties.params().get(1) : null;

            if (!(lowerObject instanceof Integer lower))
                throw new FunctionParametersException(callProperties.scope(), this, "1", lowerObject, Integer.class,
                        callProperties.tFuncCall().lineNumber);

            if (upperObject == null) {
                // only one param was given, so treat lower as upper and 0 as lower.
                // check if a is negative
                if (lower < 0)
                    throw new WtfAreYouDoingException(callProperties.scope(),
                            "When only one argument is given, it is treated as the upper bound, and the lower bound is 0. Therefore, the upper bound cannot be negative.",
                            callProperties.tFuncCall().lineNumber);
                return ThreadLocalRandom.current().nextInt(0, lower + 1);
            }

            if (!(upperObject instanceof Integer upper))
                throw new FunctionParametersException(callProperties.scope(), this, "2", upperObject, Integer.class,
                        callProperties.tFuncCall().lineNumber);

            if (lower > upper)
                throw new WtfAreYouDoingException(callProperties.scope(), "The lower bound cannot be bigger than the upper bound.",
                        callProperties.tFuncCall().lineNumber);

            // When Jaiva was truly the best
//            if (lower == 6 && upper == 9)
//                return 69;

            return ThreadLocalRandom.current().nextInt(lower, upper + 1);
        }
    }

    /**
     * {@code m_round(value)} -> rounds an approximate to the nearest integer.
     */
    static class FRound extends BaseFunction {

        FRound() {
            super(
                    FunctionBuilder.start()
                            .name("m_round")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The input to round", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Rounds the given real number to an integer.")
                                            .addReturns("An integer value, or a long if the input was already a long")
                                            .addExample("""
                                            khuluma("Rounded value of 4.6 is: " + m_round(4.6))!
                                            """)
                                            .sinceVersion("1.0.0")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.tFuncCall(), callProperties.scope());
            
            Object value = callProperties.params().getFirst();

            if (value instanceof Integer || value instanceof Long)
                return value;
            else if (value instanceof Double d)
                return (int) java.lang.Math.round(d);
            else
                throw new FunctionParametersException(callProperties.scope(), this, "1", value, Number.class, callProperties.tFuncCall().lineNumber);

        }
    }

    /**
     * {@code m_sqrt(value)} -> Returns the (positive) square root of the number
     * provided
     */
    static class FSqrt extends BaseFunction {
        FSqrt() {
            super(
                    FunctionBuilder.start()
                            .name("m_sqrt")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The input to sqrt", false, Argument.Type.NUMBER)
                            ))
                            .docs(JDoc.builder()
                                    .addDesc("Calculates the (positive) square root of the input value.")
                                    .addReturns("The suare root, or idk if the returned value is NaN")
                                    .addExample("""
                                    khuluma("The square root of 16 is: " + m_sqrt(16))!
                                    m_sqrt(-1)! @ Returns idk
                                    """)
                                    .sinceVersion("1.0.2")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.tFuncCall(), callProperties.scope());

            Object value = callProperties.params().getFirst();

            Number n = switch (value) {
                case Integer i -> Math.sqrt(i);
                case Double d -> Math.sqrt(d);
                case Long l -> Math.sqrt(l.doubleValue());
                case null, default ->
                        throw new FunctionParametersException(callProperties.scope(), this, "1", value, Number.class, callProperties.tFuncCall().lineNumber);
            };

            return ifNan(callProperties.tFuncCall(), n);

        }
    }

    /**
     * {@code m_floor(value)} - Returns the largest integer less than or equal
     * to the number provided.
     */
    static class FFloor extends BaseFunction {
        FFloor() {
            super(
                    FunctionBuilder.start()
                            .name("m_floor")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The input to round", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the largest integer less than or equal to the given value.")
                                            .addReturns("The largest integer less than or equal to the given value.")
                                            .sinceVersion("1.0.2")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.tFuncCall(), callProperties.scope());
            Object value = callProperties.params().getFirst();

            if (value instanceof Integer i)
                return i;
            else if (value instanceof Double d)
                return (int) java.lang.Math.floor(d);
            else
                throw new FunctionParametersException(callProperties.scope(), this, "1", value, Number.class, callProperties.tFuncCall().lineNumber);
        }
    }

    /**
     * {@code m_ceil(value)} - Returns the smallest integer greater than or
     * equal to the number provided.
     *
     * <p>
     * Throws {@link WtfAreYouDoingException} if:
     * <ul>
     * <li>{@code value} is not a number (Integer or Double).</li>
     * </ul>
     * </p>
     */
    static class FCeil extends BaseFunction {

        FCeil() {
            super(
                    FunctionBuilder.start()
                            .name("m_ceil")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The input to ceil", false, Argument.Type.NUMBER)
                            ))
                    .docs(
                            JDoc.builder()
                                    .addDesc("Returns the smallest integer greater than or equal to the given value.")
                                    .addReturns("The smallest integer greater than or equal to the given value.")
                                    .sinceVersion("1.0.2")
                    )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.tFuncCall(), callProperties.scope());
            Object value = callProperties.params().getFirst();

            if (value instanceof Integer i)
                return i;
            else if (value instanceof Double d)
                return (int) java.lang.Math.ceil(d);
            else
                throw new FunctionParametersException(callProperties.scope(), this, "1", value, Number.class, callProperties.tFuncCall().lineNumber);
        }
    }

    static class FLog extends BaseFunction {
        FLog() {
            super(
                    FunctionBuilder.start()
                            .name("m_log")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The value to calculate the logarithm of.",
                                            false, Argument.Type.NUMBER)
                            ).add(
                                    new AArgument("base", "The base of the logarithm, otherwise 10 is used",
                                            true, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Calculates the logarithm of a value with the specified base.")
                                            .addReturns("The logarithm of the value with the specified base.")
                                            .addExample("""
                                            khuluma("Log base 10 of 1000 is: " + m_log(1000))!
                                            khuluma("Log base 2 of 1024 is: " + m_log(1024, 2))!
                                            """)
                                            .sinceVersion("5.0.0")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.tFuncCall(), callProperties.scope());

            Object value = callProperties.params().getFirst();
            Object base =
                    callProperties.params().size() > 1 ? callProperties.params().get(1) : 10;

            if (!(value instanceof Number v))
                throw new FunctionParametersException(callProperties.scope(), this, "1", value, Number.class, callProperties.tFuncCall().lineNumber);
            if (!(base instanceof Number b))
                throw new FunctionParametersException(callProperties.scope(), this, "2", base, Number.class, callProperties.tFuncCall().lineNumber);

            double val = v.doubleValue();
            double bas = b.doubleValue();


            Number n = switch ((int) bas) {
                case 10 -> java.lang.Math.log10(val);
                case 2 -> java.lang.Math.log(val) / java.lang.Math.log(2);
                default -> java.lang.Math.log(val) / java.lang.Math.log(bas);
            };

            return ifNan(callProperties.tFuncCall(), n);
        }
    }

}

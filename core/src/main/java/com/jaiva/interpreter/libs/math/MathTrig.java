package com.jaiva.interpreter.libs.math;

import com.jaiva.errors.InterpreterException.FunctionParametersException;
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

import static com.jaiva.interpreter.libs.math.MathBase.ifNan;

@JaivaLibrary(path = "math/trig", description = "All the (basic) math functions related to the illusive triangle (trig)")
public class MathTrig extends BaseLibrary {
    public MathTrig() {
        add(
                new FSin(), new FCos(), new FTan(), new FAsin(), new FAcos(),
                new FAtan(), new FToRad(), new FToDeg(), new FAtan2()
        );
    }

    /**
     * A function that returns the sine of a number in radians.
     * <p>
     * Usage:
     * <ul>
     * <li>{@code m_sin(value)} - Returns the sine of the given value in
     * radians.</li>
     * </ul>
     * </p>
     */
    static class FSin extends BaseFunction {
        FSin() {
            super(
                    FunctionBuilder.start()
                            .name("m_sin")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The value in radians", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the sine of a number in radians.")
                                            .addReturns("The sine of the given value in radians.")
                                            .sinceVersion("1.0.2")
                            )
            );
            this.freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object v = callProperties.params().getFirst();
            // Ensure the first parameter is a number
            if (!(v instanceof Number)) {
                throw new FunctionParametersException(callProperties.scope(), this, "1", v, Number.class, callProperties.getLineNumber());
            }
            // Calculate the arctangent of the number
            double value = ((Number) v).doubleValue();
            return java.lang.Math.sin(value);
        }
    }

    /**
     * A function that returns the cosine of a number in radians.
     * <p>
     * Usage:
     * <ul>
     * <li>{@code m_cos(value)} - Returns the cosine of the given value in
     * radians.</li>
     * </ul>
     * </p>
     */
    static class FCos extends BaseFunction {
        FCos() {
            super(
                    FunctionBuilder.start()
                            .name("m_cos")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The value in radians", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the cosine of a number in radians.")
                                            .addReturns("The cosine of the given value in radians.")
                                            .sinceVersion("1.0.2")
                            )
            );
            this.freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object v = callProperties.params().getFirst();
            // Ensure the first parameter is a number
            if (!(v instanceof Number)) {
                throw new FunctionParametersException(callProperties.scope(), this, "1", v, Number.class, callProperties.getLineNumber());
            }
            // Calculate the arctangent of the number
            double value = ((Number) v).doubleValue();
            return java.lang.Math.cos(value);
        }
    }

    /**
     * A function that returns the tangent of a number in radians.
     * <p>
     * Usage:
     * <ul>
     * <li>{@code m_tan(value)} - Returns the tangent of the given value in
     * radians.</li>
     * </ul>
     * </p>
     */
    static class FTan extends BaseFunction {
        FTan() {
            super(
                    FunctionBuilder.start()
                            .name("m_tan")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The value in radians", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the trig tangent of a number in radians.")
                                            .addReturns("The trig tangent of the given value in radians.")
                                            .sinceVersion("1.0.2")
                            )
            );
            this.freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object v = callProperties.params().getFirst();
            // Ensure the first parameter is a number
            if (!(v instanceof Number)) {
                throw new FunctionParametersException(callProperties.scope(), this, "1", v, Number.class, callProperties.getLineNumber());
            }
            // Calculate the arctangent of the number
            double value = ((Number) v).doubleValue();
            return java.lang.Math.tan(value);
        }
    }

    /**
     * A function that returns the arcsine of a number in radians.
     * <p>
     * Usage:
     * <ul>
     * <li>{@code m_asin(value)} - Returns the arcsine of the given value in
     * radians.</li>
     * </ul>
     * </p>
     */
    static class FAsin extends BaseFunction {
        FAsin() {
            super(
                    FunctionBuilder.start()
                            .name("m_asin")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The value in radians", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the arc sine of a number in radians.")
                                            .addReturns("The arc sine of the given value in radians, or idk if the argument's absolute value is greater than 1")
                                            .sinceVersion("1.0.2")
                            )
            );
            this.freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object v = callProperties.params().getFirst();
            // Ensure the first parameter is a number
            if (!(v instanceof Number)) {
                throw new FunctionParametersException(callProperties.scope(), this, "1", v, Number.class, callProperties.getLineNumber());
            }
            // Calculate the arctangent of the number
            double value = ((Number) v).doubleValue();
            Number n = java.lang.Math.asin(value);

            return ifNan(callProperties.tFuncCall(), n);
        }
    }

    /**
     * A function that returns the arccosine of a number in radians.
     * <p>
     * Usage:
     * <ul>
     * <li>{@code m_acos(value)} - Returns the arccosine of the given value in
     * radians.</li>
     * </ul>
     * </p>
     */
    static class FAcos extends BaseFunction {
        FAcos() {
            super(
                    FunctionBuilder.start()
                            .name("m_acos")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The value in radians", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the arc cosine of a number in radians.")
                                            .addReturns("The arc cosine of the given value in radians, or idk if the argument's absolute value is greater than 1")
                                            .sinceVersion("1.0.2")
                            )
            );
            this.freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object v = callProperties.params().getFirst();
            // Ensure the first parameter is a number
            if (!(v instanceof Number)) {
                throw new FunctionParametersException(callProperties.scope(), this, "1", v, Number.class, callProperties.getLineNumber());
            }
            // Calculate the arctangent of the number
            double value = ((Number) v).doubleValue();
            Number n = java.lang.Math.acos(value);
            return ifNan(callProperties.tFuncCall(), n);
        }
    }

    /**
     * A function that returns the arctangent of a number in radians.
     * <p>
     * Usage:
     * <ul>
     * <li>{@code m_atan(value)} - Returns the arctangent of the given value in
     * radians.</li>
     * </ul>
     * </p>
     */
    static class FAtan extends BaseFunction {
        FAtan() {
            super(
                    FunctionBuilder.start()
                            .name("m_atan")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("value", "The value in radians", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the arc tangent of a number in radians.")
                                            .addReturns("The arc tangent of the given value in radians.")
                                            .sinceVersion("1.0.2")
                            )
            );
            this.freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object v = callProperties.params().getFirst();
            // Ensure the first parameter is a number
            if (!(v instanceof Number)) {
                throw new FunctionParametersException(callProperties.scope(), this, "1", v, Number.class, callProperties.getLineNumber());
            }
            // Calculate the arctangent of the number
            double value = ((Number) v).doubleValue();
            return java.lang.Math.atan(value);
        }
    }

    /**
     * A function that converts degrees to radians.
     * <p>
     * Usage:
     * <ul>
     * <li>{@code m_toRad(degrees)} - Converts the given degrees to radians.</li>
     * </ul>
     * </p>
     */
    static class FToRad extends BaseFunction {
        FToRad() {
            super(
                    FunctionBuilder.start()
                            .name("m_toRad")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("degrees", "The value in degrees", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Converts degrees to radians.")
                                            .addReturns("The value in radians.")
                                            .addExample("""
                                            maak deg <- 90!
                                            maak rad <- m_toRad(deg)!
                                            khuluma(rad) @ Output: 1.5707963267948966
                                            """)
                                            .sinceVersion("1.0.2")
                            )
            );
            this.freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object value = callProperties.params().getFirst();
            // Ensure the first parameter is a number
            if (!(value instanceof Number)) {
                throw new FunctionParametersException(callProperties.scope(), this, "1", value, Number.class, callProperties.getLineNumber());
            }
            // Convert degrees to radians
            double degrees = ((Number) value).doubleValue();
            return java.lang.Math.toRadians(degrees);
        }
    }

    /**
     * A function that converts radians to degrees.
     * <p>
     * Usage:
     * <ul>
     * <li>{@code m_toDeg(radians)} - Converts the given radians to degrees.</li>
     * </ul>
     * </p>
     */
    static class FToDeg extends BaseFunction {
        FToDeg() {
            super(
                    FunctionBuilder.start()
                            .name("m_toDeg")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("radians", "The value in radians", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Converts radians to degrees.")
                                            .addReturns("The value in degrees.")
                                            .addExample("""
                                            maak rad <- 1.5708!
                                            maak deg <- m_toDeg(rad)!
                                            khuluma(deg) @ Output: 90.0002104591497
                                            """)
                                            .sinceVersion("1.0.2")
                            )
            );
            this.freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object value = callProperties.params().getFirst();
            // Ensure the first parameter is a number
            if (!(value instanceof Number)) {
                throw new FunctionParametersException(callProperties.scope(), this, "1", value, Number.class, callProperties.getLineNumber());
            }
            // Convert radians to degrees
            double radians = ((Number) value).doubleValue();
            return java.lang.Math.toDegrees(radians);
        }
    }

    static class FAtan2 extends BaseFunction {
        FAtan2() {
            super(
                    FunctionBuilder.start()
                            .name("m_atan2")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("y", "The y-coordinate", false, Argument.Type.NUMBER)
                            ).add(
                                    new AArgument("x", "The x-coordinate",  false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the angle in radians between the positive x-axis and the point (x, y).")
                                            .addReturns("The angle in radians from the positive x-axis to the point (x, y).")
                                            .addNote("honestly, if you're using this function you should probably not be using Jaiva to do what"
                                            + "ever the hell you're doing...")
                                            .sinceVersion("6.0.0-alpha.3")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object v = callProperties.params().getFirst();
            Object v2 = callProperties.params().get(1);
            // Ensure the first parameter is a number
            if (!(v instanceof Number)) {
                throw new FunctionParametersException(callProperties.scope(), this, "1", v, Number.class, callProperties.getLineNumber());
            }
            if (!(v2 instanceof Number)) {
                throw new FunctionParametersException(callProperties.scope(), this, "2", v, Number.class, callProperties.getLineNumber());
            }
            // Calculate the arctangent of the number
            double value = ((Number) v).doubleValue();
            double value2 = ((Number)v2).doubleValue();
            return java.lang.Math.atan2(value, value2);
        }
    }
}
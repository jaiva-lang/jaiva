package com.jaiva.interpreter.libs.math;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AVarArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.jdoc.JDoc;

import java.util.ArrayList;

public class MathContainer extends BaseLibrary {
    public MathContainer() {
        add(
                new FGcd(), new FLcm()
        );
    }

    private ArrayList<Number> toIntegerList(ArrayList<Object> params, String funcName, BaseFunction func, Scope scope, int lineNumber) throws Exception {
        ArrayList<Number> nums = new ArrayList<>();
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            if (p instanceof Integer inte) {
                if (inte < 0)
                    throw new InterpreterException.WtfAreYouDoingException(scope, funcName + " is only defined for non-negative integers", lineNumber);
                nums.add(inte);
            } else if (p instanceof Long l) {
                if (l < 0)
                    throw new InterpreterException.WtfAreYouDoingException(scope, funcName + " is only defined for non-negative longs", lineNumber);
                nums.add(l);
            } else {
                throw new InterpreterException.FunctionParametersException(scope, func, i + "", p, Integer.class, lineNumber);
            }
        }
        return nums;
    }


    /**
     * Computes the GCD of two integers using the Euclidean algorithm.
     *
     * @param a First integer
     * @param b Second integer
     * @return GCD of a and b
     */
    private static long computeGCD(Number a, Number b) {
        long a2 = a.longValue();
        long b2 = b.longValue();
        if (b2 == 0) {
            return a2;
        }
        return computeGCD(b2, a2 % b2);
    }

    public class FGcd extends BaseFunction {
        public FGcd() {
            super(
                    FunctionBuilder.start()
                            .name("m_gcd")
                            .setLib("math/base")
                            .arguments(Arguments.getInstance().addVarArg(
                                    new AVarArgument("nums", "Variable amount of numbers to calculate the greatest common divisor of")
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Calculates the greatest common divisor (GCD) of a list of numbers.")
                                            .addReturns("The greatest common divisor of the provided numbers.")
                                            .addExample("""
                                            khuluma(m_gcd(10, 20, 42, 20))! @ Outputs: 2
                                            khuluma(m_gcd(54, 24, 36))! @ Outputs: 6
                                            """)
                                            .sinceVersion("5.0.0")
                            )
            );
            this.freeze();
        }


        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            if (callProperties.params().size() < 2)
                throw new InterpreterException.FunctionParametersException(callProperties.scope(), this, callProperties.params().size(), 2, callProperties.getLineNumber());

            ArrayList<Number> nums = toIntegerList(callProperties.params(), "gcd", this, callProperties.scope(), callProperties.getLineNumber());

            Number gcd = nums.getFirst();
            for (int i = 1; i < nums.size(); i++) {
                gcd = computeGCD(gcd, nums.get(i));
                if (gcd.longValue() == 1L) {
                    break; // GCD is 1, no need to continue
                }
            }
            return gcd;

        }
    }

    public class FLcm extends BaseFunction {
        public FLcm() {
            super(
                    FunctionBuilder.start()
                            .name("m_lcm")
                            .setLib("math/base")
                            .arguments(Arguments.getInstance().addVarArg(
                                    new AVarArgument("nums", "Variable amount of numbers to calculate the lowest common multiple of")
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Calculates the lowest common multiple (LCM) of a list of numbers.")
                                            .addReturns("The lowest common multiple of the provided numbers.")
                                            .addExample("""
                                            khuluma(m_lcm(4, 5, 6))! @ Outputs: 60
                                            khuluma(m_lcm(7, 3, 14))! @ Outputs: 42
                                            """)
                                            .sinceVersion("5.0.0")
                            )
            );
            this.freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            if (callProperties.params().size() < 2)
                throw new InterpreterException.FunctionParametersException(callProperties.scope(), this, callProperties.params().size(), 2, callProperties.getLineNumber());

            ArrayList<Number> nums = toIntegerList(callProperties.params(), "lcm", this, callProperties.scope(), callProperties.getLineNumber());

            Number lcm = nums.getFirst();
            for (int i = 1; i < nums.size(); i++) {
                Number num = nums.get(i);
                lcm = (lcm.longValue() * num.longValue()) / computeGCD(lcm, num);
            }
            return lcm;
        }
    }
}

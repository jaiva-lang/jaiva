package com.jaiva.interpreter;

import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.symbol.fn.BaseFunction;
import com.jaiva.interpreter.symbol.fn.LinkedFunction;
import com.jaiva.lang.EscapeSequence;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TExpression;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// class whre i put the long depressing code for number pair operations
// cuz java doesnt FUCKING allow you to just FUCKING compare generic FUCKING number.
// is there a better way? probably. i couldnt care less tho

final class PrimitivesMath {

    @NotNull
    private static String err(String op, @NotNull Object lhs, @NotNull Object rhs) {
        return "You cannot apply the " + op + " operator to a " + lhs.getClass().getSimpleName() + "(lhs) " +
                rhs.getClass().getSimpleName() + "(rhs) combination operation.";
    }
    /**
     * Handles arithmetic operations between two numeric operands (Integer or
     * Double).
     * Supports the following operators: "+", "-", "*", "/", "%", and "^".
     * The method performs type checking and applies the operation according to the
     * types of the operands.
     * If both operands are integers, integer arithmetic is used except for "^"
     * (power), which returns a double.
     * If either operand is a double, the operation is performed in double
     * precision.
     * Throws a CatchAllException for invalid operators.
     * Returns a void value if operands are not numeric.
     *
     * @param op         The arithmetic operator as a string ("+", "-", "*", "/",
     *                   "%", "^").
     * @param lhs        The left-hand side operand (Integer or Double).
     * @param rhs        The right-hand side operand (Integer or Double).
     * @param lineNumber The line number for error reporting.
     * @param scope Context Trace
     * @return The result of the arithmetic operation, or a void value if operands
     *         are not numeric.
     * @throws InterpreterException If an invalid operator is provided or another
     *            interpreter error occurs.
     */
    public static Object handleNumOperations(String op, Object lhs, Object rhs, int lineNumber, Scope scope)
            throws InterpreterException {
        Object result = switch (lhs) {
            case Integer iLhs when rhs instanceof Integer iRhs ->
                // Because the ^ returns a double, we use 2 variables, so that if yopu dont use
                // the ^ operator you dont receive a double output.
                    switch (op) {
                        case "+" -> iLhs + iRhs;
                        case "-" -> iLhs - iRhs;
                        case "*" -> iLhs * iRhs;
                        case "/" -> iLhs / iRhs;
                        case "%" -> iLhs % iRhs;
                        case "^" -> Math.pow(iLhs, iRhs);
                        case "&" -> iLhs & iRhs;
                        case "|" -> iLhs | iRhs;
                        case "<<" -> // bitshift left
                                iLhs << iRhs;
                        case ">>" -> // bitshift right
                                iLhs >> iRhs;
                        case "<x" -> // hexshift left
                                iLhs << (iRhs * 4);
                        case ">x" -> // hexshift right
                                iLhs >> (iRhs * 4);
                        case "#" -> iLhs ^ iRhs;
                        default -> throw new InterpreterException.CatchAllException(scope, "Invalid operator given. " + err(op, lhs, rhs), lineNumber);
                    };
            case Double iLhs when rhs instanceof Double iRhs -> switch (op) {
                case "+" -> iLhs + iRhs;
                case "-" -> iLhs - iRhs;
                case "*" -> iLhs * iRhs;
                case "/" -> iLhs / iRhs;
                case "%" -> iLhs % iRhs;
                case "^" -> Math.pow(iLhs, iRhs);
                default -> throw new InterpreterException.WtfAreYouDoingException(scope, "Invalid operator given. " + err(op, lhs, rhs), lineNumber);
            };
            case Double iLhs when rhs instanceof Integer iRhs -> switch (op) {
                case "+" -> iLhs + iRhs;
                case "-" -> iLhs - iRhs;
                case "*" -> iLhs * iRhs;
                case "/" -> iLhs / iRhs;
                case "%" -> iLhs % iRhs;
                case "^" -> Math.pow(iLhs, iRhs);
                default -> throw new InterpreterException.WtfAreYouDoingException(scope, "Invalid operator given. " + err(op, lhs, rhs), lineNumber);
            };
            case Integer iLhs when rhs instanceof Double iRhs -> switch (op) {
                case "+" -> iLhs + iRhs;
                case "-" -> iLhs - iRhs;
                case "*" -> iLhs * iRhs;
                case "/" -> iLhs / iRhs;
                case "%" -> iLhs % iRhs;
                case "^" -> Math.pow(iLhs, iRhs);
                default -> throw new InterpreterException.WtfAreYouDoingException(scope, "Invalid operator given. " + err(op, lhs, rhs), lineNumber);
            };
            case Long nLhs when rhs instanceof Integer nRhs ->  switch (op) {
                case "+" -> nLhs + nRhs;
                case "-" -> nLhs - nRhs;
                case "*" -> nLhs * nRhs;
                case "/" -> nLhs / nRhs;
                case "%" -> nLhs % nRhs;
                case "^" -> Math.pow(nLhs, nRhs);
                case "<<" -> // bitshift left
                        nLhs << nRhs;
                case ">>" -> // bitshift right
                        nLhs >> nRhs;
                case "<x" -> // hexshift left
                        nLhs << (nRhs * 4);
                case ">x" -> // hexshift right
                        nLhs >> (nRhs * 4);
                case "#" -> nLhs ^ nRhs;
                default -> throw new InterpreterException.WtfAreYouDoingException(scope, "Invalid operator given. " + err(op, lhs, rhs), lineNumber);
            };
            case Integer nLhs when rhs instanceof Long nRhs ->  switch (op) {
                case "+" -> nLhs + nRhs;
                case "-" -> nLhs - nRhs;
                case "*" -> nLhs * nRhs;
                case "/" -> nLhs / nRhs;
                case "%" -> nLhs % nRhs;
                case "^" -> Math.pow(nLhs, nRhs);
                case "<<" -> // bitshift left
                        nLhs << nRhs;
                case ">>" -> // bitshift right
                        nLhs >> nRhs;
                case "<x" -> // hexshift left
                        nLhs << (nRhs * 4);
                case ">x" -> // hexshift right
                        nLhs >> (nRhs * 4);
                case "#" -> nLhs ^ nRhs;
                default -> throw new InterpreterException.WtfAreYouDoingException(scope, "Invalid operator given. " + err(op, lhs, rhs), lineNumber);
            };
            case Long nLhs when rhs instanceof Double nRhs ->  switch (op) {
                case "+" -> nLhs + nRhs;
                case "-" -> nLhs - nRhs;
                case "*" -> nLhs * nRhs;
                case "/" -> nLhs / nRhs;
                case "%" -> nLhs % nRhs;
                case "^" -> Math.pow(nLhs, nRhs);
                default -> throw new InterpreterException.WtfAreYouDoingException(scope, "Invalid operator given. " + err(op, lhs, rhs), lineNumber);
            };
            case Double nLhs when rhs instanceof Long nRhs ->  switch (op) {
                case "+" -> nLhs + nRhs;
                case "-" -> nLhs - nRhs;
                case "*" -> nLhs * nRhs;
                case "/" -> nLhs / nRhs;
                case "%" -> nLhs % nRhs;
                case "^" -> Math.pow(nLhs, nRhs);
                default -> throw new InterpreterException.WtfAreYouDoingException(scope, "Invalid operator given. " + err(op, lhs, rhs), lineNumber);
            };
            case Long nLhs when rhs instanceof Long nRhs ->  switch (op) {
                case "+" -> nLhs + nRhs;
                case "-" -> nLhs - nRhs;
                case "*" -> nLhs * nRhs;
                case "/" -> nLhs / nRhs;
                case "%" -> nLhs % nRhs;
                case "^" -> Math.pow(nLhs, nRhs);
                case "&" -> nLhs & nRhs;
                case "|" -> nLhs | nRhs;
                case "<<" -> // bitshift left
                        nLhs << nRhs;
                case ">>" -> // bitshift right
                        nLhs >> nRhs;
                case "<x" -> // hexshift left
                        nLhs << (nRhs * 4);
                case ">x" -> // hexshift right
                        nLhs >> (nRhs * 4);
                case "#" -> nLhs ^ nRhs;
                default -> throw new InterpreterException.WtfAreYouDoingException(scope, "Invalid operator given. " + err(op, lhs, rhs), lineNumber);
            };
            case null, default -> Token.voidValue(lineNumber);
        };

        // Return int if the double is whole.
        if (result instanceof Double res) {
            double d = res;
            if (d == Math.rint(d)) {
                return (int) d;
            }
        }

        return result;
    }

    /**
     * Resolves (what used to be only string) operations between two operands (lhs and rhs) based on the
     * specified operator (op).
     * <p>
     * This method handles various string operations, including concatenation,
     * substring extraction, and comparison.
     *
     * @param lhs The left-hand side operand.
     * @param rhs The right-hand side operand.
     * @param op  The operator to be applied.
     * @param ts  The TStatement object associated with the operation.
     * @param scope Context Trace.
     * @return The result of the string operation.
     * @throws JaivaException If an error occurs during
     *                        string calculation.
     */
    public static Object resolveOverloadedOperators(Object lhs, Object rhs, String op, TExpression ts, Scope scope)
            throws JaivaException {
        String I = Integer.class.getSimpleName().charAt(0) + "";
        String S = String.class.getSimpleName().charAt(0) + "";
        String D = Double.class.getSimpleName().charAt(0) + "";
        String B = Boolean.class.getSimpleName().charAt(0) + "";
        String L = Long.class.getSimpleName().charAt(0) + "";

        String leftHandSide = lhs instanceof String ? S
                : lhs instanceof Integer ? I
                        : lhs instanceof Double ? D
                : lhs instanceof Long ? L
                                : lhs instanceof Boolean ? B :
                lhs instanceof ArrayList<?> ? "A" :
                        lhs instanceof BaseFunction ? "F" : "idk";

        String rightHandSide = rhs instanceof String ? S
                : rhs instanceof Integer ? I
                        : rhs instanceof Double ? D
                : rhs instanceof Long ? L
                                : rhs instanceof Boolean ? B :
                rhs instanceof ArrayList<?> ? "A" :
                        rhs instanceof BaseFunction ? "F" : "idk";
        if (rhs instanceof String) {
            rhs = EscapeSequence.fromEscape((String) rhs, ts.lineNumber);
        }
        if (lhs instanceof String) {
            lhs = EscapeSequence.fromEscape((String) lhs, ts.lineNumber);
        }
        String switchTing = leftHandSide + rightHandSide;

        ArrayList<String> IS = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "=", "!="));
        ArrayList<String> ArrI = new ArrayList<>(Arrays.asList("*", "+", "=", "-", "!="));
        ArrayList<String> AA = new ArrayList<>(List.of("&&", "=", "!="));
        ArrayList<String> SS = new ArrayList<>(Arrays.asList("+", "-", "=", "!=", "/", "?"));
        ArrayList<String> idk = new ArrayList<>(Arrays.asList("+", "=", "!="));
        ArrayList<String> FF = new ArrayList<>(List.of("||", "&&"));

        if (op.equals("+")) {
            if (lhs instanceof String l) {
                return l + rhs;
            } else if (rhs instanceof String r) {
                return lhs + r;
            }
        }

        if (op.equals("?") && lhs instanceof ArrayList<?> arr) {
            return arr.contains(rhs);
        }

        try {
            switch (switchTing) {
                case "FF" -> {
                    if (!FF.contains(op))
                        throw new InterpreterException.OverloadedCalcException(scope, ts);
                    return op.equals("||") || op.equals("&&") ?
                            new LinkedFunction("f", (BaseFunction)lhs, (BaseFunction) rhs,
                                    op.equals("||") ? LinkedFunction.LinkedFuncType.OR : LinkedFunction.LinkedFuncType.AND
                                    ) : null;
                }
                case "AI" -> {
                    if (!ArrI.contains(op))
                        throw new InterpreterException.OverloadedCalcException(scope, ts);
                    return op.equals("+") ? isAddAll((ArrayList) lhs, (Integer) rhs, ts, false)
                            : op.equals("-") ? new ArrayList<>(
                                    ((ArrayList) lhs).subList(0, ((ArrayList) lhs).size()-((Integer) rhs))
                            )
                            : op.equals("*") ? arrRepeat(((ArrayList) lhs), ((Integer) rhs))
                            : op.equals("=") ? false : op.equals("!=") ? true : ((ArrayList) lhs);
                }
                case "IA" -> {
                    if (!ArrI.contains(op))
                        throw new InterpreterException.OverloadedCalcException(scope, ts);
                    return op.equals("+") ? isAddAll((ArrayList) rhs, (Integer) lhs, ts, true)
                            : op.equals("-") ? new ArrayList<>(((ArrayList) rhs).subList(
                            ((Integer) lhs), ((ArrayList) rhs).size()))
                            : op.equals("*") ? arrRepeat(((ArrayList) rhs), ((Integer) lhs), true)
                            : op.equals("=") ? false : op.equals("!=") ? true : ((ArrayList) rhs);
                }
                case "AA" -> {
                    if (!AA.contains(op))
                        throw new InterpreterException.OverloadedCalcException(scope, ts);
                    return op.equals("&&") ?
                            retainAll((ArrayList)lhs, (ArrayList)rhs) :
                            op.equals("=") ? lhs.equals(rhs) :
                                    op.equals("!=") ? !lhs.equals(rhs) : null;
                }
                case "idkS" -> {
                    if (!idk.contains(op))
                        throw new InterpreterException.OverloadedCalcException(scope, ts);
                    return op.equals("=") ? false : op.equals("!=") ? true : "idk" + rhs;
                }
                case "Sidk" -> {
                    if (!idk.contains(op))
                        throw new InterpreterException.OverloadedCalcException(scope, ts);
                    return op.equals("=") ? false : op.equals("!=") ? true : lhs + "idk";
                }
                case "IS" -> {
                    if (!IS.contains(op))
                        throw new InterpreterException.OverloadedCalcException(scope, ts);
                    return op.equals("+") ? ((Integer) lhs) + ((String) rhs)
                            : op.equals("-") ? ((String) rhs).substring(
                            ((Integer) lhs))
                            : op.equals("*") ? ((String) rhs).repeat((Integer) lhs)
                            : op.equals("/")
                            ? ((String) rhs)
                            .substring(((String) rhs).length() / ((Integer) lhs))
                            : op.equals("=") ? false : op.equals("!=") ? true : ((String) rhs);
                }
                case "SI" -> {
                    if (!IS.contains(op))
                        throw new InterpreterException.OverloadedCalcException(scope, ts);
                    return op.equals("+") ? ((String) lhs) + ((Integer) rhs)
                            : op.equals("-") ? ((String) lhs).substring(0, ((String) lhs).length() - ((Integer) rhs))
                            : op.equals("*") ? ((String) lhs).repeat((Integer) rhs)
                            : op.equals("/")
                            ? ((String) lhs)
                            .substring(0, ((String) lhs).length() / ((Integer) rhs))
                            : op.equals("=") ? false : op.equals("!=") ? true : ((String) lhs);
                }
                case "SS" -> {
                    if (!SS.contains(op))
                        throw new InterpreterException.OverloadedCalcException(scope, ts);
                    return op.equals("+") ? (String) lhs + (String) rhs
                            : op.equals("-")
                            ? ((String) lhs).replaceFirst(Pattern.quote((String) rhs),
                            Matcher.quoteReplacement(""))
                            : op.equals("/")
                            ? ((String) lhs).replaceAll(Pattern.quote((String) rhs),
                            Matcher.quoteReplacement(""))
                            : op.equals("=") ? ((String) lhs).equals((String) rhs)
                            : op.equals("!=") ? !((String) lhs).equals((String) rhs)
                            : op.equals("?") ? ((String) lhs).contains((String) rhs)
                            : ((String) lhs);
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
            // too big or too small of a number
            throw new InterpreterException.OverloadedCalcException(scope, ts, e);
        } catch (IllegalArgumentException e) {
            // something received a negative number, when it shouldn't have.
            throw new InterpreterException.OverloadedCalcException(scope, ts, e);
        }
        return void.class;
    }

    @NotNull
    private static ArrayList<?> retainAll(ArrayList<Object> lhs, ArrayList<Object> rhs) {
        ArrayList<Object> l = new ArrayList<>(rhs);
        l.retainAll(new HashSet<>(lhs));
        return l;
    }

    @NotNull
    private static Object isAddAll(ArrayList arrO, Integer amount, TExpression ts, boolean left) {
        ArrayList<Object> arr = new ArrayList<>();

        if (!left) arr.addAll(arrO);
        for (int i = 0; i < amount; i++) {
            arr.add(Token.voidValue(ts.lineNumber));
        }
        if (left) arr.addAll(arrO);

        return arr;
    }

    private static Object arrRepeat(ArrayList<?> arr, Integer i) {
        return arrRepeat(arr, i, false);
    }

    private static Object arrRepeat(ArrayList<?> arr, Integer integer, boolean b) {
        ArrayList<Object> objects = new ArrayList<>();
        if (b) {
            return arr.stream().flatMap(s -> {
                ArrayList<Object> list = new ArrayList<>();
                for (int i = 0; i < integer; i++) {
                    list.add(s);
                }
                return list.stream();
            }).collect(Collectors.toCollection(ArrayList::new));
        }

        for (int i = 0; i < integer; i++) {
            objects.addAll(arr);
        }
        return objects;

    }

    @NotNull
    @Contract("_, _, null, _, _ -> fail")
    static Object compareStuff(Scope scope, TExpression tExpression, Object lhs, Object rhs, String op) throws InterpreterException.TExpressionResolutionException, InterpreterException.CatchAllException {
        // however if its these the input is a number or a double
        // check input first of all
        if (!(lhs instanceof Integer) && !(lhs instanceof Double) && !(lhs instanceof Long))
            throw new InterpreterException.TExpressionResolutionException(scope,
                    tExpression, "left hand side",
                    lhs.toString());
        if (!(rhs instanceof Integer) && !(rhs instanceof Double) && !(rhs instanceof Long))
            throw new InterpreterException.TExpressionResolutionException(scope,
                    tExpression, "right hand side",
                    rhs.toString());

        InterpreterException.CatchAllException catchAllException =
                new InterpreterException.CatchAllException(scope,"Uhm something soff bout da line", tExpression.lineNumber);

        return switch (lhs) {
            case Integer v when rhs instanceof Integer v1 ->
                // handle ints
                    switch (op) {
                        case ">=" -> v >= v1;
                        case "<=" -> v <= v1;
                        case "<" -> v < v1;
                        case ">" -> v > v1;
                        default -> throw catchAllException;
                    };
            case Double v when rhs instanceof Double v1 ->
                // handle doubles
                    switch (op) {
                        case ">=" -> v >= v1;
                        case "<=" -> v <= v1;
                        case "<" -> v < v1;
                        case ">" -> v > v1;
                        default -> throw catchAllException;
                    };
            case Long v when rhs instanceof Long v1 ->
                // handle doubles
                    switch (op) {
                        case ">=" -> v >= v1;
                        case "<=" -> v <= v1;
                        case "<" -> v < v1;
                        case ">" -> v > v1;
                        default -> throw catchAllException;
                    };
            case Integer v when rhs instanceof Double v1 ->
                // handle doubles
                    switch (op) {
                        case ">=" -> v >= v1;
                        case "<=" -> v <= v1;
                        case "<" -> v < v1;
                        case ">" -> v > v1;
                        default -> throw catchAllException;
                    };
            case Integer v when rhs instanceof Long v1 ->
                // handle doubles
                    switch (op) {
                        case ">=" -> v >= v1;
                        case "<=" -> v <= v1;
                        case "<" -> v < v1;
                        case ">" -> v > v1;
                        default -> throw catchAllException;
                    };
            case Double v when rhs instanceof Integer v1 ->
                // handle doubles
                    switch (op) {
                        case ">=" -> v >= v1;
                        case "<=" -> v <= v1;
                        case "<" -> v < v1;
                        case ">" -> v > v1;
                        default -> throw catchAllException;
                    };
            case Long v when rhs instanceof Integer v1 ->
                // handle doubles
                    switch (op) {
                        case ">=" -> v >= v1;
                        case "<=" -> v <= v1;
                        case "<" -> v < v1;
                        case ">" -> v > v1;
                        default -> throw catchAllException;
                    };
            case Long v when rhs instanceof Double v1 ->
                // handle doubles
                    switch (op) {
                        case ">=" -> v >= v1;
                        case "<=" -> v <= v1;
                        case "<" -> v < v1;
                        case ">" -> v > v1;
                        default -> throw catchAllException;
                    };
            case Double v when rhs instanceof Long v1 ->
                // handle doubles
                    switch (op) {
                        case ">=" -> v >= v1;
                        case "<=" -> v <= v1;
                        case "<" -> v < v1;
                        case ">" -> v > v1;
                        default -> throw catchAllException;
                    };
            default -> null;
        };
    }
}

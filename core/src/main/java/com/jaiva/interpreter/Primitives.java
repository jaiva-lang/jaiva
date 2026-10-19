package com.jaiva.interpreter;

import com.jaiva.errors.InterpreterException.*;
import com.jaiva.errors.Warnings;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.*;
import com.jaiva.interpreter.symbol.BaseVariable.VariableType;
import com.jaiva.interpreter.symbol.continuous.CFClosures;
import com.jaiva.lang.EscapeSequence;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.TConditional;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.*;
import com.yetnt.utils.builders.AnsiColour;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The Primitives class is a utility class that provides methods for resolving
 * string operations, converting tokens to primitives, and evaluating conditions
 * for various statements in the Jaiva programming language.
 * <p>
 * It includes methods for handling arithmetic and boolean operations, as well
 * as
 * parsing and evaluating conditions in loops and if statements.
 * <p>
 */
public final class Primitives {
    /**
     * Checks if a given {@link Symbol} has a {@link SymbolConfig} annotation and prints
     * deprecation or experimental warnings if applicable.
     *
     * @param s The {@link Symbol} to check for annotations.
     */
    private static void checkSymbolAnnotation(Symbol s, int lineNumber, Scope scope, IConfig<Object> config) throws NoWarningsException {
        if (s == null) return;
        Class<?> actual = s.getClass();
        SymbolConfig c = actual.getAnnotation(SymbolConfig.class);
        TokenDefault<?> token = s.token;
        JDoc doc = JDoc.from(token.tooltip);
        if (!Objects.isNull(c) || !Objects.isNull(doc)) {
            String depStr = Objects.isNull(doc) ? "" : doc.getDeprecatedString();
            if (!depStr.isEmpty() || (!Objects.isNull(c) && c.deprecated())) {
                Warnings.println(
                        lineNumber,
                        s.name + " is deprecated. " + AnsiColour.printInline(depStr, AnsiColour.FONT.BOLD, AnsiColour.FONT.UNDERLINE, AnsiColour.FONT.ITALIC),
                        scope, config
                );
            }
            if (!Objects.isNull(c) && c.experimental()) {
                Warnings.println(
                        lineNumber,
                        s.name + " is marked as experimental. Be careful!!",
                        scope, config
                );
            }
        }
    }

    /**
     * The bane of my existence
     * The method that turns scrambled TStatement, TFuncCall, TVarRef and primitives
     * into... primitives.
     * <p>
     * It essentially un
     * 
     * @param token the token or primitive in question
     * @param returnName Boolean flag which tells this method to only return the name of whatever you're trying to parse.
     * @param config Interpreter config
     * @param scope Context Trace
     * @return A primitive, which can be either a number, string, boolean, array, function reference or idk.
     * @throws UnknownVariableException      when the
     *                                       TVarRef cannot be
     *                                       found.
     * @throws TExpressionResolutionException
     *                                       when
     *                                       one of the sides
     *                                       of a
     *                                       {@link TExpression}
     *                                       cannot be resolved
     *                                       to a
     *                                       primitive.
     * @throws WtfAreYouDoingException
     *                                       when you try
     *                                       use a function as
     *                                       a
     *                                       variable or a
     *                                       variable as a
     *                                       function.
     * @throws WeirdAhhFunctionException
     *                                       when the
     *                                       function name
     *                                       cannot be turned
     *                                       into a proper ting
     *                                       yknow
     */
    public static Object toPrimitive(Object token, boolean returnName,
                                     IConfig<Object> config, Scope scope)
            throws Exception {
        Object parsed;
        if (token instanceof Token<?>)
            parsed = ((Token<?>) token).value();
        else
            parsed = token;
        switch (parsed) {
            case TLambda lambda -> {
                return BaseFunction.createLambda(lambda.name, lambda, scope);
            }
            case TExpression tExpression -> {
                // If the input is a TStatement, resolve the lhs and rhs.
                Object lhs = toPrimitive(tExpression.lHandSide, false, config, scope);
                String op = tExpression.op;
                Object rhs = toPrimitive(tExpression.rHandSide, false, config, scope);

                Object sTuff = PrimitivesMath.resolveStringOperations(lhs, rhs, op, tExpression, scope);
                if (sTuff != void.class)
                    return sTuff;

                // Check the input type, where input 1 is arithmatic, and 0 is boolean.
                if (tExpression.statementType == 1 || (op.equals("|") || op.equals("&"))) {
                    // check input first of all
                    if (!(lhs instanceof Integer) && !(lhs instanceof Double) && !(lhs instanceof Long))
                        throw new TExpressionResolutionException(scope,
                                tExpression, "left hand side",
                                lhs.toString());
                    if (!(rhs instanceof Integer) && !(rhs instanceof Double) && !(rhs instanceof Long))
                        throw new TExpressionResolutionException(scope,
                                tExpression, "right hand side",
                                rhs.toString());

                    // For the following if, thanks to the above condition
                    // if one is an integer then the other is an integer too
                    Object v = PrimitivesMath.handleNumOperations(op, lhs, rhs, tExpression.lineNumber, scope);
                    if (!(v instanceof TVoidValue)) {
                        return v;
                    }
                } else {
                    // In this else branch, the type is boolean logic
                    switch (op) {
                        case "&&", "||", "'" -> {
                            // if the logic operator is one of these naturally, the input has to be boolean
                            // check input first of all
                            if (!(lhs instanceof Boolean))
                                throw new TExpressionResolutionException(scope,
                                        tExpression, "left hand side",
                                        lhs.toString());
                            if (!op.equals("'") && !(rhs instanceof Boolean))
                                throw new TExpressionResolutionException(scope,
                                        tExpression, "right hand side",
                                        rhs.toString());

                            switch (op) {
                                case "&&":
                                    return ((Boolean) lhs) && ((Boolean) rhs);
                                case "||":
                                    return ((Boolean) lhs) || ((Boolean) rhs);
                                case "'":
                                    return !((Boolean) lhs);
                            }
                        }
                        case ">=", "<=", "<", ">" -> {
                            return PrimitivesMath.compareStuff(scope, tExpression, lhs, rhs, op);
                        }
                        case "=", "!=" -> {
                            // here, the inputs can be either a
                            // int, double, boolean or string.
                            // check input first of all
                            // TODO: This is literally just anything that can be input. Fix
                            if (!(lhs instanceof Integer) && !(lhs instanceof Double) && !(lhs instanceof Boolean)
                                    && !(lhs instanceof String) && !(lhs instanceof TVoidValue) && !(lhs instanceof ArrayList)
                                    && !(lhs instanceof BaseFunction) && !(lhs instanceof Long))
                                throw new TExpressionResolutionException(scope,
                                        tExpression, "left hand side",
                                        lhs.toString());
                            if (!(rhs instanceof Integer) && !(rhs instanceof Double) && !(rhs instanceof Boolean)
                                    && !(rhs instanceof String) && !(rhs instanceof TVoidValue) && !(lhs instanceof ArrayList)
                                    && !(lhs instanceof BaseFunction) && !(lhs instanceof Long))
                                throw new TExpressionResolutionException(scope,
                                        tExpression, "right hand side",
                                        rhs.toString());

                            // for TVoidValue, set to void.class
                            if (rhs instanceof TVoidValue)
                                rhs = void.class;
                            if (lhs instanceof TVoidValue)
                                lhs = void.class;

                            // handle ALL types.
                            switch (op) {
                                case "=":
                                    return lhs.equals(rhs);
                                case "!=":
                                    return !lhs.equals(rhs);
                            }
                        }
                    }

                }
            }
            case TVarRef tVarRef -> {
                // just find the reference in the table and return whatever it is
                if (returnName) {
                    Object t = toPrimitive(tVarRef.varName, returnName, config, scope);
                    if (t instanceof String)
                        return t;
                }
                MapValue v = scope.getVfs().get(tVarRef.varName instanceof Token<?>
                        ? toPrimitive(tVarRef.varName, true, config,
                        scope)
                        : (tVarRef).varName);
                Object index = (tVarRef).index == null ? null : toPrimitive(tVarRef.index, false, config, scope);
                if (index != null && (Integer) index <= -1)
                    return new WtfAreYouDoingException(scope,
                            "Now tell me, how do you access negative data in ana array?",
                            tVarRef.lineNumber);
                if (v == null)
                    throw new UnknownVariableException(scope, tVarRef);
                if (!(v.getValue() instanceof BaseVariable variable)) {
                    if (v.getValue() instanceof BaseFunction) {
                        if (index == null)
                            return v.getValue();
                        // in this case, index is something so we need to call the function and it
                        // SHOULD return an array.
                        // therefore, we want to call toPrimitive on it again
                        // If we got BaseFunction, that means tVarRef.varName is a TFuncCall.
                        Object ret = toPrimitive(tVarRef.varName, false, config, scope);
                        if (!(ret instanceof ArrayList))
                            throw new WtfAreYouDoingException(scope,
                                    "The function you used there did not return an array, and you expect to be able to index into that?",
                                    tVarRef.lineNumber);
                        return ((ArrayList<?>) ret).get((Integer) index) instanceof ArrayList && tVarRef.getLength()
                                ? ((ArrayList<?>) ((ArrayList<?>) ret).get((Integer) index)).size()
                                : ((ArrayList<?>) ret)
                                .get((Integer) index);
                    } else {
                        throw new WtfAreYouDoingException(scope, v.getValue(), BaseVariable.class,
                                tVarRef.lineNumber);
                    }
                }
                checkSymbolAnnotation(variable, tVarRef.lineNumber, scope, config);
                if (index != null && (variable.variableType == VariableType.ARRAY
                        || variable.variableType == VariableType.A_FUCKING_AMALGAMATION
                        || tVarRef.varName instanceof TVarRef)) {
                    // it's an array ref, where we have an index
                    ArrayList<Object> arr = variable.a_getAll();
                    if (tVarRef.varName instanceof Token) {
                        Object t = Primitives.toPrimitive(tVarRef.varName, returnName,
                                config, scope);
                        if (t instanceof ArrayList) arr = (ArrayList) (t);
                        else if (t instanceof String) return t;
                    }
                    if (arr.size() <= (Integer) index)
                        return new WtfAreYouDoingException(scope,
                                "Bro you're tryna access more data than there is in " + variable.name, tVarRef.lineNumber);
                    return arr.get((Integer) index) instanceof ArrayList && tVarRef.getLength()
                            ? ((ArrayList<?>) arr.get((Integer) index)).size()
                            : arr
                            .get((Integer) index);
                } else if (index == null && (variable.variableType == VariableType.ARRAY
                        || variable.variableType == VariableType.A_FUCKING_AMALGAMATION)) {
                    // return the arraylist of variables. and hope something up the chain catches
                    // the array list.
                    // that's what we assume to happen since they just like passed a reference yknow.
                    return tVarRef.getLength() ? variable.a_size() : variable.a_getAll();
                } else {
                    // normal variable ref, return it
                    try {
                        if (variable.s_get() instanceof String vs) return tVarRef.getLength() ? vs.length()
                                : (index instanceof Integer newI) ? vs.charAt(newI) + "" : variable.s_get();
                        else
                            return variable.s_get();

                    } catch (IndexOutOfBoundsException e) {
                        // user gave an invalid index.
                        throw new WtfAreYouDoingException(scope,
                                "I don't think " + variable.name + " has a position " + index,
                                tVarRef.lineNumber);
                    }
                }

            }
            case TFuncCall tFuncCall -> {
                if (returnName) {
                    Object t = toPrimitive(tFuncCall.functionName, true, config, scope);
                    if (t instanceof String)
                        return t;
                }
                Object funcName = toPrimitive(tFuncCall.functionName instanceof Token
                        ? toPrimitive(tFuncCall.functionName, true, config,
                        scope)
                        : tFuncCall.functionName, false, config, scope);

                BaseFunction function = null;
                MapValue v = null;

                if (funcName instanceof Lambda lambda) {
                    function = lambda;
                } else {
                    if (!(funcName instanceof String name))
                        throw new WeirdAhhFunctionException(scope, tFuncCall);
                    if (!(tFuncCall.functionName instanceof String)) {
                        Object j = toPrimitive(tFuncCall.functionName, false, config, scope);
                        if (j instanceof BaseFunction) function = (BaseFunction) j;
                        else return j;
                    }
                    v = scope.getVfs().get(name);
                    if (v == null)
                        throw new UnknownVariableException(scope, tFuncCall);

                    if (tFuncCall.functionName instanceof Token) {
                        Object t = toPrimitive(tFuncCall.functionName,
                                returnName, config, scope);
                        if (t instanceof BaseFunction) function = (BaseFunction) (t);
                        else if (t instanceof String) return t;

                    }

                }

                if (v != null && v.getValue() instanceof BaseVariable bn) {
                    throw new WtfAreYouDoingException(scope,
                            "You are trying to use the variable " + bn.name + " as a function.",
                            tFuncCall.lineNumber);
                }
                function = function != null ? function : (BaseFunction) v.getValue();

                if (function instanceof CFClosures.Close)
                    System.out.println("Dsf");

                checkSymbolAnnotation(function, tFuncCall.lineNumber, scope, config);

                ArrayList<Object> args = BaseFunction.resolveSpreadOperator(function, tFuncCall, config, scope);
                ArrayList<Object> newArgs = new ArrayList<>();

                if (function.isEvaluateParamsEarly()) {
                    if (args.size() != 1 || args.getFirst() != null) {
                        for (Object arg : args) {
                            newArgs.add(toPrimitive(arg, false, config, scope));
                        }
                    }
                } else {
                    newArgs = args;
                }

                Object returnValue = function.call(
                        new CallProperties(tFuncCall, newArgs, config, scope));
                return returnValue instanceof String && tFuncCall.getLength()
                        ? EscapeSequence.fromEscape((String) returnValue, tFuncCall.lineNumber).length()
                        : returnValue instanceof ArrayList && tFuncCall.getLength() ? ((ArrayList<?>) returnValue).size()
                        : returnValue;
            }
            case TTernary ternary -> {
                // parse the condition.
                Object condition = setCondition(ternary, config, scope);

                if ((Boolean) condition)
                    return toPrimitive(ternary.trueExpr, false, config, scope);
                else
                    return toPrimitive(ternary.falseExpr, false, config, scope);
            }
            default -> {
//        else if (isPrimitive(token)) {
                // Primitives include: Boolean, Integer, Double, String, TVoidValue, ArrayList
                // If we got here, then
                // its not a token so its def jus a primitive, so we wanna parse it as a
                // primitive.
                // also for the above recursive call where it may already be a primitive.
                return switch (token) {
                    case String s -> EscapeSequence.fromEscape(s, -1);
                    case Integer i -> i;
                    case Long l -> l;
                    case Double d -> {
                        double val = d;
                        if (val == Math.rint(val)) {
                            yield (int) val;
                        } else {
                            yield d;
                        }
                    }
                    case TVoidValue v -> v;
                    case Boolean b -> b;
                    case BaseFunction f -> f;
                    case ArrayList a -> {
                        ArrayList<Object> parsedArr = new ArrayList<>();
                        for (Object o : a) {
                            parsedArr.add(toPrimitive(o, false, config, scope));
                        }
                        yield parsedArr; // yield keyword for returning in switch expressions
                        // pretty cool, never used it before
                    }
                    default -> void.class;
                };
            }
        }
        return void.class;
    }

    /**
     * Checks if the given object is a primitive type (Boolean, Integer, Double, String, or TVoidValue).
     * @param t The object to be checked.
     * @return True if the object is a primitive type, false otherwise.
     */
    public static boolean isPrimitive(Object t) {
        return t instanceof Boolean || t instanceof Integer
                || t instanceof Double || t instanceof String || t instanceof TVoidValue || t instanceof ArrayList<?>;
    }

    /**
     * Evaluates and sets the condition for a `TForLoop`, `TWhileLoop`,
     * `TIfStatement` and `TTernary` statement.
     *
     * @param t   The Object containing the condition to be evaluated.
     * @param config THe interpreter configuration instance
     * @param scope Scope
     * @return The evaluated condition as an `Object`. The returned value is
     *         expected
     *         to be a `Boolean`.
     * @throws Exception If the condition cannot be resolved to a `Boolean` or if
     *                   there is an error during variable handling or parsing.
     */
    @NotNull
    public static Object setCondition(TokenDefault<?> t, IConfig<Object> config, Scope scope) throws Exception {
        Object c;

        if (t instanceof TConditional conditional)
            c = conditional.getConditionToken();
        else
            c = null;


        Object condition = Interpreter.handleVariables(c, config, scope);

        if (!(condition instanceof Boolean)) {
            assert condition != null;
            assert c != null;
            throw new TExpressionResolutionException(scope,
                    t, c instanceof Token<?>(Object value) ? (TExpression) value : ((TExpression) c),
                    "boolean", condition.getClass().getName());
        }

        return condition;
    }

}

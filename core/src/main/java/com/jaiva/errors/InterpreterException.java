package com.jaiva.errors;

import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.BaseVariable.DefinedVariable;
import com.jaiva.interpreter.symbol.DefinedFunction;
import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.*;
import com.yetnt.utils.builders.AnsiColour;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Base Interpreter Exception.
 */
public class InterpreterException extends JaivaException {

    private static final long serialVersionUID = 1L;
    public Scope scopeTrace;

    /**
     * Returns a user-friendly name for a given class or type name.
     * <p>
     * Maps internal class or type names to more readable strings:
     * <ul>
     * <li>If the name matches {@code DefinedFunction} or {@code BaseFunction},
     * returns "function".</li>
     * <li>If the name matches {@code DefinedVariable} or {@code BaseVariable},
     * returns "variable".</li>
     * <li>If the name matches {@code String}, returns "string".</li>
     * <li>If the name matches {@code int} or {@code double}, returns "number".</li>
     * <li>If the name matches {@code boolean}, returns "boolean".</li>
     * <li>Otherwise, returns the original name.</li>
     * </ul>
     *
     * @param c the class
     * @return a user-friendly string representing the type
     */
    private static String friendlyName(Class<?> c) {

        if (c.isAssignableFrom(BaseFunction.class))      return "function";
        else if (c.isAssignableFrom(BaseVariable.class)) return "variable";
        else if (c == String.class)                      return "string";
        else if (c == int.class)                         return "integer";
        else if (c == double.class)                      return "double";
        else if (c == long.class)                        return "long";
        else if (c == boolean.class)                     return "boolean";
        else if (c == ArrayList.class)                   return "array";
        else                                             return c.getSimpleName();

    }

    /**
     * Returns a user-friendly name for a given class or type name.
     * <p>
     * Maps internal class or type names to more readable strings:
     * <ul>
     * <li>If the name matches {@code DefinedFunction} or {@code BaseFunction},
     * returns "function".</li>
     * <li>If the name matches {@code DefinedVariable} or {@code BaseVariable},
     * returns "variable".</li>
     * <li>If the name matches {@code String}, returns "string".</li>
     * <li>If the name matches {@code int} or {@code double}, returns "number".</li>
     * <li>If the name matches {@code boolean}, returns "boolean".</li>
     * <li>Otherwise, returns the original name.</li>
     * </ul>
     *
     * @param s the simple name of the class or type
     * @return a user-friendly string representing the type
     */
    private static String friendlyName(String s) {
        if (s.equals(DefinedFunction.class.getSimpleName()) || s.equals(BaseFunction.class.getSimpleName()))
            return "function";
        else if (s.equals(DefinedVariable.class.getSimpleName()) || s.equals(BaseVariable.class.getSimpleName()))
            return "variable";
        else if (s.equals(String.class.getSimpleName()))
            return "string";
        else if (s.equals(int.class.getSimpleName()) || s.equals(double.class.getSimpleName()))
            return "number";
        else if (s.equals(boolean.class.getSimpleName()))
            return "boolean";
        else
            return s;
    }

    /**
     * Interpreter Exception Constructor
     * 
     * @param lineNumber the line number
     * @param message    Message to send.
     */
    public InterpreterException(Scope ct, int lineNumber, String message) {
        super(AnsiColour.print(message, AnsiColour.FORE.RED) + "\n" + AnsiColour.print(ct.toString(), AnsiColour.FORE.YELLOW), lineNumber);
        scopeTrace = ct;
    }

    public Scope getScopeTrace() {
        return scopeTrace;
    }

    public static class StreamerSuccess extends InterpreterException {
        private static final long serialVersionUID = 1L;
        public StreamerSuccess(String message, int lineNumber, Scope scope) {
            super(scope, lineNumber, message);
        }
    }

    /**
     * Exception to throw whenever a user tries to modify a symbol.
     */
    public static class FrozenSymbolException extends InterpreterException {
        private static final long serialVersionUID = 1L;

        /**
         * Base error to throw. Use this when you REALLY don't know the symbol instance.
         * This is an esolang but try be as descriptive as possible.
         * 
         * @param lineNumber the line number
         */
        public FrozenSymbolException(Scope ct, int lineNumber) {
            super(ct, lineNumber, "You tried to modify a frozen symbol. Don't do that.");
        }

        /**
         * Error to throw when we know the symbol.
         * 
         * @param s          The symbol in question
         * @param lineNumber the line number
         */
        public FrozenSymbolException(Scope ct, Symbol s, int lineNumber) {
            super(ct, lineNumber,
                    "The symbol \"" + s.name + "\" is lowkey frozen. You can't modify it.");
        }

        /**
         * Error to throw when we know the symbol but not the line number.
         * 
         * @param s The symbol in question
         */
        public FrozenSymbolException(Scope ct, Symbol s) {
            super(ct, -1, "The symbol \"" + s.name + "\" is lowkey frozen. You can't modify it.");
        }

        /**
         * Throw this overload when a user tries to modify a global symbol.
         * 
         * @param s          The symbol in question
         * @param isGlobal   true
         * @param lineNumber the line number
         */
        public FrozenSymbolException(Scope ct, Symbol s, boolean isGlobal, int lineNumber) {
            super(ct, lineNumber, "Now tell me. How do you try and modify a global symbol? Specifically, \"" + s.name
                    + "\". Shame be upon you!");
        }
    }

    /**
     * Exception to throw when a function's expected amount of parameters doesn't
     * match the amount we receive.
     */
    public static class FunctionParametersException extends InterpreterException {
        private static final long serialVersionUID = 1L;

        /**
         * Overload for too many or too little parameters (When it does accept
         * parameters)
         * 
         * @param s          base function instance
         * @param amtGiven   the amount of parameters they gave.
         * @param lineNumber the line number this happened on
         */

        public FunctionParametersException(Scope ct, BaseFunction s, int amtGiven, int lineNumber) {
            super(ct, lineNumber, s.name + "() only needs " + ((TFunction) s.token).args.length
                    + " parameter" + (amtGiven == 1 ? "" : "s") + ", and your goofy ahh gave " + amtGiven
                    + ". ☠️");
        }

        /**
         * Overload when a function expects a specific amount of parameters
         *
         * @param s          base function instance
         * @param amtGiven   the amount of parameters they gave.
         * @param amtRequired the amount of parameters required
         * @param lineNumber the line number this happened on
         */

        public FunctionParametersException(Scope ct, BaseFunction s, int amtGiven, int amtRequired, int lineNumber) {
            super(ct, lineNumber, s.name + "() needs " + amtRequired
                    + " parameter" + (amtRequired == 1 ? "" : "s") + ", and your goofy ahh gave " + amtGiven
                    + ". ☠️");
        }

        /**
         * Constructor for when a function takes no input
         * 
         * @param s          Base function instance
         * @param lineNumber the line number this happened on
         */
        public FunctionParametersException(Scope ct, BaseFunction s, int lineNumber) {
            super(ct, lineNumber, s.name + "() takes no input, why did you give it input??");
        }

        /**
         * Exception thrown when a required parameter for a function is not defined or
         * is set to an invalid value.
         *
         * @param s          The function where the parameter is missing or invalid.
         * @param nthParam   The position converted to a string.
         * @param lineNumber The line number in the source code where the exception
         *                   occurred.
         */
        public FunctionParametersException(Scope ct, BaseFunction s, String nthParam, int lineNumber) {
            super(ct, lineNumber, "The " + nthParam + (nthParam.endsWith("1") ? "st"
                    : nthParam.endsWith("2") ? "nd" : nthParam.endsWith("3") ? "rd" : "") + " parameter in " + s.name
                    + "() is required. (Either you didn't set it or set it to idk)");
        }

        /**
         * Exception thrown when a function is called with an invalid parameter type.
         *
         * @param s          The function where the parameter is invalid.
         * @param nthParam   The position of the parameter in the function call.
         * @param param      The parameter that was passed in.
         * @param expected   The expected class of the paramter
         * @param lineNumber The line number in the source code where the exception
         *                   occurred.
         */
        public FunctionParametersException(Scope ct, BaseFunction s, String nthParam, Object param,
                                           Class<?> expected,
                                           int lineNumber) {
            super(ct, lineNumber, "The " + nthParam + (nthParam.endsWith("1") ? "st"
                    : nthParam.endsWith("2") ? "nd" : nthParam.endsWith("3") ? "rd" : "th") + " parameter in " + s.name
                    + "() is required to be a " + friendlyName(expected) + ", but you gave me a "
                    + (param == null ? "null?" : friendlyName(param.getClass())) + ". Wtf bro.");
        }
    }

    /**
     * Exception to throw to warn the user, about the consequences of setting both
     * the array and scalar value of a variable.
     */
    public static class DualVariableReassignWarning extends InterpreterException {
        private static final long serialVersionUID = 1L;

        /**
         * DualVariableReassignWarning Constructor
         * 
         * @param s          The base variable the user is trying to set.
         * @param lineNumber The line number this occured on.
         */
        public DualVariableReassignWarning(Scope ct, BaseVariable s, int lineNumber) {
            // tokenizer.
            super(ct, lineNumber, s.name
                    + " has already been defined as either an array or some value. Do not reassign it as vice versa. If you know what you are doing however, place a ~ after the variable name in the declaration.");
        }

    }

    /**
     * Exception to throw when a {@link TExpression} could not be resolved to a
     * meaningful value.
     */
    public static class TExpressionResolutionException extends InterpreterException {
        private static final long serialVersionUID = 1L;

        /**
         * Constructor for when the lhs or rhs of a TStatement doesnt match the type of
         * the entire
         * statement.
         * 
         * @param s         The tstatement
         * @param side      "lhs" or "rhs"
         * @param incorrect The .toString() of the Object we received.
         */
        public TExpressionResolutionException(Scope ct, TExpression s, String side,
                                              String incorrect) {
            super(ct, s.lineNumber,
                    incorrect + " (the " + side + ") in (" + s.statement + ") is not allowed in" +
                            (s.op.equals("'") || s.op.equals("||") || s.op.equals("&&") ? " a logical"
                                    : s.statementType == 0 ? " a comparison" : " an arithmetic")
                            + " statement.");
        }

        /**
         * Constrcutor for when when some construct (like an {@link TIfStatement} or
         * {@link TWhileLoop}) expected the input {@link TExpression} to resolve to
         * something like a boolean but it resolved to something else.
         * 
         * @param outer    The construct's token which triggered the error.
         * @param s        The {@link TExpression} in question.
         * @param expected What it expected (in string form)
         * @param received What it received (in string form)
         */
        public TExpressionResolutionException(Scope ct, TokenDefault outer, TExpression s,
                                              String expected, String received) {
            super(ct, s.lineNumber,
                    "i expected (" + s.statement + ") to resolve to a " + expected
                            + ". (I'm pretty sure a " + outer.name
                            + " does not take in a" + received + ")");
        }
    }

    /**
     * Exception to throw when the user tries using a symbol that wasn't defined in
     * the current context.
     */
    public static class UnknownVariableException extends InterpreterException {
        private static final long serialVersionUID = 1L;

        /**
         * Constructor to use when we're tryig to use a variable, so we've got a
         * {@link TVarRef} instance
         * 
         * @param s The variable reference.
         */
        public UnknownVariableException(Scope ct, TVarRef s) {
            super(ct, s.lineNumber, "Lowkey can't find symbol named " + s.varName
                    + " that you used anywhere. It prolly aint in this block's scope fr.");
        }

        /**
         * Constructor used by one of the global functions which can use a string name
         * in place of a variable reference.
         * 
         * @param name       The variable name
         * @param lineNumber The line this occured on.
         */
        public UnknownVariableException(Scope ct, String name, int lineNumber) {
            super(ct, lineNumber, "Lowkey can't find the variable named " + name
                    + " that you used anywhere. It prolly aint in this block's scope fr.");
        }

        /**
         * Constructor to use when the variable the user is trying to reassign to (so a
         * {@link TVarReassign}) doesn't exist.
         * 
         * @param s The variable reassignment token
         */
        public UnknownVariableException(Scope ct, TVarReassign s) {
            super(ct, s.lineNumber, "Lowkey can't find the variable named " + s.name
                    + " that you're trying to reassign anywhere. It prolly aint in this block's scope fr.");
        }

        /**
         * Constructor to use when the function the user is trying to call (so a
         * {@link TFuncCall}), doesn't exist.
         * 
         * @param s The function call token.
         */
        public UnknownVariableException(Scope ct, TFuncCall s) {
            super(ct, s.lineNumber, "Lowkey can't find the function named " + s.functionName
                    + " that you used anywhere. It prolly aint in this block's scope fr.");
        }
    }

    /**
     * Exception to use for any generic errors which realisitclaly do not need
     * their own class.
     */
    public static class WtfAreYouDoingException extends InterpreterException {
        private static final long serialVersionUID = 1L;

        /**
         * Constructor for when the user inputs "foo" into "bar", however "bar" expects
         * "foo" to be a specific type.
         * 
         * @param s            The symbol we got
         * @param correctClass The correct class.
         * @param lineNumber   The line number this occured on.
         */
        public WtfAreYouDoingException(Scope ct, Object s, Class<?> correctClass, int lineNumber) {
            super(ct, lineNumber,
                    "Alright bub. if you're going to start mixing and matching, you might as well write lua code. "
                            + "(You tried using a " + friendlyName(s.getClass()) + " as if it were a "
                            + friendlyName(correctClass.getSimpleName()) + ")");
        }

        /**
         * Constructor for custom "wtf" errors.
         * 
         * @param s          The message to send.
         * @param lineNumber The line it occured at.
         */
        public WtfAreYouDoingException(Scope ct, String s, int lineNumber) {
            super(ct, lineNumber, s);
        }

    }

    /**
     * Exception to throw when an error happens trying to parse a function call or
     * something related to that.
     */
    public static class WeirdAhhFunctionException extends InterpreterException {
        private static final long serialVersionUID = 1L;

        /**
         * When a function name does not resolve to a string that can actualy be used
         * in the vfs HashMap.
         * <p>
         * (When we parse a {@link TFuncCall}, due to the nature of functional
         * programming, it may have layers in it's name which are a bunch of layered
         * calls or whatever, {@link Primitives} tries to recursively parse this name
         * into something we can use. If we can't get it to a string, we trhow this
         * exception as the vfs {@link HashMap} only uses strings.)
         *
         * @param tFuncCall The weird function in question.
         */
        public WeirdAhhFunctionException(Scope ct, TFuncCall tFuncCall) {
            super(ct, tFuncCall.lineNumber,
                    tFuncCall.name + " could NOT be resolved to a poper function name. Idk what else to tell you zawg");
        }
    }

    /*
     * Exception to throw when something goes wrong during a string calculation
     */
    public static class StringCalcException extends InterpreterException {
        private static final long serialVersionUID = 1L;

        /**
         * Constructor to use when the statement contains an operator which isn't a
         * string operator.
         * 
         * @param st The TStatement.
         */
        public StringCalcException(Scope ct, TExpression st) {
            super(ct, st.lineNumber,
                    st.statement + "A statement contains invalid string operations. Not telling u what tho lolol.");
        }

        /**
         * Constructor to use when a string calculation returned an error, such as
         * saying {@code "string" - 10} when the string is only 6 characters long
         * 
         * @param s The TStatement.
         * @param e The Exception that got caught.
         */
        public StringCalcException(Scope ct, TExpression s, Exception e) {
            super(ct, s.lineNumber,
                    e instanceof StringIndexOutOfBoundsException
                            ? "Bro, whatever you did, it's out of the string's bounbds. fix it pls 🙏"
                            : "So something parsed to a negative number, when sum else shouldnt have it yknow. I'm too lazy to finish writing this error tho.");
        }
    }

    /**
     * Exception to use when the user wants to throw an error.
     */
    public static class CimaException extends InterpreterException {

        private static final long serialVersionUID = 1L;

        /**
         * Default Constructor
         * 
         * @param message    The message the user wants to print.
         * @param lineNumber The line number where we can find the {@link TThrowError}
         */
        public CimaException(Scope ct, String message, int lineNumber) {
            super(ct, lineNumber, "Ight. Stopped program. Reason: " + message);
        }
    }

    /**
     * Exception to throw for any "catch all" cases that cannot actually happen but
     * need to have an error thrown as the user can input really anything they want
     * and a switch-case/ if-else chain might not catch it, but the default:/last
     * else block might catch it, then throw this error.
     * <p>
     * Essentialy a fallback exception.
     */
    public static class CatchAllException extends InterpreterException {

        private static final long serialVersionUID = 1L;

        /**
         * Represents a generic exception that is not expected to occur.
         * This exception is used as a fallback for unexpected errors.
         *
         * @param message    A detailed message describing the error.
         * @param lineNumber The line number where the error occurred.
         */
        public CatchAllException(Scope ct, String message, int lineNumber) {
            super(ct, lineNumber, "This error shouldnt happen... (" + message + ")");
        }
    }

    /**
     * Exception thrown on start up if external libraries could not be loaded.
     */
    public static class LibImportException extends InterpreterException {

        private static final long serialVersionUID = 1L;

        public LibImportException(Scope ct, String lib) {
            super(ct, -1, "The library "+lib+" could not be imported. yeah neh. (prolly aint ur fault)");
        }
    }

    /**
     * Exception thrown if the current scope is not allowed to print warnings.
     * This is used by {@link Warnings} to throw errors instead of print.
     */
    public static class NoWarningsException extends InterpreterException {

        private static final long serialVersionUID = 1L;

        public NoWarningsException(Scope ct, int linNumber, String err) {
            super(ct, linNumber, err);
        }
    }

    /**
     * Exception thrown when an exception is thrown by either the tokenizer or interpreter while its trying
     * to parse an import. The inner exception is wrapped by this exception so the user still gets
     * nice output.
     */
    public static class TseaException extends InterpreterException {
        public TseaException(Scope ct, int lineNumber, Exception exception) {
            this(ct, lineNumber, "Couldn't import symbols! " +
                    AnsiColour.print("(" + AnsiColour.remove(exception.getMessage()) + ")", AnsiColour.FONT.UNDERLINE)
            );
        }
        public TseaException(Scope ct, int lineNumber, String msg) {
            super(ct, lineNumber, msg);
        }
    }
}
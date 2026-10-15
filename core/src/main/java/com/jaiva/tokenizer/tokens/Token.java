package com.jaiva.tokenizer.tokens;

import com.jaiva.errors.JaivaException;
import com.jaiva.errors.TokenizerException;
import com.jaiva.errors.TokenizerException.CatchAllException;
import com.jaiva.errors.TokenizerException.MalformedSyntaxException;
import com.jaiva.lang.Chars;
import com.jaiva.lang.Keywords;
import com.jaiva.tokenizer.tokens.specific.*;
import com.jaiva.utils.Find;
import com.jaiva.utils.Validate;
import com.jaiva.utils.cd.ContextDispatcher;
import com.jaiva.utils.cd.ContextDispatcher.To;
import com.jaiva.utils.cd.ReservedCases;
import com.jaiva.utils.generic.BracePairs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The Token class represents a generic token that holds a value of type T.
 * <p>
 * T must extend the {@link TokenDefault} class which is the base class for all
 * tokens.
 * <p>
 * <p>
 * All tokens can be represented as a {@link Token} object, where T is the type
 * of
 * the token, and changed back to it's original type when needed.
 *
 * @param <T>   the type of the value held by this token
 * @param value The value held by this token.
 */
public record Token<T extends TokenDefault<?>>(T value) {
    /**
     * Returns the value held by this token.
     *
     * @return the value held by this token
     */
    @Override
    public T value() {
        return value;
    }

    @NotNull
    @Override
    public String toString() {
        return "Token { " +
                "name = " + value.name +
                " | value = " + value +
                " }";
    }

    /**
     * Creates a new instance of the {@link TVoidValue} inner class, representing a
     * void value token.
     *
     * @param lineNumber the line number associated with the token (currently
     *                   ignored and set to 0)
     * @return a new {@link TVoidValue} instance
     */
    @NotNull
    @Contract("_ -> new")
    public static TVoidValue voidValue(int lineNumber) {
        return new TVoidValue(lineNumber);
    }

    /**
     * Splits a string by top-level commas, ignoring commas inside parentheses or
     * brackets.
     *
     * @param argsString The string to split.
     * @return A list of strings split by top-level commas.
     */
    @NotNull
    public static List<String> splitByTopLevelComma(@NotNull String argsString) {
        List<String> parts = new ArrayList<>();
        int level = 0;
        int lastSplit = 0;
        for (int i = 0; i < argsString.length(); i++) {
            char c = argsString.charAt(i);
            if (c == '(' /* || c == '[' */) {
                level++;
            } else if (c == ')' /* || c == ']' */) {
                level--;
            } else if ((c == ',' && level == 0)) {
                if (i == 0) {
                    parts.add(argsString.substring(lastSplit, i).trim());
                    lastSplit = i + 1;
                } else if ((argsString.charAt(i - 1) != '$')) {
                    parts.add(argsString.substring(lastSplit, i).trim());
                    lastSplit = i + 1;
                }
            }
        }
        parts.add(argsString.substring(lastSplit).trim());
        return parts;
    }

    /**
     * Simplifies an identifier by adding a prefix if it doesn't contain any
     * parentheses or brackets.
     *
     * @param identifier The identifier to simplify.
     * @param prefix     The prefix to add.
     * @return The simplified identifier.
     */
    private static String simplifyIdentifier(@NotNull String identifier, String prefix) {
        return identifier.contains(")") || identifier.contains("(") || identifier.contains("[")
                || identifier.contains("]") || Validate.containsOperator(identifier.toCharArray()) != -1 ? identifier
                : prefix + identifier;
    }

    /**
     * ALl this method does is to parse any given input into either:
     * - A function call
     * - A variable reference (can be both a function reference or array access call
     * or just standrard variable)
     * - Ternary operators
     * - Or Primitives
     * <p>
     * Anything handled by this method means it can be placed as a valid construct
     * in any other construct in Jaiva.
     * <p>
     * <p>
     * NOTE : This function will call ContextDispatcher and if needed will switch to
     * use TStatement and parse as an arithmatioc operation/boolean rather than a
     * plain function call / variable reference
     *
     * @param line given line.
     * @return an atomic token
     */
    public static Object processContext(String line, int lineNumber) throws JaivaException {
        line = line.trim(); // gotta trim da line fr

        ContextDispatcher d = new ContextDispatcher(line);
        if (d.getDeligation() == To.TEXPRESSION) {
            return new TExpression(lineNumber).parse(line);
        }
        int index = Find.lastOuterMostBracePairIndex(line);

        if (index == 0) {
            // the outmost pair is just () so its prolly a TStatement, remove the stuff then
            // parse as TStatement, if it isnt a TStatement,TStatement will route back here
            // anyways.
            return new TExpression(lineNumber).parse(line.substring(1, line.length() - 1));
        }

        if (d.bits == ReservedCases.LAMBDA.code()) {
            // 19, It's a lambda!!
            // f~() : "weee"!
            String lambdaName = "__lambda__ln" + lineNumber  + "__" + UUID.randomUUID();
            BracePairs bracePairs = Find.bracePairs(line);
            int indexOfCol = line.indexOf(':');
            if (bracePairs.closedPairs().isEmpty()) throw new TokenizerException.MalformedSyntaxException(
                    "So like, are you going to add any parentheses to your lambda or..?", lineNumber);
            if (bracePairs.closedPairs().getFirst().getSecond() > indexOfCol) throw new TokenizerException.MalformedSyntaxException(
                    "How did you add a colon before the lambda's parameter list ends???", lineNumber);

            // "f~  (woot, w) : woot - 1" becomes "(woot, w)"
            String defintion = line.substring(0, indexOfCol).trim().substring(2).trim()
                    .substring(1); // now "woot, w)"
            defintion = defintion.substring(0, defintion.length() - 1); // now "woot, w"
            String[] args = defintion.split(Character.toString(Chars.ARGS_SEPARATOR));
            for (int i = 0; i < args.length; i++) args[i] = args[i].trim();


            // "f~  (bleh) : woot - 1" becomes "woot - 1"
            String implementation = line.substring(indexOfCol+1).trim();
            return new TLambda(
                    lambdaName,
                    args,
                    processContext(implementation, lineNumber),
                    lineNumber
            );
        }

        if (d.bits == ReservedCases.TERNARY.code()) {
            // 18 corresponds to the Ternary case from ContextDispatcher. See
            // ../utils/ContextDispatcher.md
            // Ternary expression
            // (cond) => true however false
            // String c = line.substring(0, line.indexOf(Chars.TERNARY)).trim();
            String c = line.substring(0, Find.lastIndexOf(line, Chars.TERNARY)).trim();
            String expressions = line.substring(Find.lastIndexOf(line, Chars.TERNARY) + 2).trim();

            Object condition = new TExpression(lineNumber).parse(c.trim());
            String expr1 = expressions.substring(0, Find.lastIndexOf(expressions, Keywords.TERNARY)).trim();
            String expr2 = expressions.substring(Find.lastIndexOf(
                            expressions, Keywords.TERNARY) + Keywords.TERNARY.length())
                    .trim();

            return new TTernary(condition, processContext(expr1, lineNumber),
                    processContext(expr2, lineNumber), lineNumber).toToken();
        } else if (d.bits == ReservedCases.PARAMS_EXTENDOR.code()) {
            // remove 2 chars at the start
            line = line.substring(2).trim();
            ArrayList<String> args = new ArrayList<>();

            if (Find.lastOuterMostBracePairIndex(line) == 0 && !line.startsWith("[")) {
                // if nraced, remove braces
                String params = line.substring(1, line.lastIndexOf(")")).trim();
                args.addAll(Token.splitByTopLevelComma(params));
            } else {
                // otherwise treat as a singular argument
                args.add(line);
            }

            if (line.length() - Chars.SPREAD.length() == line.indexOf(Chars.SPREAD)) {
                // user attempts to spread
                throw new TokenizerException.MalformedJDocException(
                        lineNumber,
                        "The spread operator cannot be applied to a function extension."
                );
            }

            ArrayList<Object> parsedArgs = new ArrayList<>();
            for (String arg : args) {
                parsedArgs.add(processContext((String) arg, lineNumber));
            }
            return new TExtendParams(parsedArgs, lineNumber).toToken();
        } else {
            if (index != -1 && (line.charAt(index) == '(')) {
                // then its a TFuncCall
                String name = line.substring(0, index).trim();
                String params = line.substring(index + 1, line.lastIndexOf(")")).trim();

                ArrayList<String> args = new ArrayList<>(Token.splitByTopLevelComma(params));
                ArrayList<Object> parsedArgs = new ArrayList<>();
                for (String arg : args) {
                    parsedArgs.add(processContext((String) arg, lineNumber));
                }
                return new TFuncCall(processContext(simplifyIdentifier(name, "F~"), lineNumber), parsedArgs,
                        lineNumber, line.charAt(line.length() - 1) == Chars.LENGTH_CHAR,
                        line.length() - Chars.SPREAD.length() == line.indexOf(Chars.SPREAD)).toToken();
            } else if (index != -1 && (line.charAt(index) == '[')) {
                // then its a variable call an array one to be specific
                // name[index]
                String name = line.substring(0, index).trim();
                String arrayIndex = line.substring(index + 1, line.lastIndexOf("]")).trim();
                return new TVarRef(
                        processContext(simplifyIdentifier(name, "V~"), lineNumber),
                        processContext(arrayIndex, lineNumber),
                        lineNumber,
                        line.charAt(line.length() - 1) == Chars.LENGTH_CHAR,
                        line.length() - Chars.SPREAD.length() == line.indexOf(Chars.SPREAD)
                        )
                        .toToken();
            } else {
                // here, processTFuncCall will try to parse "line" as a primitive type. (if not
                // a bool, int, or string)
                // if it fails, return TVarRef
                // if it succeeds, return the primitive type
                try {
                    return parseNumberLiteral(line);
                } catch (NumberFormatException e) {
                    try {
                        return Double.parseDouble(line);
                    } catch (NumberFormatException e2) {
                        if (line.equals("true") || line.equals("false") || line.equals(Keywords.TRUE)
                                || line.equals(Keywords.FALSE)) {
                            line = line.replace(Keywords.TRUE, "true").replace(Keywords.FALSE, "false");
                            return Boolean.parseBoolean(line);
                        } else if (line.contains("\"")) {
                            return line.substring(1, line.length() - 1).replace("\"", "");
                        } else if (line.isBlank()) {
                            return null;
                        } else if (line.equals(Keywords.UNDEFINED)) {
                            return new TVoidValue(lineNumber);
                        } else {
                            if (line.startsWith("V~") || line.startsWith("F~")) {
                                return line.substring(2);
                            } else {
                                return new TVarRef(line, lineNumber, line.charAt(line.length() - 1) == '~',
                                        line.endsWith(Chars.SPREAD)
                                ).toToken();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Dispatches the context based on the line and line number.
     *
     * @param line       The line to dispatch.
     * @param lineNumber The line number of the line.
     * @return The dispatched object.
     * @throws MalformedSyntaxException If there is a syntax
     *                                  critical
     *                                  error.
     * @throws TokenizerException       If there is a syntax
     *                                  error.
     */
    public static Object dispatchContext(String line, int lineNumber)
            throws JaivaException {
        line = line.trim();
        ContextDispatcher d = new ContextDispatcher(line);
        return switch (d.getDeligation()) {
            case TEXPRESSION -> new TExpression(lineNumber).parse(line);
            case PROCESS_CONTENT -> processContext(line, lineNumber);
            case SINGLE_BRACE, EMPTY_STRING ->
                    throw new MalformedSyntaxException("Okay so uhm, there's a malformed string somewhere there",
                            lineNumber);
            default -> throw new CatchAllException("yeah sum went wrong with ur dispatch code", lineNumber);
        };

    }

    /**
     * Parses a string representing an integer literal in decimal, hexadecimal,
     * binary, or octal format.
     * <p>
     * Supported formats:
     * <ul>
     * <li>Decimal: e.g., "42", "-17"</li>
     * <li>Hexadecimal: prefixed with "0x" or "0X", e.g., "0x2A", "-0X1F"</li>
     * <li>Binary: prefixed with "0b" or "0B", e.g., "0b1010", "-0B1101"</li>
     * <li>Octal: prefixed with "0c" or "0C", e.g., "0c52", "-0C17"</li>
     * </ul>
     * Leading and trailing whitespace is ignored. Negative numbers are supported.
     *
     * @param input the string to parse as an integer literal
     * @return the parsed integer value
     * @throws NumberFormatException if the input is not a valid integer literal
     */
    public static Number parseNumberLiteral(String input) throws NumberFormatException {
        input = input.trim();
        if (input.isBlank())
            throw new NumberFormatException();
        boolean negation = input.charAt(0) == '-';
        input = negation ? input.substring(1) : input; // remove the negation
        String prefix = input.length() > 1 ? input.substring(0, 2) : null;
        String end = input.substring(input.length() - 1);
        if (Arrays.asList("0x", "0X", "0b", "0B", "0c", "0C").contains(prefix)) {
            String literal = (negation ? "-" : "") + input.substring(2);
            switch (prefix) {
                case "0x", "0X":
                    return Integer.parseInt(literal, 16);
                case "0b", "0B":
                    return Integer.parseInt(literal, 2);
                case "0c", "0C":
                    return Integer.parseInt(literal, 8);
            }
        }
        if (Arrays.asList("L", "d").contains(end)) {
            String literal = (negation ? "-" : input.charAt(0)) + input.substring(1);
            switch (end) {
                case "L":
                    return Long.parseLong(literal.substring(0, literal.length() - 1));
                case "d":
                    return Double.parseDouble(literal.substring(0, literal.length() - 1));
            }
        }
        return Integer.parseInt(negation ? "-" + input : input); // allows this method to throw.
    }

}
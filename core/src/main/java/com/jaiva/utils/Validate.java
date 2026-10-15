package com.jaiva.utils;

import com.jaiva.lang.Chars;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;
import com.jaiva.tokenizer.tokens.specific.TIfStatement;
import com.jaiva.tokenizer.tokens.specific.TTernary;
import com.jaiva.tokenizer.tokens.specific.TVarRef;
import com.jaiva.utils.cd.ContextDispatcher;
import com.yetnt.utils.tuple.Pair;
import com.yetnt.utils.tuple.SamePair;

import java.util.ArrayList;

/**
 * Validate class is a utils class where methods which "validate" the
 * existance of foo in bar or some other boolean input.
 * <p>
 * Or Validate somethign such that if it fails, it throws an error.
 * 
 */
public final class Validate {
    /**
     * These are for your if, while loops, for loops, where we need to make sure
     * that its NOT an integer, string or null.
     * It has to be TFuncCall, TVarRef, TStatement or a primitive boolean.
     * 
     * @param t The object to check.
     * @return boolean indicating the given token is valid input.
     */
    public static boolean isValidBoolInput(Object t) {
        return t instanceof TFuncCall || t instanceof TVarRef || t instanceof TIfStatement
                || t instanceof Token<?> || t instanceof TTernary || t instanceof Boolean;
    }

    /**
     * Simple Check.
     * <p>
     * This is made only for {@link ContextDispatcher}
     * <p>
     * Checks if a character is considered an operator.
     * <p>
     * 
     * @param c The character
     * @return boolean indicating whether the given char is a string.
     */
    public static boolean isOperator(char c) {
        return Chars.Operators.getAllChars().contains(c);
    }

    /**
     * Checks if the given character array contains any arithmetic or boolean
     * operators.
     * Returns 1 if an arithmetic operator is found, 0 if a boolean operator is
     * found, and -1 if no operators are found.
     *
     * @param string Input
     * @return Index of operator
     */
    public static int containsOperator(char[] string) {
        for (char c : string) {
            int t = Chars.Operators.getType(c + "");
            if (t == -1)
                continue;
            else
                return t;
        }
        return -1;
    }

    /**
     * Determines if a '-' character at a specified index in the input string
     * is a unary minus (as opposed to a subtraction operator).
     * <p>
     * A '-' is considered a unary minus if the substring between the operator
     * before it and the '-' itself is empty or contains only whitespace.
     *
     * @param unaryMinusIndex         The index of the '-' character in the input
     *                                string.
     * @param opBeforeUnaryMinusIndex The index of the operator before the '-'
     *                                character.
     * @param inputString             The input string to analyze.
     * @return {@code true} if the '-' is a unary minus; {@code false} otherwise.
     * @throws StringIndexOutOfBoundsException if the indices are out of bounds
     *                                         for the given input string.
     */
    public static boolean isUnaryMinus(int unaryMinusIndex, int opBeforeUnaryMinusIndex, String inputString) {
        if (inputString.charAt(unaryMinusIndex) != '-')
            return false;
        return inputString.substring(
                opBeforeUnaryMinusIndex != 0 ? opBeforeUnaryMinusIndex + 1 : 0, unaryMinusIndex).trim().isEmpty();
    }

    /**
     * Determines if a '{@code '}' character at a specified index in the input string
     * is a logical NOT.
     * <p>
     * A '{@code '}' is considered a logical NOT if the substring between the
     * operator
     * after it and the '{@code '}' itself is empty or contains only whitespace.
     *
     * @param logicalNotIndex        The index of the '{@code '}' character in the
     *                               input
     *                               string.
     * @param opAfterLogicalNotIndex The index of the operator after the
     *                               '{@code '}'
     *                               character.
     * @param inputString            The input string to analyze.
     * @return {@code true} if the '{@code '}' is a logical NOT; {@code false}
     *         otherwise.
     * @throws StringIndexOutOfBoundsException if the indices are out of bounds
     *                                         for the given input string.
     */
    public static boolean isLogicalNot(int logicalNotIndex, int opAfterLogicalNotIndex, String inputString) {
        if (inputString.charAt(logicalNotIndex) != '\'')
            return false;
        return inputString.substring(logicalNotIndex != inputString.length() - 1 ? logicalNotIndex + 1
                : inputString
                        .length(),
                opAfterLogicalNotIndex).trim().isEmpty();
    }

    /**
     * General method to check whether `opIndex` is within the range of any one of
     * the Pair pairs in the `list`
     * <p>
     * Currently used by {@link Validate#isOpInQuotePair(String, int)} and
     * {@link Find#lastIndexOf(String, String)}
     * <p>
     * A given `index` is considered enclosed within the set of pairs if it is
     * within the domain `(first, second)` (Both numbers exclusive, as the index
     * you're looking for cannot also be part of the pair)
     * 
     * @param index The index to search for
     * @param list  An arraylist of Pair objects containing the pairs of ranges.
     *              Generally you can make this list by calling either
     *              {@link Find#quotationPairs(String)} or
     *              {@link Find#bracePairs(String)}
     * @return The index of the list in which the `index` was found to be in range.
     *          Otherwise `-1`
     */
    public static int isOpInPair(int index, ArrayList<SamePair<Integer>> list) {
        for (int i = 0; i < list.size(); i++) {
            Pair<Integer, Integer> pair = list.get(i);
            if (index > pair.getFirst() && index < pair.getSecond())
                return i;

        }
        return -1;
    }

    /**
     * Checks if the given operator index is within any pair of quotation marks in
     * the provided line.
     *
     * @param line    The input string to be analyzed.
     * @param opIndex The index of the operator to check within the line.
     * @return The index of the quotation pair (0-based) that contains the operator
     *         index,
     *         or -1 if the operator index is not within any quotation pair.
     */
    public static int isOpInQuotePair(String line, int opIndex) {
        ArrayList<SamePair<Integer>> quoteSamePairs = Find.quotationPairs(line);
        return isOpInPair(opIndex, quoteSamePairs);
    }

    /**
     * Class to represent the result of validating a symbol name.
     * This class exists such that if it is a valid symbol, we can use it.
     */
    public static class IsValidSymbolName {

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((op == null) ? 0 : op.hashCode());
            result = prime * result + (isValid ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            IsValidSymbolName other = (IsValidSymbolName) obj;
            if (op == null) {
                if (other.op != null)
                    return false;
            } else if (!op.equals(other.op))
                return false;
            return isValid == other.isValid;
        }

        /**
         * The invalid character found in the symbol name, if any.
         */
        public String op;
        /**
         * Indicates whether the symbol name is valid or not.
         */
        public boolean isValid = true;

        /**
         * Constructor for IsValidSymbolName.
         *
         * @param o the invalid character found in the symbol name
         * @param v the validity status of the symbol name
         */
        public IsValidSymbolName(String o, boolean v) {
            op = o;
            isValid = v;
        }

        /**
         * Default constructor for IsValidSymbolName.
         * Initializes the object with default values.
         */
        public IsValidSymbolName() {
        }

    }

    /**
     * Validates whether a given string is a valid symbol name.
     * <p>
     * This method checks each character in the input string against a set of
     * disallowed characters for symbol names. If any character in the string
     * is found in the disallowed set, the method returns an instance of
     * {@code IsValidSymbolName} indicating the invalid character and a
     * validation status of {@code false}. If no disallowed characters are
     * found, it returns an instance of {@code IsValidSymbolName} with a
     * validation status of {@code true}.
     *
     * @param line the string to validate as a symbol name
     * @return an {@code IsValidSymbolName} object containing the validation
     *         result and the first invalid character (if any)
     */
    public static IsValidSymbolName isValidSymbolName(String line) {
        for (char c : line.toCharArray()) {
            if (Chars.invalidCharsForSymbolName().contains(Character.toString(c))) {
                return new IsValidSymbolName(Character.toString(c), false);
            }
        }
        return new IsValidSymbolName();
    }
}

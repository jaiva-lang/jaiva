package com.jaiva.utils;

import com.jaiva.lang.Chars;
import com.jaiva.lang.Chars.Operators;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TTernary;
import com.jaiva.utils.generic.BracePairs;
import com.jaiva.utils.generic.LeastImportantOperator;
import com.jaiva.utils.generic.MultipleLinesOutput;
import com.yetnt.utils.tuple.Pair;
import com.yetnt.utils.tuple.SamePair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides utility methods for finding specific characters or
 * operators in strings,
 * as well as determining the context of mathematical or logical statements.
 * <p>
 * It includes methods for finding the index of enclosing characters,
 * identifying
 * the least important operator,
 * and handling quotation marks in strings.
 * <p>
 * The class also contains inner classes for representing multiple lines output
 * and operator indices.
 * <p>
 * Any if not all functions and or sub classes which are related to "finding"
 * something, are in this class.
 */
public final class Find {

    /**
     * Find the index of the enclosing character in a string.
     * This takes in startCount and endCount so that it doesnt have to traverse the
     * entire string again but rather just the new portion.
     * <p>
     * overload for codeblocks
     * 
     * @param line  The string to search in.
     * @param start The starting character.
     * @param end   The ending character.
     * @return a MultipleLinesOutput obj
     */
    @NotNull
    @Contract("_, _, _, _, _, _, _, _, _, _ -> new")
    public static MultipleLinesOutput closingCharIndexML(String line, @NotNull String start,
                                                         String end,
                                                         int startCount, int endCount, String previousLines, String type, String[] args, Token<?> blockChain,
                                                         int lineNumber) {
        if (start.length() > 2 || end.length() > 2)
            throw new IllegalArgumentException("Arguments must be at most 2 characters long!");
        boolean isStart = true;

        for (int i = 0; i < line.length(); i++) {
            boolean b = i + 1 < line.length() && line.charAt(i + 1) == end.charAt(1);
            boolean b1 = line.charAt(i) == end.charAt(0);
            if (!start.equals(end)) {
                if (line.charAt(i) == start.charAt(0)) {
                    startCount += (i + 1 < line.length() && line.charAt(i + 1) == start.charAt(1)) ? 1 : 0;
                } else if (b1) {
                    endCount += b ? 1 : 0;
                }
                if (startCount == endCount && startCount != 0) {
                    return new MultipleLinesOutput(startCount, endCount, previousLines + line, type,
                            args, blockChain, lineNumber);
                }
            } else {
                if (b1) {
                    if (b) {
                        return new MultipleLinesOutput(i, i, previousLines + line, type, args, blockChain, lineNumber);
                    }
                    isStart = !isStart;
                }
            }
        }
        return new MultipleLinesOutput(startCount, endCount, previousLines + line, type, args, blockChain, lineNumber);
    }

    /**
     * Find the index of the enclosing character in a string.
     * This takes in startCount and endCount so that it doesnt have to traverse the
     * entire string again but rather just the new portion.
     * 
     * @param line       The string to search in.
     * @param start      The starting character.
     * @param end        The ending character.
     * @param startCount The start count from another {@link MultipleLinesOutput}
     *                   object
     * @param endCount   The end count from another {@link MultipleLinesOutput}
     *                   object
     * @return A MultipleLinesOutput Object indicating that either this line needs continuation or its complete.
     */
    @NotNull
    @Contract("_, _, _, _, _ -> new")
    public static MultipleLinesOutput closingCharIndexML(@NotNull String line, char start, char end,
                                                         int startCount,
                                                         int endCount) {
        boolean isStart = true;
        for (int i = 0; i < line.length(); i++) {
            if (start != end) {
                if (line.charAt(i) == start) {
                    startCount++;
                } else if (line.charAt(i) == end) {
                    endCount++;
                }
                if (startCount == endCount && startCount != 0) {
                    return new MultipleLinesOutput(startCount, endCount, true);
                }
            } else {
                if (line.charAt(i) == end) {
                    if (!isStart) {
                        return new MultipleLinesOutput(i, i, true);
                    }
                    isStart = false;
                }
            }
        }
        return new MultipleLinesOutput(startCount, endCount, true);
    }

    /**
     * Find the index of the enclosing character in a string.
     * 
     * @param line  The string to search in.
     * @param start The starting character.
     * @param end   The ending character.
     * @return The index of the closing char. -1 if none found.
     */
    public static int closingCharIndex(@NotNull String line, char start, char end) {
        int startCount = 0;
        int endCount = 0;
        boolean isStart = true;
        for (int i = 0; i < line.length(); i++) {
            if (start != end) {
                if (line.charAt(i) == start) {
                    startCount++;
                } else if (line.charAt(i) == end) {
                    endCount++;
                }
                if (startCount == endCount && (startCount != 0)) {
                    return i;
                }
            } else {
                if (line.charAt(i) == end) {
                    if (!isStart) {
                        return i;
                    }
                    isStart = false;
                }
            }
        }
        return -1;
    }

    /**
     * Determines the least important operator in a given mathematical or logical
     * statement.
     * The method analyzes the statement to find operators outside of parentheses or
     * brackets,
     * categorizes them by precedence, and identifies the least important operator
     * based on
     * its type and position.
     * <p>
     * This is for the TStatement class to use.
     *
     * @param statement The input string representing a mathematical or logical
     *                  expression.
     *                  It may contain operators, parentheses, and brackets.
     * @return A {@code LeastImportantOperator} object containing the operator, its
     *         position,
     *         and its precedence group. If no operator is found, an empty
     *         {@code LeastImportantOperator} object is returned.<br>
     *
     *         The method works as follows:<br>
     *         1. Iterates through the statement to identify operators outside of
     *         parentheses or brackets.<br>
     *         2. Categorizes operators by precedence groups (e.g., exponentiation,
     *         multiplication/division,
     *         addition/subtraction, boolean operators).<br>
     *         3. Identifies the least important operator based on its precedence
     *         group and position.<br>
     *         4. Handles multi-character operators (e.g., "&&", "||", ">=", "<=",
     *         "==").<br>
     *         <br>
     *
     *         Note: The method assumes the existence of helper classes and methods
     *         such as<br>
     *         {@code Validate.isOperator(char)}, {@code Operators.getAll()}, and
     *         {@code Operators.getType(String)}.
     */
    @NotNull
    @Contract("_ -> new")
    public static LeastImportantOperator leastImportantOperator(@NotNull String statement) {
        int level = 0;
        if (statement.trim().isEmpty())
            return new LeastImportantOperator();
        ArrayList<Integer> indexes1 = new ArrayList<>();
        for (int i = 0; i < statement.length(); i++) {
            char c = statement.charAt(i);
            if ((c == '(' || c == '[') && Validate.isOpInQuotePair(statement, i) == -1)
                level++;
            else if ((c == ')' || c == ']') && Validate.isOpInQuotePair(statement, i) == -1)
                level--;
            else if (level == 0 && Validate.isOperator(c) && (Validate.isOpInQuotePair(statement, i) == -1))
                indexes1.add(i);

        }
        indexes1 = sanitizeStatement(statement, indexes1);

        if (indexes1.isEmpty())
            return new LeastImportantOperator(); // exit early if no operators are found.
        int group = -1; // 0 = Exponentiation, 1 = DivMult, 2 = AddSub, 3 = Bitwise, 4 = Comparison, 5 =
                        // Logical

        ArrayList<Pair<String, Integer>> indexes2 = new ArrayList<>(); // WHere the string, is the op itself, the
                                                                         // integer is the index.

        List<Character> multiOpChars = Arrays.asList('|', '&', '=', 'x', '>', '<'); // If the op is 2 chars long, it's
                                                                                    // last
        // char must be one of these.
        List<Character> prevOpChars = Arrays.asList('|', '&', '!', '>', '<');
        for (int i = Operators.getAllLists().size() - 1; i >= 0; i--) {
            List<String> list = Operators.getAllLists().get(i);
            char first = 0;
            for (int opIndex : indexes1) {
                String op = statement.substring(opIndex, opIndex + 1);
                boolean isMulti = first != 0 || (opIndex + 1 != statement.length() && multiOpChars.contains(statement.charAt(opIndex + 1)));
                char prevChar = opIndex > 0 ? statement.charAt(opIndex - 1) : 0;
                op = statement.substring(
                        opIndex,
                        isMulti ? (opIndex + 2)
                                : (opIndex + 1))
                        .trim();

                // String test = (first + op).trim();

                if (isMulti && first == 0)
                    first = op.charAt(0);
                else if (isMulti && prevChar != 0 && prevOpChars.contains(prevChar)
                /* && Operators.getType(test) < i */) {
                    first = 0;
                    continue; // skip this iteration as we got it earlier.
                } else {
                    // clean up first
                    first = 0;
                }

                if (!list.contains(op))
                    continue;

                if (group == -1) {
                    group = i;
                    indexes2.add(new Pair<>(op, opIndex));
                } else if (group == Operators.getType(op)) {
                    indexes2.add(new Pair<>(op, opIndex));
                }
            }
        }

        if (indexes2.isEmpty())
            return new LeastImportantOperator();

        Pair<String, Integer> ipair = indexes2.getLast();

        return new LeastImportantOperator(
                ipair.getFirst(), ipair.getSecond(),
                group);
    }

    /**
     * Sanitizes a list of operator indexes in a given statement by removing indexes
     * that correspond to unary minus operators and logical not operators, while
     * preserving other operator
     * indexes.
     *
     * @param statement The input string containing the statement to be analyzed.
     * @param opIndexes A list of integer indexes representing the positions of
     *                  operators
     *                  in the statement.
     * @return A sanitized list of operator indexes, excluding those that
     *         correspond to unary minus operators.
     */
    @NotNull
    public static ArrayList<Integer> sanitizeStatement(String statement, ArrayList<Integer> opIndexes) {
        ArrayList<Integer> remInt = removeUNARY(statement, opIndexes);

        return removeNOT(statement, remInt);
    }

    @NotNull
    public static ArrayList<Integer> removeUNARY(String statement, @NotNull ArrayList<Integer> opIndexes) {
        ArrayList<Integer> sanitized = new ArrayList<>();

        for (int i = 0; i < opIndexes.size(); i++) {
            int opIndex = opIndexes.get(i);
            if (i == 0 && opIndexes.size() == 1) {
                // if (!(statement.charAt(opIndex) == '-'))
                sanitized.add(opIndex);
                continue;

            }
            int opIndexBeforeUnary = i != 0 ? opIndexes.get(i - 1) : 0;
            boolean isUnary = Validate.isUnaryMinus(opIndex, opIndexBeforeUnary, statement);
            if (!isUnary)
                sanitized.add(opIndex);
        }

        return sanitized;
    }

    @NotNull
    public static ArrayList<Integer> removeNOT(String statement, @NotNull ArrayList<Integer> opIndexes) {
        ArrayList<Integer> sanitized = new ArrayList<>();

        for (int i = 0; i < opIndexes.size(); i++) {
            int opIndex = opIndexes.get(i);
            if (i == opIndexes.size() - 1 && opIndexes.size() == 1) {
                // if (!(statement.charAt(opIndex) == '\''))
                sanitized.add(opIndex);
                continue;

            }
            int opIndexAfterNOT = i != opIndexes.size() - 1 ? opIndexes.get(i + 1) : statement.length() - 1;
            boolean isLogicalNot = Validate.isLogicalNot(opIndex, opIndexAfterNOT, statement);
            if (!isLogicalNot)
                sanitized.add(opIndex);
        }

        return sanitized;
    }

    /**
     * Get the index of the operator in the given character array.
     * 
     * @param string String to find an operator index.
     * @return An int corresponding to the operator index. Note since it takes in a character array it only finds single operators and not multi
     */
    public static int operatorIndex(@NotNull char[] string) {
        for (int i = 0; i < string.length; i++) {
            if (Operators.getAllChars().contains(string[i]))
                return i;
        }
        return -1;
    }

    /**
     * Finds and returns all pairs of indices representing the positions of matching
     * quotation marks in the given string. Each pair consists of the starting index
     * of an opening quotation mark and the ending index of the corresponding
     * closing
     * quotation mark.
     *
     * @param line The input string to search for quotation mark pairs.
     * @return An ArrayList of Pair objects, where each Pair contains two
     *         integers:
     *         the starting and ending indices of a pair of matching quotation
     *         marks.
     *         If no pairs are found, an empty list is returned.
     */
    @NotNull
    public static ArrayList<SamePair<Integer>> quotationPairs(@NotNull String line) {
        ArrayList<SamePair<Integer>> arr = new ArrayList<>();
        int oldCharIndex = -1;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            char before = i > 0 ? line.charAt(i - 1) : Character.MIN_VALUE;
            char before2 = i > 1 ? line.charAt(i - 2) : Character.MIN_VALUE;
            if (c == '"' && (before != Chars.ESCAPE || before2 == Chars.ESCAPE)) {
                if (oldCharIndex == -1) {
                    oldCharIndex = i;
                } else {
                    arr.add(new SamePair<>(oldCharIndex, i));
                    oldCharIndex = -1;
                }
            }
        }
        return arr;
    }

    /**
     * Finds and returns all pairs of indices representing the positions of matching
     * closing and opening parenthesis and brackets in the given string. Each pair
     * consists of the starting index
     * of an opening opening brace and the ending index of the corresponding
     * closing brace.
     * <p>
     * Both [] and ()
     *
     * @param line The input string to search for quotation brace pairs.
     * @return A Pair containing two ArrayLists:
     * - The first ArrayList contains pairs of indices representing the
     * beginning
     * and end of matching braces.
     * - The second ArrayList contains pairs of indices representing
     * unmatched
     * braces.
     */
    @NotNull
    @Contract("_ -> new")
    public static BracePairs bracePairs(
            @NotNull String line) {
        ArrayList<SamePair<Integer>> finalArr = new ArrayList<>();
        ArrayList<Pair<Integer, Character>> stack = new ArrayList<>();
        ArrayList<Pair<Integer, Character>> danglingclose = new ArrayList<>();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if ((c == '[' || c == '(') && Validate.isOpInQuotePair(line, i) == -1)
                stack.add(new Pair<>(i, c));

            if ((c == ']' || c == ')') && Validate.isOpInQuotePair(line, i) == -1) {
                if (stack.isEmpty()) {
                    danglingclose.add(new Pair<>(i, c));
                    continue;
                }
                Pair<Integer, Character> t = stack.getLast();
                if ((t.getSecond() == '[' && c == ']') || (t.getSecond() == '(' && c == ')')) {
                    finalArr.add(new SamePair<>(t.getFirst(), i));
                    stack.removeLast();
                }
            }
        }

        return new BracePairs(finalArr, stack, danglingclose);
    }



    /**
     * Returns the index of the opening brace which is part of the last,
     * outermost brace pair. Both () and [] are checked for agasinst each other.
     * This will r
     * <p>
     * <blockquote>
     *
     * <pre>
     * "func()" returns 4
     * "()[]" returns 2
     * "()" returns 0
     * </pre>
     *
     * </blockquote>
     * </p>
     *
     * @param line Input.
     * @return the index where the outermost brace pair starts
     * @implNote Specialization of {@link #lastOutermostBracePair(String)}
     */
    public static int lastOuterMostBracePairIndex(String line) {
        SamePair<Integer> last = lastOutermostBracePair(line);
        return last == null ? -1 : last.getFirst();
    }


    /**
     * Returns the pair of indexes corresponding to the last,
     * outermost brace pair. Both () and [] are checked for against each other.
     * <p>
     * <blockquote>
     *
     * <pre>
     * "func()" returns SamePair(4, 5)
     * "()[]" returns SamePair(2, 3)
     * "()" returns SamePair(0, 1)
     * </pre>
     *
     * </blockquote>
     * </p>
     *
     * @param line Input.
     * @return the pair where the outermost brace pair starts
     */
    @Nullable
    public static SamePair<Integer> lastOutermostBracePair(String line) {
        BracePairs bp = Find.bracePairs(line);
        ArrayList<SamePair<Integer>> closed = bp.closedPairs();
        if (closed.isEmpty()) return null;
        return closed.getLast();
    }

    /**
     * Finds the last index of the given input string in statement, while
     * disregarding input enclosed in quotation marks or inside a set of brackets []
     * or parenthesis ()
     * <p>
     * This is meant for {@link Token#processContext(String, int, com.jaiva.tokenizer.TConfig)} to properly split
     * nested {@link TTernary} at the correct places. It can be used elsewhere
     * provided with caution
     * 
     * @param statement The statement to check within
     * @param input     The input to check for
     * @return the integer corresponding to the index of the first character of the
     *          found input in the statement
     *          Else: -1 if no valid index is found.
     */
    public static int lastIndexOf(String statement, String input) {
        int index = -1;
        boolean containsIndex = true;

        while (containsIndex) {
            int i = statement.indexOf(input);

            if (i == -1) {
                containsIndex = false;
                continue;
            }
            if (Validate.isOpInQuotePair(statement, i) == -1
                    && Validate.isOpInPair(i, Find.bracePairs(statement).closedPairs()) == -1)
                index = i;
            statement = statement.replaceFirst(input, "-".repeat(input.length()));
        }

        return index;
    }

}

package com.jaiva.lang;

import com.jaiva.errors.TokenizerException;
import com.jaiva.utils.Find;
import io.github.yetnt.utils.tuple.Pair;
import io.github.yetnt.utils.tuple.SamePair;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * EscapeSequence class is a utility class that provides methods for escaping
 * and
 * unescaping strings.
 * <p>
 * This class is used to escape and unescape strings for use in Jaiva source
 * code.
 * <p>
 * The escape and unescape methods are used to replace special characters with
 * their escaped equivalents and vice versa.
 */
public class EscapeSequence {
    private static final ArrayList<Character> unescapeChars = new ArrayList<>(
            Arrays.asList('=', ',', '!', '@', '\n', '\t', '\r', '\b', '\f', '\"', '$'));

    private static final ArrayList<Character> escapeChars = new ArrayList<>(
            Arrays.asList('=', ',', '!', '@', 'n', 't', 'r', 'b', 'f', '"', '$'));

    /**
     * Escapes special sequences in the given string according to custom escape
     * rules.
     * <p>
     * This method replaces custom escape sequences (prefixed with '$') with their
     * corresponding
     * characters. If the string does not contain the escape character, it performs
     * a fast replacement
     * for known sequences. Otherwise, it processes the string character by
     * character to handle
     * more complex or malformed escape sequences, throwing an exception if an
     * invalid escape is found.
     * </p>
     *
     * <ul>
     * <li><code>$=</code> &rarr; <code>=</code></li>
     * <li><code>$,</code> &rarr; <code>,</code></li>
     * <li><code>$!</code> &rarr; <code>!</code></li>
     * <li><code>$@</code> &rarr; <code>@</code></li>
     * <li><code>$n</code> &rarr; newline (<code>\n</code>)</li>
     * <li><code>$t</code> &rarr; tab (<code>\t</code>)</li>
     * <li><code>$r</code> &rarr; carriage return (<code>\r</code>)</li>
     * <li><code>$b</code> &rarr; backspace (<code>\b</code>)</li>
     * <li><code>$f</code> &rarr; form feed (<code>\f</code>)</li>
     * <li><code>$"</code> &rarr; double quote (<code>"</code>)</li>
     * <li><code>$$</code> &rarr; dollar sign (<code>$</code>)</li>
     * </ul>
     *
     * @param str        the input string to escape
     * @param lineNumber the line number for error reporting
     * @return the escaped string with all valid escape sequences replaced
     * @throws TokenizerException if an invalid escape sequence is encountered
     */
    public static String fromEscape(String str, int lineNumber) throws TokenizerException {
        if (!str.contains(Character.toString(Chars.ESCAPE)))
            return str
                    .replace("$=", "=")
                    .replace("$,", ",")
                    .replace("$!", "!")
                    .replace("$@", "@")
                    .replace("$n", "\n")
                    .replace("$t", "\t")
                    .replace("$r", "\r")
                    .replace("$b", "\b")
                    .replace("$f", "\f")
                    .replace("$\"", "\"")
                    .replace("$$", "$");
        // If it contaisn the $ character, go through the entire string.
        StringBuilder build = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            char prevChar = i != 0 ? str.charAt(i - 1) : Character.MIN_VALUE;
            char nextChar = i != str.length() - 1 ? str.charAt(i + 1) : Character.MAX_VALUE;

            // check the current char.
            if (escapeChars.contains(c)) {
                // we found something that can possible be escaped, check before said char to
                // make sure it's not already.
                if (c == Chars.ESCAPE) {
                    // check before and after.
                    if (prevChar != Chars.ESCAPE && nextChar == Chars.ESCAPE) {
                        // turns 2 $ into 1 $
                        build.append(c);
                    } else if (prevChar != Chars.ESCAPE && !escapeChars.contains(nextChar)) {
                        // these are invalid $ escape sequences.
                        throw new TokenizerException.MalformedSyntaxException(
                                "Somewhere along the lines, an escape character was used wrongfully.", lineNumber);
                    }
                    // Essentially, if the previous character is $ and the current is a $, we skip
                    // this, as the ealier call we got this already.
                    // this also ensures, if we get something like $t, it skips parsing $ but will
                    // parse t
                } else {
                    // only add if the previous char is a $
                    if (prevChar == Chars.ESCAPE) {
                        switch (c) {
                            case '=':
                                build.append('=');
                                break;
                            case ',':
                                build.append(',');
                                break;
                            case '!':
                                build.append('!');
                                break;
                            case '@':
                                build.append('@');
                                break;
                            case 'n':
                                build.append('\n');
                                break;
                            case 't':
                                build.append('\t');
                                break;
                            case 'r':
                                build.append('\r');
                                break;
                            case 'b':
                                build.append('\b');
                                break;
                            case 'f':
                                build.append('\f');
                                break;
                            case '"':
                                build.append('"');
                                break;
                        }
                    } else {
                        build.append(c);
                    }
                }
            } else {
                // append the usual char.
                build.append(c);
            }
        }

        return build.toString();
    }

    /**
     * Escapes special characters in the given string according to custom rules.
     * <p>
     * If the string does not contain the escape character (as defined by
     * {@code Chars.ESCAPE}),
     * it performs a series of replacements for specific characters (such as
     * {@code $, =, ,, !, @, \n, \t, \r, \b, \f, "}).
     * Each special character is replaced with a corresponding escape sequence
     * (e.g., {@code $} becomes {@code $$},
     * {@code =} becomes {@code $=}, {@code \n} becomes {@code $n}, etc.).
     * </p>
     * <p>
     * If the string contains the escape character, the method processes the string
     * character by character,
     * ensuring that escape sequences are correctly applied and that existing escape
     * characters are handled properly.
     * </p>
     *
     * @param str the input string to escape
     * @return the escaped string with special characters replaced by their escape
     *         sequences
     */
    public static String toEscape(String str) {
        if (!str.contains(Character.toString(Chars.ESCAPE)))
            return str
                    .replace("$", "$$")
                    .replace("=", "$=")
                    .replace(",", "$,")
                    .replace("!", "$!")
                    .replace("@", "$@")
                    .replace("\n", "$n")
                    .replace("\t", "$t")
                    .replace("\r", "$r")
                    .replace("\b", "$b")
                    .replace("\f", "$f")
                    .replace("\"", "$\"");
        // If it contaisn the $ character, go through the entire string.
        StringBuilder build = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            char prevChar = i != 0 ? str.charAt(i - 1) : Character.MIN_VALUE;
            char nextChar = i != str.length() - 1 ? str.charAt(i + 1) : Character.MAX_VALUE;

            // check the current char.
            if (unescapeChars.contains(c)) {
                if (c == Chars.ESCAPE) {
                    if (!escapeChars.contains(nextChar) && prevChar != Chars.ESCAPE) {
                        // single $, push double $$
                        build.append(c);
                        build.append(c);
                    } else if (escapeChars.contains(nextChar)) {
                        build.append(c);
                    }
                } else {
                    if (prevChar != Chars.ESCAPE) {
                        build.append(Chars.ESCAPE);

                        switch (c) {
                            case '\n':
                                build.append('n');
                                break;
                            case '\t':
                                build.append('t');
                                break;
                            case '\r':
                                build.append('r');
                                break;
                            case '\b':
                                build.append('b');
                                break;
                            case '\f':
                                build.append('f');
                                break;
                            default:
                                build.append(c);
                                break;
                        }
                    } else {
                        build.append(c);
                    }

                }
            } else {
                // append the usual char.
                build.append(c);
            }
        }

        return build.toString();
    }

    /**
     * Escape a string for JSON.
     *
     * @param str The string to escape.
     * @return JSON with escape codes escaped.
     */
    public static String escapeJson(String str) {
        return str
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\t", "\\t")
                .replace("\r", "\\r")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\"", "\\\"");
    }

    /**
     * Escape all strings in a line.
     * 
     * @deprecated Use {@link EscapeSequence#escapeAll(String)}. This only exists if
     *             code which relied on this breaks and i need to use this again. (I
     *             preferably would not like to)
     * @param line Line to escape
     * @return The line with everything Jaiva-esque escaoed.
     */
    public static String oldEscapeAll(String line) {
        // maak a <- "heelo"!
        // func("weird thing", "with multiple strings")
        // We have to escape anything in EVEN number stuff (ODD number in 0-based
        // indexing)
        // 0, 1, 2, 3, 4
        // all the actual strings are stored in 1, 3, etc
        String[] parts = line.split("(?<!\\$)\""); // Negative lookbehind checks that their isnt $ before "
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i % 2 != 0) {
                String unescapedString = parts[i]
                        .split("((?<!\\$)(\\\"|\\!|\\\\|\\\"|@|=))|(\\t|\\n|\\s)").length != 1
                                ? toEscape(parts[i])
                                : parts[i];
                b.append("\"" + unescapedString + "\"");
            } else {
                b.append(parts[i]);
            }
        }

        return b.toString();
    }

    /**
     * This method escapes all contents within string literals.
     * <p>
     * This uses the {@link Find#quotationPairs(String)} which if there is an
     * unclosed string, does not consider as a pir.
     * 
     * @param line Line to escape
     * @return Line with all jaiva special stuff escaped.
     */
    public static String escapeAll(String line) {
        ArrayList<SamePair<Integer>> samePairs = Find.quotationPairs(line);
        if (samePairs.isEmpty())
            return line;
        StringBuilder b = new StringBuilder();
        // put the substring before the first pair
        b.append(line.substring(0, samePairs.getFirst().getFirst() + 1)); // include the first "
        for (int i = 0; i < samePairs.size(); i++) {
            Pair<Integer, Integer> pair = samePairs.get(i);
            String sub = line.substring(pair.getFirst() + 1, pair.getSecond());
            b.append(toEscape(sub));
            // append " then the rest of the string, and then another "
            b.append(line.substring(
                    pair.getSecond(),
                    i != samePairs.size() - 1 ? samePairs.get(i + 1).getFirst() + 1 : line.length()));

        }

        return b.toString();
    }
}
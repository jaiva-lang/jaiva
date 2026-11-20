package com.jaiva.lang;

import com.jaiva.tokenizer.tokens.specific.TDocsComment;

import java.util.ArrayList;
import java.util.List;

/**
 * Comments class is a utility class that provides methods for processing and
 * manipulating comments in strings.
 * <p>
 * This class includes methods for removing single line comments, checking if an
 * array of strings contains only comments, and safely decimating strings to
 * remove comments.
 */
public class Comments {


    /**
     * This method is a refactored version of decimate that does not remove blank lines.
     * It processes an array of strings, removing comments from lines that do not
     * contain a newline character.
     * @param lines an array of strings to be processed
     * @return a new array of strings with comments removed from lines
     */
    public static String[] decimate(String[] lines) {
        List<String> newLines = new ArrayList<>();
        for (String s : lines) {
            String line = s;
            if (!line.contains("\n")) {
                line = Comments.decimate(line.trim());
            }
            newLines.add(line);
        }
        return newLines.toArray(new String[0]);
    }

    /**
     * Removes single line comments.
     * @param line The line
     * @return The line without the comment.
     */
    public static String decimate(String line) {
        int commentIdx = line.indexOf(Chars.COMMENT);
        if (commentIdx == -1 || line.indexOf(Chars.COMMENT_DOC) == commentIdx)
            return line;
        if (commentIdx > 0 && line.charAt(commentIdx - 1) == Chars.ESCAPE)
            return line;
        String out = line.substring(0, commentIdx);
        // Replace comment with a newline to preserve line structure
        return out/* + ' '*/;
    }

    /**
     * Safely decimates a given string by checking if it starts with a specific
     * comment
     * marker and processes it accordingly.
     *
     * @param o The input string to be processed.
     * @return The original string if it does not start with the comment marker,
     *         otherwise a tokenized representation of the substring up to the
     *         comment marker.
     */
    public static Object safeDecimate(String o) {
        if (o.indexOf(Chars.COMMENT_DOC) != 0)
            return decimate(o);

        return new TDocsComment(o.substring(o.indexOf(Chars.COMMENT_DOC) + 2))
                .toToken();

    }

    /**
     * Check if the array is only comments. This is used to check if the line
     * contains only comments or not.
     * <p>
     * This is a helper method where a ! may be splitting inside a comment which had
     * a double @ symbol.
     * 
     * @param arr Array to check against
     * @return boolean indicating whether the array of lines only has comments.
     */
    public static boolean arrayIsOnlyComments(String[] arr) {
        if (arr.length > 2) {
            for (String s : arr) {
                if (!s.trim().isEmpty() && !s.trim().startsWith(Character.toString(Chars.COMMENT))) {
                    return false;
                }
            }
            return true;
        } else {
            // if one of the elements is a comment, and the other isnt, return true.
            // check both elements.
            // otherwise return false.
            if (arr[0].trim().isEmpty() && arr[1].trim().isEmpty()) {
                return true;
            } else if (arr[0].trim().isEmpty() && arr[1].trim().startsWith(Character.toString(Chars.COMMENT))) {
                return true;
            } else if (arr[0].trim().startsWith(Character.toString(Chars.COMMENT)) && arr[1].trim().isEmpty()) {
                return true;
            } else return arr[0].trim().startsWith(Character.toString(Chars.COMMENT))
                    && !arr[1].trim().startsWith(Character.toString(Chars.COMMENT))
                    || !arr[0].trim()
                    .startsWith(Character.toString(Chars.COMMENT))
                    && arr[1].trim().startsWith(Character.toString(Chars.COMMENT));

        }
    }

}

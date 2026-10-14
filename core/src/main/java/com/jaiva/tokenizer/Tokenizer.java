package com.jaiva.tokenizer;

import com.jaiva.Main;
import com.jaiva.errors.JaivaException;
import com.jaiva.errors.TokenizerException;
import com.jaiva.errors.TokenizerException.CatchAllException;
import com.jaiva.errors.TokenizerException.FileOrDirectoryNotFoundException;
import com.jaiva.errors.TokenizerException.MalformedSyntaxException;
import com.jaiva.errors.TokenizerException.TypeMismatchException;
import com.jaiva.lang.Chars;
import com.jaiva.lang.Comments;
import com.jaiva.lang.EscapeSequence;
import com.jaiva.lang.Keywords;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.TParamsExtendable;
import com.jaiva.tokenizer.tokens.TSymbol;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.*;
import com.jaiva.utils.Find;
import com.jaiva.utils.Validate;
import com.jaiva.utils.Validate.IsValidSymbolName;
import com.jaiva.utils.generic.BlockChain;
import com.jaiva.utils.generic.MultipleLinesOutput;
import com.yetnt.utils.tuple.Pair;
import com.yetnt.utils.tuple.SamePair;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Tokenizer class is one of the 3 main classes which handle Jaiva code.
 * <p>
 * This class in particular handles the parsing of Jaiva code into tokens. It
 * only provides a single method which reads a single line and returns either a
 * single or multiple tokens, or sometimes a class which tells he outer instance
 * to do something before sending the next line.
 */
public final class Tokenizer {

    /**
     * Method to check whether a given block construct contains an opening and a
     * closing brace (before {@link Chars#BLOCK_OPEN}) or else throw an invalid
     * syntax exception.
     * Also checks whether it has a {@link Chars#BLOCK_OPEN}
     * 
     * @param construct  The construct in string form (in case of
     *                   {@link Tokenizer#handleArgs}
     *                   that is the type parameter)
     * @param line       The line
     * @param lineNumber The line's number.
     * @throws TokenizerException (Specifically MalformedSyntaxException)
     */
    private static void checkForMalformedConstruct(String construct, String line, int lineNumber)
            throws TokenizerException {
        if (!construct.equals(Keywords.FOR) && (!line.contains(Character.toString(Chars.STATEMENT_OPEN))
                || !line.contains(Character.toString(Chars.STATEMENT_CLOSE))))
            throw new MalformedSyntaxException(construct + " is missing opening or closing brace, go check it",
                    lineNumber);

        if (!line.contains(Chars.BLOCK_OPEN))
            throw new MalformedSyntaxException(construct + " is missing the open block thingy, yknow?", lineNumber);

        // make sure the closing ) is actually the outmost pair.
        int closingCharIndex = Find.closingCharIndex(line, Chars.STATEMENT_OPEN, Chars.STATEMENT_CLOSE);

        if (closingCharIndex == -1)
            throw new MalformedSyntaxException("i dont know what the hell to tell you because like this error"
            + "is suppsoed to show up like WAY earlier. idk something about braces man. Ugh why do you even "
                    + "bother coding in Jaiva?", lineNumber);

        String inbetween = construct.equals(Keywords.FOR) ? ""
                : line.substring(closingCharIndex + 1, line.indexOf(Chars.BLOCK_OPEN)).trim();
        if (!inbetween.isEmpty())
            throw new MalformedSyntaxException("Closeth thy brace.", lineNumber);
    }

    private static Object handleBlocks(boolean isComment, String line,
                                       MultipleLinesOutput multipleLinesOutput, String entireLine, String t, String[] args,
                                       Token<?> blockChain, int lineNumber) {
        MultipleLinesOutput m;
        if (multipleLinesOutput != null) {
            // multiple lines output exists. So we need to keep going until we find }
            m = isComment ? Find.closingCharIndexML(
                    line, Chars.COMMENT_OPEN, Chars.COMMENT_CLOSE, multipleLinesOutput.startCount,
                    multipleLinesOutput.endCount)
                    : Find.closingCharIndexML(
                            line, Chars.BLOCK_OPEN, Chars.BLOCK_CLOSE,
                            multipleLinesOutput.startCount,
                            multipleLinesOutput.endCount, multipleLinesOutput.preLine,
                            t, args, blockChain, lineNumber);
        } else {
            // Not given, but we need to find the closing tag.
            m = isComment ? Find.closingCharIndexML(
                    line, Chars.COMMENT_OPEN, Chars.COMMENT_CLOSE, 0, 0)
                    : Find.closingCharIndexML(line, Chars.BLOCK_OPEN, Chars.BLOCK_CLOSE, 0, 0, "",
                            t, args, blockChain, lineNumber);
        }

        if (m.endCount == m.startCount && m.startCount != 0 && m.startCount != -1) {
            return isComment ? null : m;
        } else {
            return m;
        }
    }

    /**
     * Handles the arguments for the given block type.
     * <p>
     * This method is used to handle the arguments for the given type. It is used in
     * the
     * {@link Tokenizer#readLine(String, String, Object, BlockChain, int, TConfig)}
     * method.
     * <p>
     * The arguments are handled based on the type of block it is. For example, if
     * it
     * is a function, it will handle the arguments as a function. If it is an if
     * statement, it will handle the arguments as an if statement.
     *
     * @param type The type of block it is.
     * @param line The line to handle the arguments for.
     * @return The arguments for the given type.
     */
    private static String[] handleArgs(String type, String line, int lineNumber) throws TokenizerException {
        try {
            switch (type) {
                case "mara if": {
                    checkForMalformedConstruct(type, line, lineNumber);
                    // mara condition ->
                    // mara (i < 10) ->
                    return new String[] {
                            line.substring(line.indexOf(Chars.STATEMENT_OPEN) + 1, line.lastIndexOf(Chars.STATEMENT_CLOSE)),
                            "" };
                }
                case "if": {
                    checkForMalformedConstruct(type, line, lineNumber);
                    // if (condition) ->
                    // if (variable != 100) ->
                    return new String[] {
                            line.substring(line.indexOf(Chars.STATEMENT_OPEN), line.lastIndexOf(Chars.STATEMENT_CLOSE)),
                            "" };
                }
                case "nikhil": {
                    checkForMalformedConstruct(type, line, lineNumber);
                    // nikhil (condition) ->
                    // nikhil (i < 10) ->
                    return new String[] {
                            line.substring(line.indexOf(Chars.STATEMENT_OPEN) + 1, line.lastIndexOf(Chars.STATEMENT_CLOSE)),
                            "" };
                }
                case "colonize": {
                    checkForMalformedConstruct(type, line, lineNumber);
                    // colonize declaration | condition | increment ->
                    // colonize variableName with array name ->

                    // colonize i <- 0 <| i <= 10 <| + ->
                    // colonize pointer with arr ->

                    if (line.contains(Chars.FOR_SEPARATOR)) {
                        String[] parts = line.split(Keywords.FOR)[1].trim().split(Chars.BLOCK_OPEN)[0].split("<\\|");
                        return new String[] { parts[0].trim(), parts[1].trim(), parts[2].trim() };
                    } else {
                        String[] parts = line.split(Keywords.FOR)[1].trim().split(Chars.BLOCK_OPEN)[0]
                                .split(Keywords.FOR_EACH);
                        return new String[] { parts[0].trim(), parts[1].trim(), Keywords.FOR_EACH };
                    }
                }
                case "kwenza": {
                    checkForMalformedConstruct(type, line, lineNumber);
                    // kwenza function_name(...args) ->
                    // kwenza addition(param1, param2) ->
                    String[] parts = line.split(Keywords.D_FUNCTION)[1].trim().split(Chars.BLOCK_OPEN);
                    String functionName = parts[0].substring(0, parts[0].indexOf(Chars.STATEMENT_OPEN));
                    String args = parts[0].substring(parts[0].indexOf(Chars.STATEMENT_OPEN) + 1,
                            parts[0].indexOf(Chars.STATEMENT_CLOSE));
                    return new String[] { functionName, args };
                }
                default: {
                    return new String[] { "" };
                }
            }
        } catch (Exception e) {
            throw new MalformedSyntaxException("SOMETHINg is fishy about this line big twan.....", lineNumber);
        }
    }

    /**
     * Handles the block lines for the given line when we get told its a block.
     * <p>
     * This method is used to handle the block lines for the given line. It is used
     * in
     * the
     * {@link Tokenizer#readLine(String, String, Object, BlockChain, int, TConfig)}
     * method.
     *
     * @param isComment           Whether the line is a comment or not.
     * @param line                The line to handle the block lines for.
     * @param multipleLinesOutput The multiple lines output object.
     * @param tokenizerLine       The tokenizer line.
     * @param tokens              The tokens to add to.
     * @param type                The type of block it is.
     * @param args                The arguments for the block.
     * @param blockChain          The block chain object.
     * @param lineNumber          The line number of the line.
     * @return The tokens for the given line.
     */
    @SuppressWarnings("unchecked")
    private static Object processBlockLines(boolean isComment, String line,
            MultipleLinesOutput multipleLinesOutput,
            String tokenizerLine, ArrayList<Token<?>> tokens, String type, String[] args,
            Token<?> blockChain, int lineNumber, TConfig config)
            throws Exception {
        type = multipleLinesOutput == null ? type : multipleLinesOutput.b_type;
        args = multipleLinesOutput == null ? args : multipleLinesOutput.b_args;
        int newLineNumber = multipleLinesOutput == null ? lineNumber : multipleLinesOutput.lineNumber;
        Object output = handleBlocks(isComment, line + "\n",
                multipleLinesOutput,
                tokenizerLine, type, args, multipleLinesOutput != null ? multipleLinesOutput.specialArg : blockChain,
                newLineNumber);
        if (output == null)
            return null;

        if (output instanceof MultipleLinesOutput) {
            int endCount = ((MultipleLinesOutput) output).endCount;
            int startCount = ((MultipleLinesOutput) output).startCount;
            if (endCount != startCount)
                return output;
        }
        assert output instanceof MultipleLinesOutput;
        MultipleLinesOutput finalMOutput = ((MultipleLinesOutput) output);
        Pair<String, Boolean> formattedPreLine = formatPreline(finalMOutput);
        String preLine = formattedPreLine.getFirst();
        ArrayList<Token<?>> nestedTokens = new ArrayList<>();
        config.flags.SKIP_READLINE_TRIM = true;
        Object stuff = readLine(preLine, (formattedPreLine.getSecond() == true ? null : ""), null, null, finalMOutput.lineNumber + 1, config);
//        line = Comments.decimate(line);
        try {
            if (stuff instanceof ArrayList) {
                nestedTokens.addAll((ArrayList<Token<?>>) stuff);
            } else if (stuff instanceof Token<?>) {
                nestedTokens.add((Token<?>) stuff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TCodeblock codeblock = new TCodeblock(nestedTokens, finalMOutput.lineNumber, lineNumber);
        // Okay cool, we've parsed everything, but what if its different types?
        Token<?> specific;
        assert multipleLinesOutput != null;
        switch (type) {
            case "mara if": {
                Object obj = new TExpression(finalMOutput.lineNumber).parse(args[0]);
                if (Validate.isValidBoolInput(obj)) {
                    obj = obj instanceof Token<?> ? ((Token<?>) obj).value() : obj;
                } else {
                    throw new TypeMismatchException(
                            "Ayo the condition in the mara if (" + args[0] + ") gotta resolve to a boolean dawg.",
                            finalMOutput.lineNumber);
                }
                TIfStatement ifStatement = new TIfStatement(obj, codeblock, finalMOutput.lineNumber);
                TIfStatement originalIf = ((TIfStatement) multipleLinesOutput.specialArg
                        .value());
                originalIf.appendElseIf(ifStatement);
                specific = originalIf.toToken();
                if (line.contains(Keywords.ELSE)) {
                    return new BlockChain(specific, line.replaceFirst(Chars.BLOCK_CLOSE, "").trim());
                }
                break;
            }
            case "mara": {
                TIfStatement originalIf = ((TIfStatement) multipleLinesOutput.specialArg
                        .value());
                originalIf.appendElse(codeblock);
                specific = originalIf.toToken();
                break;
            }
            case "if": {
                String cond = args[0].replaceFirst(Pattern.quote(Character.toString(Chars.STATEMENT_OPEN)),
                        Matcher.quoteReplacement(" ")).trim();
                Object obj = new TExpression(finalMOutput.lineNumber)
                        .parse(cond);
                if (Validate.isValidBoolInput(obj)) {
                    obj = obj instanceof Token<?> ? ((Token<?>) obj).value() : obj;
                } else {
                    throw new TypeMismatchException(
                            "Ayo the condition in the if (" + cond + ") gotta resolve to a boolean dawg.",
                            finalMOutput.lineNumber);
                }
                specific = new TIfStatement(obj, codeblock, finalMOutput.lineNumber).toToken();
                if (line.contains(Keywords.ELSE)) {
                    return new BlockChain(specific, line.replaceFirst(Chars.BLOCK_CLOSE, "").trim());
                }
                break;
            }
            case "zama zama": {
                specific = new TTryCatch(codeblock, finalMOutput.lineNumber).toToken();
                return new BlockChain(specific, line.replaceFirst(Chars.BLOCK_CLOSE, "").trim());
            }
            case "chaai": {
                TTryCatch tryCatch = ((TTryCatch) multipleLinesOutput.specialArg
                        .value());
                tryCatch.appendCatchBlock(codeblock);
                specific = tryCatch.toToken();
                break;
            }
            case "colonize": {
                String cond = args[0].replaceFirst(Pattern.quote(Character.toString(Chars.STATEMENT_OPEN)),
                        Matcher.quoteReplacement(" ")).trim();
                if (!args[2].equals(Keywords.FOR_EACH)) {
                    TokenDefault var = ((ArrayList<Token<?>>) Objects.requireNonNull(readLine(
                            Keywords.D_VAR + " "
                                    + cond
                                    + Chars.END_LINE,
                            "", null,
                            null, finalMOutput.lineNumber, config)))
                            .getFirst().value();

                    Object obj = new TExpression(finalMOutput.lineNumber).parse(args[1]);
                    if (Validate.isValidBoolInput(obj)) {
                        obj = obj instanceof Token<?> ? ((Token<?>) obj).value() : obj;
                    } else {
                        throw new TypeMismatchException(
                                "Ayo the condition in the colonize (" + cond + ") gotta resolve to a boolean dawg.",
                                finalMOutput.lineNumber);
                    }

                    Object finalOp;
                    String op = args[2].trim().substring(0, args[2].trim().length()-1);
                    if (op.equals("+") || op.equals("-")) {
                        finalOp = op;
                    } else {
                        finalOp = Token.processContext(op, lineNumber);
                    }
                    specific = new TForLoop(
                            var, obj,
                            finalOp,
                            codeblock, finalMOutput.lineNumber).toToken();
                } else {
                    TUnknownScalar<?, ?> variable = (TUnknownScalar<?, ?>) ((ArrayList<Token<?>>) Objects.requireNonNull(readLine(
                            Keywords.D_VAR + " "
                                    + args[0].replaceFirst("\\" + Chars.STATEMENT_OPEN, " ").trim()
                                    + Chars.END_LINE,
                            "",
                            null, null,
                            finalMOutput.lineNumber, config)))
                            .getFirst().value();
                    TVarRef arrayVar = (TVarRef) ((Token<?>) Objects.requireNonNull(Token.processContext(args[1]
                                    .replace(Chars.STATEMENT_CLOSE, ' ')
                                    .trim(),
                            finalMOutput.lineNumber)))
                            .value();
                    specific = new TForLoop(variable, arrayVar, codeblock,
                            finalMOutput.lineNumber).toToken();
                }
                break;
            }
            case "kwenza": {
                String[] fArgs = args[1].split(Character.toString(Chars.ARGS_SEPARATOR));
                for (int i = 0; i < fArgs.length; i++)
                    fArgs[i] = fArgs[i].trim();

                IsValidSymbolName IVSN = Validate.isValidSymbolName(args[0]);
                if (!IVSN.isValid)
                    throw new MalformedSyntaxException(
                            IVSN.op + " cannot be used in a variable name zawg.", finalMOutput.lineNumber);
                specific = new TFunction(args[0], fArgs, codeblock, finalMOutput.lineNumber).toToken();
                break;
            }
            case "nikhil": {
                Object obj = new TExpression(finalMOutput.lineNumber).parse(args[0]);
                if (Validate.isValidBoolInput(obj)) {
                    obj = obj instanceof Token<?> ? ((Token<?>) obj).value() : obj;
                } else {
                    throw new TypeMismatchException(
                            "Ayo the condition in the colonize " + args[0] + " gotta resolve to a boolean dawg.",
                            finalMOutput.lineNumber);
                }
                specific = new TWhileLoop(obj, codeblock, finalMOutput.lineNumber).toToken();
                break;
            }
            default: {
                throw new CatchAllException("Uhm, something went wrong. This shouldnt happen.",
                        finalMOutput.lineNumber);
            }
        }
        tokens.add(specific);
        return tokens;
    }

    private static Pair<String, Boolean> formatPreline(MultipleLinesOutput finalMOutput) {
        boolean isInline = false;
        String preLine = finalMOutput.preLine;
        preLine = preLine.startsWith(Keywords.D_FUNCTION) ? preLine.replaceFirst(Keywords.D_FUNCTION, "") : preLine;
        preLine = preLine.startsWith(Keywords.IF) ? preLine.replaceFirst(Keywords.IF, "") : preLine;
        preLine = preLine.startsWith(Keywords.WHILE) ? preLine.replaceFirst(Keywords.WHILE, "") : preLine;
        preLine = preLine.startsWith(Keywords.FOR) ? preLine.replaceFirst(Keywords.FOR, "") : preLine;
        preLine = preLine.startsWith(Keywords.ELSE) ? preLine.replaceFirst(Keywords.ELSE, "") : preLine;
        preLine = preLine.startsWith(Keywords.CATCH) ? preLine.replaceFirst(Keywords.CATCH, "") : preLine;
        preLine = preLine.startsWith(Keywords.TRY) ? preLine.replaceFirst(Keywords.TRY, "") : preLine;
        preLine = preLine.replace("$Nn", "").trim();
        preLine = preLine.substring(preLine.indexOf(Chars.BLOCK_OPEN) + 2);
        preLine = preLine.substring(0, preLine.lastIndexOf(Chars.BLOCK_CLOSE));
        String[] parts = preLine.split("\n");
        if (parts[0].trim().startsWith(String.valueOf(Chars.COMMENT))) isInline = true;
        return new Pair<>(preLine, isInline);
    }

    /**
     * Handles the variable declaration and assignment.
     * <p>
     * This method is used to handle the variable declaration and assignment. It is
     * used in the
     * {@link Tokenizer#readLine(String, String, Object, BlockChain, int, TConfig)}
     * method.
     *
     * @param line       The line to handle the variable declaration and assignment
     *                   for.
     * @param lineNumber The line number of the line.
     * @return The token for the given line.
     */
    private static Token<?> processVariable(String line, int lineNumber)
            throws JaivaException {
        boolean isString = false;
        if (line.contains(Chars.ARRAY_ASSIGNMENT)) {
            line = line.trim();
            line = line.substring(4);
            String[] parts = line.split("<-\\|");
            parts[0] = parts[0].trim();
            if (parts[0].isEmpty()) {
                throw new MalformedSyntaxException(
                        "Bro defined a variable on line with no name lmao.", lineNumber);
            }
            ArrayList<Object> parsedValues = new ArrayList<>();

            if (parts.length == 1) {
                // delcared with no value, empty array.
                return new TArrayVar(parts[0], parsedValues, lineNumber).toToken();
            }

            Token.splitByTopLevelComma(parts[1]).forEach(value -> {
                try {
                    parsedValues.add(Token.processContext(value.trim(), lineNumber));
                } catch (JaivaException e) {
                    throw new RuntimeException(e);
                }
            });
            return new TArrayVar(parts[0], parsedValues, lineNumber).toToken();

        }
        int stringStart = line.indexOf(Chars.STRING);
        int stringEnd = Find.closingCharIndex(line, Chars.STRING, Chars.STRING);
        ArrayList<SamePair<Integer>> quotepairs = Find.quotationPairs(line);
        if (stringStart != -1 && stringEnd != -1 && quotepairs.size() == 1
                && (line.charAt(line.length() - 1) == Chars.STRING
                        && line.split(Chars.ASSIGNMENT)[1].trim().charAt(0) == Chars.STRING)) {
            isString = true;
            String encasedString = line.substring(stringStart + 1, stringEnd);
            line = line.substring(0, stringStart - 1) + encasedString;
        }
        line = line.trim();
        line = line.substring(4);
        if (!line.contains(Chars.ASSIGNMENT)) {
            // they declared the variable with no value. Still valid syntax.
            // unless the name is nothing, then we have a problem.
            String name = line.trim();
            if (name.isEmpty()) {
                throw new MalformedSyntaxException(
                        "Bro defined a variable on line " + lineNumber + " with no name lmao.", lineNumber);
            }
            if (line.contains(Chars.ASSIGNMENT))
                throw new MalformedSyntaxException(
                        "if you're finna define a variable without a value, remove the assignment operator.",
                        lineNumber);
            return new TUnknownScalar(name, new TVoidValue(lineNumber), lineNumber)
                    .toToken();
        }
        String[] parts = new String[] {
                line.substring(0, line.indexOf(Chars.ASSIGNMENT)),
                line.substring(line.indexOf(Chars.ASSIGNMENT) + 2)
        };
        parts[0] = parts[0].trim();
        IsValidSymbolName IVSN = Validate.isValidSymbolName(parts[0]);
        if (!IVSN.isValid)
            throw new MalformedSyntaxException(IVSN.op + " cannot be used in a variable name zawg.", lineNumber);
        parts[1] = parts[1].trim();

        if (isString) {
            return new TStringVar(parts[0], parts[1], lineNumber).toToken();
        } else {
            try {
                return new TNumberVar(parts[0], Token.parseNumberLiteral(parts[1]), lineNumber).toToken();
            } catch (NumberFormatException e) {
                try {
                    return new TNumberVar(parts[0], Double.parseDouble(parts[1]), lineNumber).toToken();
                } catch (Exception e2) {
                    if (parts[1].equals("true") || parts[1].equals("false") || parts[1].equals(Keywords.TRUE)
                            || parts[1].equals(Keywords.FALSE)) {
                        parts[1] = parts[1].replace(Keywords.TRUE, "true").replace(
                                Keywords.FALSE,
                                "false");
                        return new TBooleanVar(parts[0], Boolean.parseBoolean(parts[1]),
                                lineNumber).toToken();
                    } else {
                        Object object = Token.dispatchContext(parts[1], lineNumber);
                        if (object instanceof Token<?>(TokenDefault<?> g)) {
                            if (g.name.equals("TStatement")) {
                                return ((TExpression) g).statementType == 0
                                        ? new TBooleanVar(parts[0], object,
                                                lineNumber).toToken()
                                        : new TNumberVar(parts[0], object, lineNumber).toToken();
                            }
                        }

                        return new TUnknownScalar(parts[0], object, lineNumber).toToken();
                    }
                }
            }
        }
    }

    /**
     * Handles the import statement.
     * <p>
     * This method is used to handle the import statement. It is used in the
     * {@link Tokenizer#readLine(String, String, Object, BlockChain, int, TConfig)}
     * method.
     *
     * @param line       The line to handle the import statement for.
     * @param lineNumber The line number of the line.
     * @param config     Tokenizer configuration
     * @return The token for the given line.
     */
    private static Token<?> handleImport(String line, int lineNumber, TConfig config)
            throws JaivaException {
        // tsea "path"
        // tsea "path" <- funcz, funca
        line = line.replace(Keywords.IMPORT.get(0), "").replace(Keywords.IMPORT.get(1), "").trim();

        // "path"
        // "path" <- funcz, funca
        String[] parts = line.split(Chars.ASSIGNMENT);

        ArrayList<SamePair<Integer>> quotepairs = Find.quotationPairs(line);
        if (quotepairs.isEmpty()) {
            throw new MalformedSyntaxException(
                    "Bro, the file to take from has to be surrounded by qutoes.", lineNumber);

        }
        int stringStart = line.indexOf(Chars.STRING);
        int stringEnd = Find.closingCharIndex(line, Chars.STRING, Chars.STRING);
        String path = line.substring(stringStart + 1, stringEnd);

        // turn other extensions into .jiv, for safety (Only when importing globals)
        if ((path.startsWith("jaiva/") || path.startsWith("jaiva\\"))
                && (path.endsWith(".jaiva") || path.endsWith(".jva")))
            path = path.substring(0, path.lastIndexOf(".") + 1) + "jiv";
        // add .jiv extension if not available already. (All globals end with .jiv, if
        // improting relatie file assume it ends in .jiv)
        if (!path.endsWith(".jiv") && !(path.endsWith(".jaiva") || path.endsWith(".jva")))
            path += ".jiv";

        Path pObj = Path.of(path);

        boolean isLib = false;

        // convert to static import if it contains "jaiva/" or "jaiva\"
        // set path, to path without the "jaiva/" or "jaiva\" prefix.
        if (path.startsWith("jaiva/") || path.startsWith("jaiva\\")) {
            isLib = true;
            path = path.replaceFirst("jaiva[/\\\\]", "");
            pObj = Path.of("lib/").resolve(path).normalize().toAbsolutePath();
            path = pObj.toString();
        }
        String fileName;

        if (isLib) {
            fileName = line.substring(stringStart + 1, stringEnd)
                    .replaceFirst("jaiva[/\\\\]", "")
                    .replace("\\", "/");
        } else {
            try {
                // try set the filename
                fileName = pObj.getFileName().toString().replaceAll(Pattern.quote(".jiv"), "");
            } catch (NullPointerException e) {
                throw new FileOrDirectoryNotFoundException("I don't think " + path + " exits...", lineNumber);
            }
        }

        if (parts.length > 1) {
            // ""path"", "funcz, funca"
            ArrayList<String> args = new ArrayList<>();
            for (String arg : parts[1].trim().split(Character.toString(Chars.ARGS_SEPARATOR)))
                args.add(arg.trim());
            return new TImport(path, fileName, isLib, args, lineNumber).toToken();
        } else {
            return new TImport(path, fileName, isLib, lineNumber).toToken();
        }
    }

    /**
     * The BIG BOY!
     * <p>
     * This method is the method. The method which reads a line and will return
     * either:
     * </p>
     * <ul>
     *     <li>
     *         A single {@link Token}
     *     </li>
     *     <li>
     *         An {@link ArrayList} of Tokens. if the line (or lines) contain multiple tokens
     *     </li>
     *     <li>
     *         A {@link MultipleLinesOutput} if we read an opening -> and need to find the closing
     *      <~ before parsing anything
     *     </li>
     *     <li>
     *         A {@link BlockChain} if we read a block of code that needs to be chained to its
     *      original token (such as if else chains)
     *     </li>
     *     <li>
     *         {@code void.class} if this is so obscure that no other exception even caught it.
     *     </li>
     * </ul>
     *
     * @implNote
     *     This method has a branch within it, which is almost perfectly identical
     *     tp {@link Main#parseTokens(String, boolean)} (purposefully so). Although it's intended use is for nested block
     *     structures, if this method is given a single string split by multiple new lines, it acts the exact same as
     *     calling {@link Main#parseTokens(String, boolean)}, i dont fucking know how i just know it works.
     *     Although, that means implementing all the plumbing required to handle all 4 different return types
     *     and simultaneously keeping track of documentation comments to consume them to the next token
     *     with the specific semantics. {@link Main#parseTokens(String, boolean)} allows you to avoid all that
     *     with the caveat that it takes a file input and not a line/lines input
     * @implSpec
     * if you're planning on hand rolling a handler for this. bless your soul. It's very stateful
     * and you'd need to handle the following cases:
     *
     * <p>
     *     <ol>
     *         <li>
     *             If it returns a {@link MultipleLinesOutput}, store it, and on the next pass, pass the stored instance
     *               back into the call.
     *         </li>
     * <li>
     *     If it returns a {@link BlockChain}, the caller must retain the chain as
     *     the current source of subsequent lines rather than immediately consuming
     *     another line from its normal input source. The next line must be obtained
     *     from {@link BlockChain#currentLine()} and passed back through this method
     *     with the chain supplied as the {@code blockChain} argument. Once the chain
     *     returns control by producing a non-{@link BlockChain} result, normal source
     *     consumption may resume.
     * </li>
     *         <li>
     *             If it returns multiple a {@link Token} and that token is a {@link TDocsComment} and the previously emitted token
     *             happens to be one that implements {@link TSymbol} interface
     *             <p>
     *                 You call:
     *                 <pre>{@code
     *                 JDoc doc = new JDoc(lineNum, comment.trim());
     *                 t.tooltip = doc;
     *                 t.json.removeKey("toolTip");
     *                 t.json.append("toolTip", doc, true);
     *                 }</pre>
     *             </p>
     *             Effectively consuming the {@link TDocsComment}
     *         </li>
     *         <li>
     *             if it returns an {@link ArrayList} of {@link Token} but that list happens to contain a single element, being a
     *             {@link TDocsComment} and the previous emitted token happens to be one that implements the {@link TSymbol}
     *             interface? You know the drill.
     *         </li>
     *         <li>
     *             Otherwise, a normal {@link Token} can be added to your tokens list, and a normal
     *             {@link ArrayList} of tokens, can be added via {@link ArrayList#addAll(Collection)}
     *         </li>
     *     </ol>
     * </p>
     *
     * 
     * @param line                The line to read.
     * @param previousLine        The previous line to read.
     * @param multipleLinesOutput The multiple lines output object.
     * @param blockChain          The block chain object.
     * @param lineNumber          The line number of the line.
     * @param config              The config object.
     * @return The token for the given line.
     * @throws Exception If the line is invalid or if
     *                   there is an error parsing the token
     *
     * @author Lehlogonolo Poole
     */
    @SuppressWarnings("unchecked")
    public static Object readLine(String line, String previousLine, Object multipleLinesOutput, BlockChain blockChain,
            int lineNumber, TConfig config)
            throws Exception {
        line = EscapeSequence.escapeAll(line);
        if (config.flags.SKIP_READLINE_TRIM) {
            config.flags.SKIP_READLINE_TRIM = false;
        } else {
            line = line.trim();
        }

        boolean cont = multipleLinesOutput instanceof MultipleLinesOutput;
        boolean isComment = (cont && ((MultipleLinesOutput) multipleLinesOutput).isComment)
                || (multipleLinesOutput == null && (line.startsWith(Character.toString(Chars.COMMENT_OPEN))
                        || (line.indexOf(Chars.COMMENT_OPEN) != -1
                                && line.charAt(line.indexOf(Chars.COMMENT_OPEN)) != Chars.ESCAPE)));
        boolean isCodeBlock = (cont && !((MultipleLinesOutput) multipleLinesOutput).isComment)
                || (line.startsWith(Keywords.IF) || line.startsWith(Keywords.D_FUNCTION)
                        || line.startsWith(Keywords.FOR))
                || line.startsWith(Keywords.WHILE) || line.startsWith(Keywords.ELSE) || line.startsWith(Keywords.TRY)
                || line.startsWith(Keywords.CATCH);

        // To the developer who would like to understand this.
        // Don't. Just don't. It's a mess. I am NOT sorry.
        String type;
        if (multipleLinesOutput != null) {
            assert multipleLinesOutput instanceof MultipleLinesOutput;
            type = ((MultipleLinesOutput) multipleLinesOutput).b_type;
        } else {
            type = line.startsWith(Keywords.IF) ? Keywords.IF
                    : line.startsWith(Keywords.D_FUNCTION) ? Keywords.D_FUNCTION
                            : line.startsWith(Keywords.FOR) ? Keywords.FOR
                                    : line.startsWith(Keywords.WHILE) ? Keywords.WHILE
                                            : line.replace(Chars.BLOCK_CLOSE, "").trim()
                                                    .startsWith(Keywords.ELSE + " " + Keywords.IF)
                                                            ? Keywords.ELSE + " " + Keywords.IF // check for "ELSE
                                                                                                // IF"
                                                                                                // before
                                                                                                // checking for "IF"
                                                            : line.replace(Chars.BLOCK_CLOSE, "").trim()
                                                                    .startsWith(Keywords.ELSE) ? Keywords.ELSE
                                                                            : line.startsWith(Keywords.TRY)
                                                                                    ? Keywords.TRY
                                                                                    : line.replace(
                                                                                            Chars.BLOCK_CLOSE,
                                                                                            "")
                                                                                            .trim()
                                                                                            .startsWith(
                                                                                                    Keywords.CATCH)
                                                                                                            ? Keywords.CATCH
                                                                                                            : null;
        }

        if ((line.contains(Chars.BLOCK_OPEN) && line.indexOf(Chars.BLOCK_OPEN) < line.indexOf("\n")) && type == null
                && !line.startsWith(Character.toString(Chars.COMMENT)))
            // A block of code but the type waws not catched, invaliud keyword then.
            // This is a syntax error.
            throw new MalformedSyntaxException(
                    line.split(" ")[0] + " aint a real keyword homie.", lineNumber);

        ArrayList<Token<?>> tokens = new ArrayList<>();
        String[] ls = line.split("\n");
        String[] lines = Comments.decimate(ls);

        if (lines.length > 1 || (
                lines.length == 1 &&
                        (line.startsWith(Chars.COMMENT_DOC) && !line.startsWith(String.valueOf(Chars.COMMENT))) &&
                        !line.equals(lines[0] + (line.endsWith(String.valueOf(Chars.END_LINE)) ? Chars.END_LINE : "")))
        ) {
//            // multiple lines.
            MultipleLinesOutput m = null;
            BlockChain b = null;
            String comment = null;
            int ln;
            for (int i = 0; i != ls.length; i++) {
                ln = config.flags.SHORTHAND_IF ?
                        lineNumber - 1 /* short if is on the same line */ :
                        lineNumber + i /* The following code, is a fix to a problem i don't want to solve yet, so just minus 1*/ - 1;
                String l = "";
                if (b != null) {
                    l = b.currentLine();
                    i--;
                    ln--;
                } else {
                    l = ls[i];
                }
                String previousLine2 = i == 0 ? previousLine : ls[i - 1];
                Object something = readLine(l, previousLine2, m, b, ln, config);
                switch (something) {
                    case MultipleLinesOutput linesOutput -> {
                        m = linesOutput;
                        b = null;
                    }
                    case ArrayList<?> objects -> {
                        m = null;
                        b = null;
                        if (((ArrayList<Token<?>>) something).size() == 1) {
                            for (Token<?> t : (ArrayList<Token<?>>) something) {
                                TokenDefault g = t.value();
                                if (comment == null || (!(g instanceof TArrayVar) && !(g instanceof TUnknownScalar)
                                        && !(g instanceof TFunction)))
                                    continue;
                                JDoc doc = new JDoc(lineNumber, comment.trim());
                                g.tooltip = doc;
                                g.json.removeKey("toolTip");
                                g.json.set("toolTip", doc);
                            }

                        }
                        comment = null;
                        tokens.addAll((ArrayList<Token<?>>) something);
                    }
                    case BlockChain chain -> {
                        m = null;
                        comment = null;
                        b = chain;
                    }
                    case Token<?> token when token.value() instanceof TDocsComment -> {
                        b = null;
                        m = null;
                        comment = (comment == null ? "" : comment)
                                + ((TDocsComment) token.value()).comment;
                    }
                    case Token<?> token when token.value() instanceof TExtendParams extendParams -> {
                        b = null;
                        m = null;
                        comment = null;
                        consumeExtension(extendParams, tokens.isEmpty() ? null : tokens.getLast().value(), lineNumber);
                    }
                    case Token<?> token -> {
                        TokenDefault<?> t = token.value();
                        t.tooltip = comment != null ? comment : t.tooltip;

                        if (comment != null
                                && ((t instanceof TArrayVar) || (t instanceof TUnknownScalar)
                                || (t instanceof TFunction))) {

                            JDoc doc = new JDoc(t.lineNumber, comment.trim());
                            t.tooltip = doc;
                            t.json.removeKey("toolTip");
                            t.json.set("toolTip", doc);
                        }
                        tokens.add(token);
                        m = null;
                        b = null;
                        comment = null;
                    }
                    case null, default -> {
                        m = null;
                        b = null;
                        comment = null;
                    }
                }
            }
            config.flags.SHORTHAND_IF = false;
            return tokens;
        }
        String tokenizerLine = (previousLine == null ? "" : previousLine.trim()) + line; // The entire line to the tokenizer.

        // System.out.println("sdfsdfd");
        // @comment (single line)

        /*
         * {
         * multi line comment}
         * and block stuff
         */
        if (cont || isComment || isCodeBlock) {
            return type == null
                    ? processBlockLines(isComment, line, (MultipleLinesOutput) multipleLinesOutput,
                            tokenizerLine,
                            tokens, null,
                            new String[] { "" }, null, lineNumber, config)
                    : processBlockLines(
                            isComment, line, (MultipleLinesOutput) multipleLinesOutput, tokenizerLine,
                            tokens, type,
                            multipleLinesOutput == null ? handleArgs(type, line, lineNumber) : null,
                            (blockChain != null ? blockChain.initialIf() : null),
                            lineNumber, config);
        }

        // if its anything after this the line has to end in a ! or else invalid syntax.
        // (All other methods which did the ! will be redundant as we can just check
        // here.)
        Object tryDecimate = Comments.safeDecimate(line);

        if (tryDecimate instanceof Token<?>)
            return tryDecimate;
        else
            line = ((String) tryDecimate).trim();

        if (line.isEmpty())
            return null;

        if (!Find.bracePairs(line).unclosedBraces().isEmpty())
            throw new MalformedSyntaxException(
                    "Ayo, you got some unclosed braces in your code. Fix that bro wtf.", lineNumber);

        if (!line.equals(Chars.BLOCK_CLOSE) && !line.endsWith(Chars.BLOCK_OPEN) && !line.endsWith(Character.toString(Chars.END_LINE))) {
            throw new MalformedSyntaxException(
                    "Ye wena. You don't shout your code. Sies.", lineNumber);
        }

        line = line.substring(0, line.length() - 1);

        if (line.startsWith(Keywords.SHORTHAND_IF)) {
            // Fake an if statement
            config.flags.SHORTHAND_IF = true;
            if (!line.contains("("))
                throw new MalformedSyntaxException("No braces for shorthand if? wtf are you doing", lineNumber);

            if (!line.contains(Chars.SHORTHAND_IF_SEPARATOR))
                throw new MalformedSyntaxException("using sif, requires you have ?> after the condition bro", lineNumber);

            String[] partz = line.split(Pattern.quote(Chars.SHORTHAND_IF_SEPARATOR));
            SamePair<Integer> outerMost = Find.lastOutermostBracePair(partz[0]);
            if (outerMost == null)
                throw new MalformedSyntaxException("Wrong brace placement???", lineNumber);
            int lastEndIndex = outerMost.getSecond();
            int indexOfOp = line.indexOf(Chars.SHORTHAND_IF_SEPARATOR);

            // split sting at that index (Without the brace.)
            String first = partz[0].substring(0, lastEndIndex).trim();
            String last = line.substring(indexOfOp+2).trim();
            // remove keyword from first
            String[] b_args = new String[]{first.substring(3), ""};
            String b_type = "if";
            String l = "<~";
            String preLine = "if " + b_args[0] + ") ->\n" + last +"!";
            String tokL = last +"!<~";
            boolean isC = false;

            return processBlockLines(
                    isC, l,
                    new MultipleLinesOutput(
                            1, 0, preLine, b_type, b_args, null, lineNumber
                    ),
                    tokL, tokens, null, new String[]{""}, null, lineNumber, config
            );
        }

        if (line.startsWith(Chars.PARAM_EXTENDOR)) {
            line = line.trim();
            return Token.processContext(line, lineNumber);
        }


        if (line.startsWith(Keywords.IMPORT.get(0)) || line.startsWith(Keywords.IMPORT.get(1)))
            return handleImport(line, lineNumber, config);

        // #STRING# is (khutla 100!) syntax which is a function return.

        if (line.startsWith(Keywords.THROW)) {
            // Remove keyword 'cima' and operator '<=='
            line = line.trim();
            Object errorMessage = Token.processContext(getString(line, lineNumber), lineNumber);

            tokens.add(new TThrowError(errorMessage, lineNumber).toToken());
            return tokens;
        }

        line = line.trim();

        if (line.startsWith(Keywords.LC_BREAK) || line.startsWith(Keywords.LC_CONTINUE))
            return switch (line) {
                case "voetsek", "nevermind" -> new TLoopControl(line, lineNumber).toToken();
                default -> throw new MalformedSyntaxException(
                        "Loop control keywords should be by themselves big bro. Remove whatever is on line ",
                        lineNumber);
            };

        if (line.startsWith(Keywords.RETURN)) {
            tokens.add(
                    new TFuncReturn(Token.processContext(line.replace(Keywords.RETURN, ""),
                            lineNumber),
                            lineNumber)
                            .toToken());
            return tokens;
        }

        // #STRING# is a line which contains something that needs to be tokenized.

        if (line.startsWith(Keywords.D_VAR)) {
            tokens.add(processVariable(line, lineNumber));
            return tokens;
        }

        // if it doesnt start with declaration nfor var, it might be a reassingment
        String[] parts = line.split(Chars.ASSIGNMENT);
        if (parts.length > 1 && !parts[0].endsWith("(")) {
            String varName = parts[0].trim();
            Object varValue = Token.processContext(parts[1].trim(), lineNumber);
            if (varName.contains("]") || varName.contains("[")) {
                // This is an array reassignment.
                tokens.add(new TVarReassign(Token.processContext(varName,
                        lineNumber), varValue, lineNumber).toToken());
                return tokens;
            } else
                tokens.add(
                        new TVarReassign(parts[0].trim(), Token.processContext(parts[1].trim(),
                                lineNumber),
                                lineNumber)
                                .toToken());

            return tokens;
        }

        if (line.equals(Keywords.UNDEFINED)) {
            tokens.add(new TVoidValue(lineNumber).toToken());
            return tokens;
        }

        // if we've reached here, then it doesnt have a keyword prolly. meaning
        // TFuncCall
        // (You cant just place a variable out of nowhere with no context around it.)
        // (Therefore i wont parse it.)
        Object token = Token.processContext(line, lineNumber);
        if (token instanceof Token<?>) {
            tokens.add((Token<?>) token);
            return tokens;
        }

        // Last resort: Don't fucking know what this line is supposed to tokenize as
        // Therefore:
        return void.class;
    }

    private static String getString(String line, int lineNumber) throws MalformedSyntaxException {
        String withoutKeyword = line.substring(Keywords.THROW.length()).trim();
        // Split on the operator "<=="
        String[] parts = withoutKeyword.split(Chars.THROW_ERROR);
        if (parts.length != 2)
            throw new MalformedSyntaxException(
                    "Ehh baba you must use the right syntax if u wanna cima this process.", lineNumber);

        return parts[1].trim();
    }

    public static void consumeExtension(TExtendParams tExtendParams, TokenDefault<?> previousToken, int lineNumber)
            throws TokenizerException {
        if (lineNumber == 1 || previousToken == null)
            throw new MalformedSyntaxException("How do you extend the start of a scope bro?", lineNumber);
        if (previousToken instanceof TParamsExtendable tpe) {
            if (tpe.endsWithFuncCall())
                tpe.addArguments(tExtendParams.args);
            else throw new MalformedSyntaxException(
                    "Cannot extend the value on line " + (lineNumber - 1) + " as it is not a function call.",
                    lineNumber
            );
        } else {
            throw new MalformedSyntaxException(
                    "Cannot extend the construct on line " + (lineNumber - 1) + " as it is not extendable",
                    lineNumber
            );
        }
        // TExtendParams effectively goes to hell.
    }
}

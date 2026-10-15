package com.jaiva;

import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.JaivaException;
import com.jaiva.errors.JaivaException.UnknownFileException;
import com.jaiva.errors.LoadException;
import com.jaiva.errors.TokenizerException;
import com.jaiva.interpreter.Interpreter;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.global.Globals;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.md.MDInputProps;
import com.jaiva.md.ToMarkdown;
import com.jaiva.tokenizer.NewJson;
import com.jaiva.tokenizer.TConfig;
import com.jaiva.tokenizer.Tokenizer;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.TSymbol;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.TDocsComment;
import com.jaiva.tokenizer.tokens.specific.TExtendParams;
import com.jaiva.utils.generic.BlockChain;
import com.jaiva.utils.generic.MultipleLinesOutput;
import com.yetnt.utils.builders.AnsiColour;
import com.yetnt.utils.builders.SimpleJSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;

import static com.jaiva.tokenizer.Tokenizer.consumeExtension;

/**
 * The Main class serves as the entry point for the Jaiva programming language
 * tokenizer and
 * interpreter. It handles command-line arguments, initializes the REPL
 * (Read-Eval-Print Loop),
 * and manages the parsing and interpretation of Jaiva source files.
 * <p>
 * The class contains methods for parsing tokens from source files, displaying
 * help information, and executing the interpreter.
 */
public class Main {
    /**
     * List of command-line arguments that are used to start the REPL mode.
     * These arguments include options for printing tokens, displaying help,
     * showing version information, running tests, and updating instructions.
     */
    public static ArrayList<String> replArgs = new ArrayList<>(
            Arrays.asList("--print-tokens", "-p", "--help", "-h", "--version", "-v", "--test", "-t", "--update",
                    "-u", "--json-stream", "-js" ));
    /**
     * List of command-line arguments that are used when running a Jaiva source
     * file.
     * These arguments include options for printing tokens in string format,
     * JSON format, JSON format with globals, and enabling debug mode.
     */
    public static ArrayList<String> tokenArgs = new ArrayList<>(
            Arrays.asList("-s", "-j", "--string", "--json", "-jg", "--json-with-globals", "-d", "--debug", "-is",
                    "--include-stacks", "--markdown", "-md", "-jog", "--json-of-globals"));
    /**
     * Version of the Jaiva programming language interpreter. This is a string
     * variable that holds the version number in the format
     * "major.minor.patch-(beta|rc|alpha)
     * .<build number>"
     * (SemVar).
     */
    public static String version = "6.2.0";
    /**
     * Author, it's just me.
     */
    public static String author = "Lehlogonolo Poole (@yetnt on some socials) https://github.com/yetnt";
    /**
     * ASCII art representation of the Jaiva logo. This is a multi-line string
     */
    public static String ASCII = """
                                       .+X$$&&&&&&&&$+.
                                       xX..         .;X$.
                                       xx              +&:
                                       xx              .&+
                                       xx               &+
                                       xx               &+
                                       xx               &+
                                       xx               &+
                                       xx               &+
                                       xx               &+
                                       xx               &+
                                       xx               &+
                                       xx               &+    .:;xX$$Xx;:.
                                       xx               &+ .:&&+.     ..x&$.
                                       xx               &+.$x.           .:$X.
                                       xx               &X&;               .Xx
                                       xx               &&X                 .&.
                                       xx               &&x                 .$:
                                       xx               &&&.                +$.
                                       xx               &+x$.             .+&:    .x&$$&x.
                                       xx               &+ :$X;.           :&;    :&:  :&:
                                       xx               &+   .+X&&$XX$&&&.  ;&.  .$x  .X$.
                                       xx               &+             .$x  .X$..+$.  ;&.
                                       xx               &+              .$+  .&+.&:  :&;
                                       xx               &+               ;&;  :&$+  .$x.
                                       xx               &+                ;$. .+$. .Xx
                                       xx               &+ .;xX$&&&&&&&X;..+&:    .X$.
                                       xx               &+ &.           .X&:+&:  .XX
                     +$&Xx+xX&$+.      xx               &+ &.             .$;.:++;:.
                    ;$..;++;. .x$.     xx               &+ &.             .x$
                     ;&&&$$&&; .&;     xx               &+ &.              x$.
                   .;&x. ..... .&;     xx               &+ &.              x$.
                   :$; .$&&$&+ .&;     Xx               &+ &.              x$.
                   .$+..;X$$X: .&;     $;              .&+ &.              x$.
                    .X$x;:::;+X&x.    +$.              :&; &.              x$.
                       .;+++;:.     .+&:               ;&: &.              x$.
              .;&x:...           ..+&X.                Xx  &.              x$.
             :$X..:+X&&$$$XXX$$$&&x:.                 +$.  &.              x$.
            .&;          .....                       +&:   &.              x$.  .+$$XxxxX&$;
            +$.                                    .x$.    &.              x$.  ;$..:;;;. .X$.
            x$.                                  .:$X.     &.              x$.  .+&&&$$&&: .&;
            .&+                                .;&X..      &.              x$.  ;&X......  .&;
             .X$;                           .+$$;.         &.              x$. :&; .$&$$&; .&;
               .+$$x+:.              ..;+X$$x;.            &.              x$. :&; .+$$$X: .&;
                   .:+xX$$$&&&&&&$$$XXx;.                  &.              x$.  :$$+:::::;x&x.
                                                           &.              x$.     .;+++;.
                       \s""";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            new REPL(0);
            System.exit(0);
            return;
        } else if (replArgs.contains(args[0])) {
            switch (args[0]) {
                case "--json-stream", "-js" -> {
                    new Streamer(args);
                    System.exit(0);
                    return;
                }
                case "--print-tokens", "-p" -> {// System.out.println("Print token REPL mode.");
                    new REPL(1);
                    System.exit(0);
                }
                case "--help", "-h" -> {
                    System.out.println();
                    System.out.println("Usage: jaiva [options]");
                    System.out.println();
                    System.out.println("\tOptions:");
                    System.out.println();
                    System.out.println("\t(no flag): Starts a REPL.");
                    System.out.println("\t--print-tokens, -p: Print tokens REPL mode.");
                    System.out.println("\t--help, -h: Show this help message.");
                    System.out.println("\t--version, -v: Show the version of Jaiva.");
                    System.out.println("\t--test -t: Test command.");
                    System.out.println("\t--update, -u: Update Instructions.");
                    System.out.println();
                    System.out.println("Usage: jaiva <file.jiv> [options]");
                    System.out.println();
                    System.out.println("\tOptions:");
                    System.out.println();
                    System.out.println("\t(no flag): Run the file.");
                    System.out.println("\t--json, -j: Print tokens in JSON format.");
                    System.out.println("\t--json-with-globals, -jg: Print tokens including globals in JSON format.");
                    System.out.println("\t--string, -s: Print tokens in string format.");
                    System.out.println(
                            "\t--incude-stacks, -is: All errors will include stack traces in the output.");
                    System.out.println("\t--debug, -d: Enable debug mode.");
                    System.out.println();
                    System.exit(0);
                }
                case "--version", "-v" -> {
//                    System.out.println(ASCII);
                    System.out.println(AnsiColour.print("Jaiva! " + version, AnsiColour.FONT.BOLD, AnsiColour.FONT.ITALIC));
//                    System.out.println(
//                            "Jaiva is a programming language that is designed to be easy to use and understand. (I'm speaking out my ass, I made this cuz i was bored on a random january)");
                    System.out.println("Made with love by: " + author);
                    System.out.println();
                    System.exit(0);
                }
                case "--test", "-t" -> {
                    System.out.println(AnsiColour.print(ASCII, AnsiColour.FONT.BOLD));
                    System.out.println();

                    System.exit(0);
                }
                case "--update", "-u" -> {
                    System.out.println();
                    System.out.println(
                            "There is an update script in your "+ AnsiColour.print("/<jaiva location>/", AnsiColour.FONT.BOLD) + " folder.");
                    System.out.println("If you're on windows you can run " + AnsiColour.print("jaiva-install", AnsiColour.FONT.BOLD) + " to update if any is available. Unix like systems may need to configure the alias to the script");
                    System.out.println();
                    System.exit(0);
                }
                default -> {
                    System.out.println("Invalid REPL mode.");
                    System.out.println();
                    System.exit(-1);
                }
            }
            return;
        } else if (!args[0].contains(".") && !args[0].contains("jaiva/") && !args[0].contains("jaiva\\")) {
            // this is to catch the case where the user does not provide a file name
            System.out.println("Provide a file name or a cmd flag kau.");
            System.out.println();
            System.exit(-1);
            return;
        }

        IConfig<Object> iconfig = new IConfig<>(args, args[0]);
        boolean stackTraces = false;
        boolean debug = false;
        try {
            if (args.length > 1) {
                if (args[1].equals("-is") || args[1].equals("--incude-stacks"))
                    stackTraces = true;
                if (args[1].equals("-d") || args[1].equals("--debug"))
                    debug = true;
            }
            ArrayList<Token<?>> tokens = new ArrayList<>();
            List<String> globalsJsonFlag = List.of("-jog", "--json-of-globals");
            if ((!args[0].startsWith("jaiva/") && !args[0].startsWith("jaiva\\") &&
                    (args.length > 1 && !globalsJsonFlag.contains(args[1])))
                    || args.length == 1
            )
                tokens = parseTokens(args[0], false);
            if (tokens.isEmpty() && (args.length > 1 && !args[1].equals("-md") && !args[1].equals("--markdown")) &&  (!globalsJsonFlag.contains(args[1]))) {
                System.out.println();
                System.exit(0);
                return;
            }

            if ((args.length > 1) && tokenArgs.contains(args[1])) {
                // here, args[1] is the tokens mode
                // return tokens mode.
                switch (args[1]) {
                    case "-is", "--incude-stacks" -> {
                        stackTraces = true;
                        break;
                    }
                    case "-d", "--debug" -> {
                        if (args[0].startsWith("jaiva/") || args[0].startsWith("jaiva\\"))
                            throw new JaivaException.UnknownFileException("You can't debug the built-in jaiva libs.");
                        new Debugger(iconfig, tokens, args[1].equals("-d"));
                        return;
                    }
                    case "-s", "--string" -> {
                        if (args[0].contains("jaiva/") || args[0].contains("jaiva\\"))
                            throw new JaivaException.UnknownFileException("You can't print tokens of the built-in jaiva libs.");
                        for (Token<?> t : tokens) {
                            System.out.println(t.toString());
                        }
                        System.out.println();
                        System.exit(0);
                        return;
                    }
                    case "-j", "--json" -> {
                        if (args[0].startsWith("jaiva/") || args[0].startsWith("jaiva\\"))
                            throw new JaivaException.UnknownFileException("You can't print tokens of the built-in jaiva libs.");
                        System.out.println(new NewJson().collectionToString(tokens));
                        System.out.println();
                        System.exit(0);
                        return;
                    }
                    case "-jog", "--json-of-globals" -> {
                        String lib = args[0];
                        System.out.print(new Globals(iconfig).returnGlobalsOf(lib, iconfig));
                        System.out.println();
                        System.exit(0);
                        return;
                    }
                    case "-md", "--markdown" -> {
                        if (args.length < 3)
                            throw new JaivaException.TooLittleArgsException("Markdown output needs an output directory!");
                        BaseLibrary baseLibrary = null;
                        Vfs vfs = null;
                        MDInputProps.Type type = MDInputProps.Type.FILE;
                        Globals globals = new Globals(iconfig);
                        boolean isLib = false;
                        if (args[0].startsWith("jaiva/") || args[0].startsWith("jaiva\\")) {
                            // The user is trying to output markdown for the built-in jaiva libs. Why not give it to them.
                            String lib = args[0]
                                    .replace("jaiva/", "")
                                    .replace("jaiva\\", "");
                            if (globals.getBuiltInGlobal(lib) == null && !lib.contains("globals"))
                                throw new JaivaException.UnknownFileException("jaiva/" + lib);
                            try {
                                baseLibrary = args[0].endsWith("globals")  // edge case for globals lib.
                                        ? globals
                                        : globals.builtInGlobals.get(lib)
                                        .loadClassLibrary(iconfig, globals);
                                vfs = baseLibrary.getUniqueVfs();
                                type = MDInputProps.Type.CLASS;
                            } catch (LoadException.LibraryLikeGetException e) {
                                vfs = globals.builtInGlobals.get(lib).load(iconfig, globals);
                            }
                            isLib = true;
                            if (vfs == null)
                                throw new JaivaException.UnknownFileException("jaiva/" + lib);
                        }
                        String out = args[2];
                        Path outDir = Path.of(out);
                        new ToMarkdown(
                                new MDInputProps(
                                        isLib, type, vfs == null ? Vfs.fromFile(tokens) : vfs, baseLibrary
                                ),
                                outDir,
                                iconfig, globals,
                                args.length > 3 ? args[3] : null
                        );
                        System.out.println();
                        System.exit(0);
                        return;
                    }
                    default -> {
                        System.out.println("Invalid token mode.");
                        System.out.println();
                        System.exit(-1);
                        return;
                    }
                }
            }

            Interpreter.interpret(tokens, new Scope(iconfig), iconfig);

            // if we reached here, everythign went well!

//            System.out.println();
            iconfig.setOutLivedMain(true);

        } catch (Exception e) {
            iconfig.releaseAll();
            if (debug || stackTraces)
                throw e; // throw the error as uncaught when debug mode is enabled so that we get the
                         // entire error along with the stack trace.
            switch (e) {
                case InterpreterException interpreterException -> {
                    System.out.println("Error while interpreting code: ");
                    System.out.println(e.getMessage());
                    System.out.println();
                    System.exit(1);
                }
                case TokenizerException tokenizerException -> {
                    System.out.println("Error while parsing code: ");
                    System.out.println(e.getMessage());
//                    e.printStackTrace(System.out);
                    System.out.println();
                    System.exit(-1);
                }
                case LoadException tokenizerException -> {
                    System.out.println("Error loading something: ");
                    System.out.println(e.getMessage());
//                    e.printStackTrace(System.out);
                    System.out.println();
                    System.exit(-1);
                }
                case JaivaException jaivaException -> {
                    // TokenizerException and InterpreterException extend off of JaivaException, so
                    // if we dont catch those, cagtch this one with generic message.
                    System.out.println("Error: ");
                    System.out.println(e.getMessage());
//                    e.printStackTrace(System.out);
                    System.out.println();
                    System.exit(-1);
                }
                default -> {
                    // Some other thing, this genrally shouldnt happen as all eror cases we need to
                    // account for where ever it is and not here in the Main class.
                    System.out.println("\"Something\" went wrong, and it's proabaly not your fault.");
                    System.out.println(e.getMessage());
//                    e.printStackTrace(System.out);
                    System.out.println();
                    System.exit(-1);
                }
            }
        }
    }

    /**
     * Parses the tokens from a given file and returns them as an ArrayList of Token
     * objects.
     * 
     * @param filePath  The path to the file to be parsed. The file must have an
     *                  extension
     *                  of either `.jaiva`, `.jiv`, or `.jva`. If the file does not
     *                  have one
     *                  of these extensions, an empty list is returned.
     * @param returnVfs A boolean flag (currently unused in the method).
     * @return An ArrayList of Token objects parsed from the file. If the file is
     *         not found or an error occurs during parsing, an empty list is
     *         returned.
     * <p>
     *         The method processes the file line by line, identifying and handling
     *         various token types
     *         such as comments, variables, functions, and block chains. It also
     *         associates tooltips
     *         with certain token types when applicable. If the file cannot be
     *         found, an error message
     *         is printed to the console.
     * <p>
     *         Exceptions:
     *         - Prints an error message if the file is not found.
     *         - Prints the exception message if any other error occurs during
     *         parsing.
     */
    public static ArrayList<Token<?>> parseTokens(String filePath, boolean returnVfs) throws Exception {

        // here, args[0] is the file name that must end in either .jaiva, .jiv or .jva
        if (!filePath.endsWith(".jaiva") && !filePath.endsWith(".jiv") && !filePath.endsWith(".jva")) {
            throw new JaivaException("I can't read whatever that lang is bro (i accept only .jaiva, .jiv or .jva)");
        }
        File myObj = new File(filePath);
        ArrayList<Token<?>> tokens = new ArrayList<>();
        MultipleLinesOutput m = null;
        BlockChain b = null;
        Scanner scanner;
        try {
            scanner = new Scanner(myObj);
        } catch (FileNotFoundException e) {
            throw new UnknownFileException(filePath);
        }
        String previousLine = "";
        String comment = null;
        TConfig config = new TConfig();
        int lineNum = 1;
        while (scanner.hasNextLine()) {
            String line = (b != null ? b.currentLine() : scanner.nextLine());
            Object something = Tokenizer.readLine(line, (b != null ? "" : previousLine), m, b, lineNum, config);
            switch (something) {
                case MultipleLinesOutput multipleLinesOutput -> {
                    m = multipleLinesOutput;
                    b = null;
                }
                case ArrayList<?> ignored -> {
                    m = null;
                    b = null;
                    ArrayList<Token<?>> tks = (ArrayList<Token<?>>) ((ArrayList<Token<?>>) something).clone();
                    if (tks.size() == 1) {
                        for (Token<?> t : ((ArrayList<Token<?>>) something)) {
                            TokenDefault<?> l = t.value();
                            if (returnVfs && !l.exportSymbol) {
                                // clean.
                                tks.remove(t);
                                continue;
                            }
                            if (comment == null || !(l instanceof TSymbol))
                                continue;
                            JDoc doc = new JDoc(lineNum, comment.trim());
                            l.tooltip = doc;
                            l.json.removeKey("toolTip");
                            l.json.setJdoc("toolTip", doc);
                        }
                    }
                    comment = null;
                    tokens.addAll(tks);
                }
                case BlockChain blockChain -> {
                    m = null;
                    comment = null;
                    b = blockChain;
                }
                case Token<?> token1 when token1.value().name.equals("TDocsComment") -> {
                    b = null;
                    m = null;
                    comment = (comment == null ? "" : comment)
                            + ((TDocsComment) token1.value()).comment;
                }
                case Token<?> token when token.value() instanceof TExtendParams extendParams -> {
                    b = null;
                    m = null;
                    comment = null;
                    consumeExtension(extendParams, tokens.isEmpty() ? null : tokens.getLast().value(), lineNum);
                }
                case Token<?> token -> {
                    TokenDefault<?> t = token.value();
                    if (returnVfs && !t.exportSymbol) {
                        // dont do anythin.
                    } else if (comment != null
                            && t instanceof TSymbol) {

                        JDoc doc = new JDoc(t.lineNumber, comment.trim());
                        t.tooltip = doc;
                        t.json.removeKey("toolTip");
                        t.json.setJdoc("toolTip", doc);
                        tokens.add(token);
                    } else {
                        tokens.add(token);
                    }
                    b = null;
                    m = null;
                    comment = null;
                }
                case null, default -> {
                    b = null;
                    m = null;
                    comment = null;
                }
            }
            previousLine = line;
            if (b == null)
                lineNum++;
        }

        scanner.close();

        return tokens;
    }
}
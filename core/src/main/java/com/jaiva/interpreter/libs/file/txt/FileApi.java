package com.jaiva.interpreter.libs.file.txt;

import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.InterpreterException.CatchAllException;
import com.jaiva.errors.InterpreterException.FunctionParametersException;
import com.jaiva.errors.InterpreterException.WtfAreYouDoingException;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libBuilders.var.VariableBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.Exports;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TArrayVar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

@JaivaLibrary(path = "file/text", description = "The file text api for actually creating files and stuff yknow")
@Exports(FileQuery.class)
public class FileApi extends BaseLibrary {

    public FileApi(IConfig<Object> config) throws InterpreterException {
        add(new VFileName(config), new VDirectory(config), new VThis(config), new FFile(config), new FNew(config));
    }

    /**
     * Represents a variable holding the file name extracted from a file path.
     * <p>
     * This class extends {@link BaseVariable} and initializes the variable with the
     * file name
     * obtained from the provided {@link IConfig} object's file path. The variable
     * is named "f_name"
     * and is frozen upon creation to prevent further modification.
     */
    static class VFileName extends BaseVariable {
        public VFileName(IConfig<Object> config) {
            super(
                    VariableBuilder.start()
                            .name("f_name")
                            .value(config.getFilePath() == null ? "file" : config.getFilePath().getFileName().toString())
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Variable that holds the current file's name")
                                            .addNote("If you call this within the REPL, or somehow the filePath is null, it holds \"file\"")
                                            .sinceVersion("1.0.0")
                                            .addExample("""
                                            if (f_name != "myFile.jiv") ->
                                                khuluma("This is not myFile.jiv!")!
                                            <~
                                            """)
                            )
            );
            freeze();

        }
    }

    /**
     * Represents a variable that holds the current file's directory.
     * <p>
     * The {@code VDirectory} class extends {@link BaseVariable} and initializes
     * itself with the absolute path of the current file's directory, as specified
     * in the provided {@link IConfig} object. This variable is frozen upon
     * creation,
     * making it immutable.
     * </p>
     *
     */
    static class VDirectory extends BaseVariable {
        public VDirectory(IConfig<Object> config) {
            super(
                    VariableBuilder.start()
                            .name("f_dir")
                            .value(config.getFileDirectory() == null ? "file" : config.getFileDirectory().toAbsolutePath().toString())
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Variable that holds the current file's directory.")
                                            .addNote("If you call this within the REPL, or somehow the filePath is null, it holds \"file\"")
                                            .sinceVersion("1.0.0")
                                            .addExample("""
                                            khuluma("Current file directory is: " + f_dir)!
                                            """)
                            )
            );
            freeze();
        }
    }

    /**
     * Represents a special variable that encapsulates information about the current
     * file context
     * within the interpreter. The structure of this variable is as follows:
     * 
     * <pre>
     * [
     *   "fileName",                // Name of the current file, or "REPL" if not in a file context
     *   "fileDir",                 // Directory of the current file, or void value if not in a file context
     *   [contents],                // List of lines in the file, or a default list in REPL mode
     *   [canRead?, canWrite?, canExecute?] // file permission flags, or defaults in REPL mode
     * ]
     * </pre>
     * <p>
     * If the interpreter is running in REPL mode (no file context), default values
     * are used.
     * Otherwise, the file's name, directory, contents, and permissions are
     * extracted and stored.
     * The variable is frozen after initialization to prevent further modification.
     */
    static class VThis extends BaseVariable {
        /**
         * Constructs a VThis object representing the current file's structure and
         * metadata.
         * <p>
         * The structure of the array assigned to this variable is as follows:
         * 
         * <pre>
         * [
         *   "fileName",                // Name of the file or "REPL" if not in a file context
         *   "fileDir",                 // Directory of the file or void value if not in a file context
         *   [contents],                // List of file contents (lines) or sample data in REPL
         *   [canRead?, canWrite?, canExecute?] // List of booleans indicating file permissions
         * ]
         * </pre>
         * 
         * If the interpreter is running in REPL mode (no file context), default values
         * are used.
         * Otherwise, the file's name, directory, contents, and permissions are
         * extracted and stored.
         *
         * @param config    The interpreter configuration, including file path and
         *                  directory.
         * @throws InterpreterException If the file does not exist or cannot be read.
         */
        public VThis(IConfig<Object> config) throws InterpreterException {
            super(
                    VariableBuilder.start()
                            .name("f_this")
                            .value(new ArrayList<>())
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the current file's properties and contents.")
                                            .addNote("Returns an array containing the current file's properties \\n [fileName, fileDir, [contents], [canRead?, canWrite?, canExecute?]]")
                                            .addNote("Once again, if we are inside the REPL, we just return static content. (Does not represent the REPL)")
                                            .sinceVersion("1.0.1")
                                            .addExample("""
                                            f_this[0]! @ holds the file name
                                            f_this[1]! @ holds the file directory
                                            khuluma("File contents: " + f_this[2])! @ holds the file contents as an array
                                            khuluma("Can we write to this file? " + f_this[3][1])! @ holds the file permissions
                                            """))
            );
            // if we're in the file.
            /*
             * [
             * "fileName",
             * "fileDir",
             * [contents],
             * [canRead?, canWrite?, canExecute?]
             * ]
             */
            FileType fl;
            if (config.getFilePath() == null) {
                fl = FileType.of(
                        new FileCreator()
                );
                ((TArrayVar) this.token).contents.addAll(fl);

                this.a_unsafeSet(fl);

                return;
            }

            ArrayList<Object> file = new ArrayList<>();
            java.io.File f = config.getFilePath().toFile();
            Scanner fs;
            try {
                fs = new Scanner(f);
            } catch (FileNotFoundException e) {
                ((TArrayVar) this.token).contents = new ArrayList<>();
                return;
            }
            ArrayList<String> contents = new ArrayList<>();
            while (fs.hasNextLine())
                contents.add(fs.nextLine());
            fs.close();

            file.add(f.getName());
            file.add(config.getFileDirectory());
            file.add(contents);
            file.add(new ArrayList<>(Arrays.asList(f.canRead(), f.canWrite(), f.canExecute())));

            ((TArrayVar) this.token).contents = file; // set the token.
            a_unsafeSet(file); // set the interpreter variable.

            freeze(); // freeze this variable.
        }
    }

    /**
     * FFile is a function that retrieves properties and contents of a specified
     * file.
     * <p>
     * Usage: f_file(path)
     * </p>
     * <ul>
     * <li>Checks if the provided path is a string and resolves it relative to the
     * current configuration's file path if necessary.</li>
     * <li>Throws an exception if the file does not exist.</li>
     * <li>Reads the file contents line by line into a list.</li>
     * <li>Returns an ArrayList of the file properties
     * </ul>
     */
    static class FFile extends BaseFunction {
        // function looks for the file and returns its properties in the structure.
        public FFile(IConfig<Object> config) {
            super(
                    FunctionBuilder.start()
                            .name("f_file")
                            .setLib("file/text")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(
                                                    new AArgument(
                                                            "path", "The path to the file you want to fetch.",
                                                            false, Argument.Type.STRING
                                                    )
                                            )
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Finds the specified file and retrieves it's contents.")
                                            .addReturns("Returns an array containing the properties of the file at the given `path` \\n [fileName, fileDir, [contents], [canRead?, canWrite?, canExecute?]]")
                                            .addExample("""
                                            maak file <- f_file("data/myFile.txt")!
                                            khuluma("file name: " + file[0])!
                                            khuluma("file directory: " + file[1])!
                                            """)
                                            .sinceVersion("1.0.1")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object path = callProperties.params().getFirst();
            if (!(path instanceof String))
                throw new WtfAreYouDoingException(callProperties.scope(), "Da path must be a string.",
                        callProperties.getLineNumber());

            Path baseDir = (callProperties.config().getFilePath() != null) ? callProperties.config().getFilePath().getParent() : null;
            Path filePath;
            if (baseDir != null) {
                filePath = Paths.get((String) path);
                if (!filePath.isAbsolute()) {
                    filePath = baseDir.resolve((String) path).normalize();
                }
            } else {
                filePath = Paths.get((String) path);
            }

            java.io.File file = filePath.toFile();
            if (!file.exists())
                throw new WtfAreYouDoingException(new Scope(callProperties.config()), "file does not exist: " + filePath,
                        callProperties.getLineNumber());

            ArrayList<String> contents = new ArrayList<>();
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    contents.add(scanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                throw new CatchAllException(new Scope(callProperties.config()),
                        "Cannot read file but we found it... " + filePath, callProperties.getLineNumber());
            }

            ArrayList<Object> result = new ArrayList<>();
            result.add(file.getName());
            result.add(file.getParentFile() != null ? file.getParentFile().getAbsolutePath()
                    : Token.voidValue(callProperties.getLineNumber()));
            result.add(contents);
            result.add(new ArrayList<>(Arrays.asList(file.canRead(), file.canWrite(), file.canExecute())));
            return result;

        }
    }

    /**
     * FNew is a function that creates a new file with specified content and
     * permissions.
     * <p>
     * Usage: f_new(path, content, canRead?, canWrite?, canExecute?)
     * </p>
     * <ul>
     * <li>Checks if the provided path is a string.</li>
     * <li>Creates the file at the specified path with the given content.</li>
     * <li>Sets the file permissions based on the provided boolean flags.</li>
     * <li>Returns true if the file was created successfully, false otherwise.</li>
     * </ul>
     */
    public static class FNew extends BaseFunction {
        public FNew(IConfig<Object> config) {
            /*
             * [
             * "fileName",
             * "fileDir",
             * [contents],
             * [canRead?, canWrite?, canExecute?]
             * ]
             */
            super(
                    FunctionBuilder.start()
                            .name("f_new")
                            .setLib("file/text")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(new AArgument(
                                                    "path", "The path to the new file to create. Along with the file name and extension",
                                                    false, Argument.Type.STRING
                                            ))
                                            .add(new AArgument(
                                                    "content", "The content the file should hold. Either a string or an array of strings.",
                                                    false, Argument.Type.ANY
                                            ))
                                            .add(new AArgument(
                                                    "canRead", "Whether or not the file can be read. Defaults to true",
                                                    true, Argument.Type.BOOLEAN
                                            ))
                                            .add(new AArgument(
                                                    "canWrite", "Whether the file can be written to or not. Defaults to true",
                                                    true, Argument.Type.BOOLEAN
                                            ))
                                            .add(new AArgument(
                                                    "canExecute", "Whether the file can be executed or not. Defaults to true.",
                                                    true, Argument.Type.BOOLEAN
                                            ))
                            )
                            .docs(

                                    JDoc.builder()
                                            .addDesc("Creates a new file with the given properties at the given file.")
                                            .addReturns("A boolean `true` if the file could be created. `false` otherwise.")
                                            .addExample("""
                                            maak success <- f_new()!
                                            <| "data/newFile.txt"!
                                            <| arrLit("Hello, World!", "This is a new file.")!
                                            <| (true, true, false)!
                                            if (success) ->
                                                khuluma("file created successfully!")!
                                            <~ else ->
                                                khuluma("Failed to createFunction file.")!
                                            <~
                                            """)
                                            .sinceVersion("2.0.1")
                            )
            );
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            checkParams(callProperties.toCheckParams());
            Object path = callProperties.params().getFirst();
            if (!(path instanceof String))
                throw new FunctionParametersException(callProperties.scope(), this, "1", callProperties.params().get(0), String.class,
                        callProperties.getLineNumber());

            Object content = callProperties.params().get(1);
            StringBuilder outBuilder = new StringBuilder();
            String output = content instanceof String ? (String) content : "";
            if (!(content instanceof String) && !(content instanceof ArrayList))
                throw new FunctionParametersException(callProperties.scope(), this, "2", callProperties.params().get(1), String.class,
                        callProperties.getLineNumber());
            if (content instanceof ArrayList<?> list) {
                if (list.isEmpty())
                    output = "";
                // if the list has only one element, we can just use that as the content
                else if (list.size() == 1)
                    output = list.getFirst().toString();
                else {
                    list.stream()
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .forEach(s -> outBuilder.append(s).append("\n"));
                    output = outBuilder.toString();
                }
            }

            boolean canRead = true, canWrite = true, canExecute = true;
            if (callProperties.params().size() > 2) {
                Object cr = callProperties.params().get(2);
                if (!(cr instanceof Boolean))
                    throw new FunctionParametersException(callProperties.scope(), this, "3", callProperties.params().get(2), boolean.class,
                            callProperties.getLineNumber());
                canRead = cr.equals(Boolean.TRUE);
            }
            if (callProperties.params().size() > 3) {
                Object cw = callProperties.params().get(3);
                if (!(cw instanceof Boolean))
                    throw new FunctionParametersException(callProperties.scope(), this, "4", callProperties.params().get(3), boolean.class,
                            callProperties.getLineNumber());
                canWrite = cw.equals(Boolean.TRUE);
            }
            if (callProperties.params().size() > 4) {
                Object ce = callProperties.params().get(4);
                if (!(ce instanceof Boolean))
                    throw new FunctionParametersException(callProperties.scope(), this, "5", callProperties.params().get(4), boolean.class,
                            callProperties.getLineNumber());
                canExecute = ce.equals(Boolean.TRUE);
            }
            Path baseDir = (callProperties.config().getFilePath() != null) ? callProperties.config().getFilePath().getParent() : null;
            Path newFilePath = Paths.get((String) path);

            if (newFilePath.toFile().exists())
                return false; // file already exists.

            if (baseDir != null && !newFilePath.isAbsolute()) {
                newFilePath = baseDir.resolve((String) path).normalize();
            }
            try {
                Files.createDirectories(newFilePath.getParent()); // ensure parent directories exist
                Files.writeString(newFilePath, output, StandardOpenOption.CREATE_NEW);
                java.io.File newFile = newFilePath.toFile();
                boolean r = newFile.setReadable(canRead);
                boolean w = newFile.setWritable(canWrite);
                boolean e = newFile.setExecutable(canExecute);
            } catch (IOException e) {
                return false; // failed to createFunction file
            }
            return true; // file created successfully
            // return super.call(tFuncCall, params, config);
        }
    }
}

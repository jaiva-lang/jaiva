package com.jaiva.interpreter.runtime;

import com.jaiva.Config;
import com.jaiva.Main;
import com.jaiva.errors.Warnings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * The IConfig class provides configuration settings for the interpreter
 * runtime.
 * It allows customization of interpreter behavior through various flags and
 * options.
 */
public final class IConfig<T> extends Config {
    /**
     * The debug controller instance used to manage debugging features.
     */
    public final DebugController dc = new DebugController();
    private boolean printStacks = false;
    private String[] args = new String[] {};
    private final ArrayList<String> sanitisedArgs = new ArrayList<>();
    private ImportVfs importVfs = new ImportVfs(false);
    private boolean REPL = false;
    private boolean outLivedMain = false;
    private final GlobalResources globalResources;
    private Path filePath = null;
    private Path fileDirectory = null;
    private Object callerValue;
    private boolean streamer = false;
    private final ArrayList<Warnings.Warning> warnings = new ArrayList<>();
    private boolean jBundler = false;

    // ...add more interpreter settings.

    /**
     * Constructs a new IConfig instance with the specified file path.
     * <p>
     * This constructor is used to set the path of the current file being
     * interpreted.
     *
     * @param args            The command-line arguments passed to the jaiva
     *                        command.
     * @param currentFilePath The path of the current file being interpreted.
     * @throws NullPointerException if {@code currentFilePath} or {@code jSrc} is
     */
    public IConfig(@NotNull String[] args, String currentFilePath) {
        super();
        this.args = args;
        for (String arg : args) {
            if (arg.equals("-is") || arg.equals("--include-stacks"))
                printStacks = !isPrintStacks();
            if (arg.equals("-d") || arg.equals("--debug")) {
                printStacks = !isPrintStacks();
                dc.activate();
            }
            if (!arg.equals(currentFilePath) && !Main.tokenArgs.contains(arg)
            /* && !Main.replArgs.contains(arg) */) {
                // because if this overload is invoked, we're running a file, so we dont need to
                // check for REPL args.
                sanitisedArgs.add(arg);
            }
        }
        Path path = Path.of(currentFilePath != null ? currentFilePath : "").toAbsolutePath();
        filePath = path;
        fileDirectory = path.getParent();
        globalResources = new GlobalResources(
                this::isOutLivedMain,
                () -> System.exit(0)
        );
    }

    public boolean isOutLivedMain() {
        if (jBundler) return false;
        return outLivedMain;
    }

    public void setOutLivedMain(boolean outLivedMain) {
        this.outLivedMain = outLivedMain;
        globalResources.outlivedMainCheck();
    }

    /**
     * Constructs a new IConfig instance with the specified file path and Jaiva
     * source directory.
     * <p>
     * This constructor is used when the current file path and Jaiva source
     * directory
     * are provided as arguments. Primarily used by the interpreter when importing
     * other files into the current context. This is so that we can just pass the
     * sanitized args already.
     *
     * @param args            The sanitized command-line arguments passed to the
     *                        jaiva
     *                        command.
     * @param currentFilePath The path of the current file being interpreted.
     */
    public IConfig(@NotNull ArrayList<String> args, String currentFilePath) {
        this(args.toArray(new String[0]), currentFilePath);
    }

    /**
     * Constructs a new IConfig instance with the specified Jaiva source directory.
     * <p>
     * This constructor is used when only the Jaiva source directory is provided,
     * typically in a REPL context.
     */
    public IConfig() {
        super();
        globalResources = new GlobalResources(
                this::isOutLivedMain,
                () -> System.exit(0)
        );
    }

    public <V> void add(V callerValue) {
        this.callerValue = callerValue;
    }
    /**
     * The caller value provided by a host Java app through {@link com.jaiva.JBundler}
     */
    public Object getCallerValue() {
        return callerValue;
    }

    public void streamer() {
        streamer = true;
    }
    /**
     * Whether we are in streamer mode or not.
     */
    public boolean isStreamer() {
        return streamer;
    }

    /**
     * Adds a new warning
     * @param warning A warning
     */
    public void addWarning(Warnings.Warning warning) {
        warnings.add(warning);
    }
    /**
     * Collected warnings when interpreting via {@link com.jaiva.Streamer}
     */
    public ArrayList<Warnings.Warning> getWarnings() {
        return warnings;
    }

    /**
     * This flag is used to print stack traces when an error occurs during
     * interpretation.
     */
    public boolean isPrintStacks() {
        return printStacks;
    }

    /**
     * The command-line arguments passed to the Jaiva tokenizer and interpreter.
     * This array is used to tell the user what arguments were passed to the current
     * file.
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * The sanitised arguments list stores the command-line arguments without
     * arguments used by jaiva. It removes the first argument (file path) and
     * possibly second argument (which is sometimes the debug flag).
     * This is useful for processing the arguments in a more user-friendly way.
     */
    public ArrayList<String> getSanitisedArgs() {
        return sanitisedArgs;
    }

    /**
     * This flag is used when the interpreter needs to import the vfs from another
     * file to use in the current file. (This means it will skip tokenising other
     * stuff and only import exported symbols.)
     */
    public ImportVfs getImportVfs() {
        return importVfs;
    }

    public void setImportVfs(ImportVfs importVfs) {
        this.importVfs = importVfs;
    }

    /**
     * Boolean flag indicating we're in the REPL.
     */
    public boolean isREPL() {
        return REPL;
    }

    public void setREPL(boolean REPL) {
        this.REPL = REPL;
    }

    /**
     * * The {@code Resources} instance provides access to global resources
     * used by the intepreter at run time.
     */
    public GlobalResources getGlobalResources() {
        return globalResources;
    }

    public void releaseAll() throws IOException {
        globalResources.releaseCurrent();
        globalResources.interruptAll();
    }

    /**
     * The path of the current file being interpreted.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * The directory containing the file we're interpreting
     */
    public Path getFileDirectory() {
        return fileDirectory;
    }

    public void setjBundler(boolean jBundler) {
        this.jBundler = jBundler;
    }
}
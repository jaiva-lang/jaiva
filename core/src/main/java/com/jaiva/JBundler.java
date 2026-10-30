package com.jaiva;


import com.jaiva.interpreter.Interpreter;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.Globals;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.tokenizer.tokens.Token;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JBundler allows other Java programs which have Jaiva as a dependency, to define their own set
 * of functions as libraries that a jaiva program can import.
 * <p>
 * Having 2 different instances of JBundler, means you have 2 different jaiva instances booted
 * up to run. Meaning they won't share the classes given to them unless you specifically make
 * them.
 */
public final class JBundler {
    private static int instances = 0;
    public final int instanceNum;

    private final String filePath;
    private ArrayList<Token<?>> tokens = new ArrayList<>();
    private final List<Class<? extends BaseLibrary>> classes;

    /**
     * Default Constructor for JBundler
     * @param fp The filepath of said file
     * @param cls The classes to import as libraries
     * @throws Exception When any of the jaiva processing fails.
     */
    @SafeVarargs
    public JBundler(String fp, Class<? extends BaseLibrary> ...cls) throws Exception {
        this.filePath = fp;
        instanceNum = instances;
        instances++;
        classes = Arrays.asList(cls);
    }

    /**
     * Constructor to make a JBundler instance without a specific path.
     * @implSpec If using this constructor, you can only use {@link #execute(String, Object)}
     * @param cls The var args of interpreter classes.
     */
    @SafeVarargs
    public JBundler(Class<? extends BaseLibrary>... cls) {
        this.filePath = "";
        instanceNum = instances;
        instances++;
        classes = Arrays.asList(cls);
    }

    public JBundler(String packagePath) {
        this.filePath = "";
        instanceNum = instances;
        instances++;
        classes = new ArrayList<>(findExternalLibraries(packagePath, JBundler.class.getClassLoader()));
    }

    /**
     * Tokenizes and interprets the given filePath
     * @param filePath THe file path
     * @throws Exception If anything occurs
     */
    public <V> void execute(String filePath, V jaivaValue) throws Exception {
        ArrayList<Token<?>> tokens = Main.parseTokens(filePath, false);
        IConfig<Object> config = new IConfig<>(new ArrayList<>(List.of("jaiva")), filePath);
        config.setjBundler(true);
        config.add(jaivaValue);
        Scope sc = new Scope(config, classes);
        Interpreter.interpret(tokens, sc, (IConfig<Object>) config);
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    public ArrayList<Token<?>> getTokens() {
        return new ArrayList<>(tokens);
    }

    public void tokenize() throws Exception {
        tokens = Main.parseTokens(filePath, false);
    }

    public void  interpret() throws Exception {
        IConfig<Object> config = new IConfig<>(new ArrayList<>(List.of("jaiva")), filePath);
        config.setjBundler(true);
        Scope scope = new Scope(config, classes);

        Interpreter.interpret(tokens, scope, config);
    }

    public void run() throws Exception {
        tokenize();
        interpret();
    }

    public static int getInstances() {
        return instances;
    }


    @NotNull
    private static List<Class<? extends BaseLibrary>> findExternalLibraries(
            String packagePath, ClassLoader cl
    ) {
        try (ScanResult scan = new ClassGraph()
                .enableClassInfo()
                .overrideClassLoaders(cl)
                .enableAnnotationInfo()
                .acceptPackages(packagePath)
                .scan()) {

            ClassInfoList candidates = scan.getClassesWithAnnotation(PublicLibrary.class.getName())
                    .filter(ci -> !ci.isInterface() && !ci.isAbstract());

            List<Class<? extends BaseLibrary>> result = new ArrayList<>();
            for (var ci : candidates) {
                @SuppressWarnings("unchecked")
                Class<? extends BaseLibrary> cls = (Class<? extends BaseLibrary>) ci.loadClass();
                result.add(cls);
            }
            return result;
        }
    }
}

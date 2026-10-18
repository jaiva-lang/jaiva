package com.jaiva.interpreter.libs.global;

// To scan for all the internal libraries isntead of having to renference everytime

import com.jaiva.JBundler;
import com.jaiva.Plugin;
import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibraryLike;
import com.jaiva.interpreter.libs.annotation.Exports;
import com.jaiva.interpreter.libs.annotation.GlobalsLib;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.Symbol;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Globals class has 2 pretty signifact jobs lol
 * <ol>
 *     <li>
 *         It is responsible for creating the actual "global" scope. In that it can:
 *         <ul>
 *             <li>Find all the other libraries defined by Jaiva in {@link com.jaiva.interpreter.libs}</li>
 *             <li>Handle externally provided libraries from other apps hosting Jaiva through manual instantiation (in {@link Plugin}) or via {@link JBundler}</li>
 *             <li>
 *                 Handle the {@link Exports} library annotation for either of the above by holding
 *                 a list to all the class libraries it found
 *             </li>
 *             <li>Allow tooling to export certian stuff</li>
 *         </ul>
 *     </li>
 *     <li>
 *         It is itself, the global "library" too, in that its not a library you can actually import
 *         because it's already injected into {@link Scope#Scope(IConfig)} which is itself the global
 *         scope. Meaning the functions defined in the containers {@link GlobalFunctions} and {@link IOFunctions}
 *         are global as they can be called anywhere. Examples include {@code khuluma()} or {@code vesion}
 *     </li>
 * </ol>
 * @see GlobalFunctions
 * @see IOFunctions
 * @see LibraryLike
 * @see BaseLibrary
 * @see Plugin
 * @see JaivaLibrary
 * @see PublicLibrary
 * @see JBundler
 * @see Vfs
 * @see Symbol
 * @author Lehlogonolo Poole
 */
@GlobalsLib
public class Globals extends BaseLibrary {

    private final ArrayList<LibraryLike> allClassLibraries = new ArrayList<>();
    private final ArrayList<LibraryLike> externalLibraries = new ArrayList<>();
    // public Vfs vfs = new HashMap<>();

    public HashMap<String, LibraryLike> builtInGlobals = new HashMap<>();

    /**
     * Default Constructor. This constructor is used when creating a new global {@link Scope}
     * in normal Jaiva scripts. This also initialises what Globals actually holds.
     * @implNote Since this is mostly decoupled form the actual execution, you can find it being
     * instantiated purely for tooling purposes other than actually executing.
     *
     * @param config The interpreter config
     *
     * @throws InterpreterException if something goes wrong lol
     */
    public Globals(IConfig<Object> config) throws JaivaException {
        this(config, Globals.class.getClassLoader(), new ArrayList<>());
    }

    public Globals(IConfig<Object> config, ClassLoader classLoader) throws JaivaException {
        this(config, classLoader, new ArrayList<>());
    }

    /**
     * Constructor used when creating a new global {@link Scope} but specifically via {@link JBundler}
     * or {@link Plugin} (Although not used) to also instantiate and store external libraries in the same position as jaiva
     * built in libraries, making it seem to the user that it's apart of Jaiva when really the host provided custom
     * libraries
     * @param config The interpreter config
     * @param classLoader The class loader to use when loading external classes. usually this shouldnt apply hence passing
     *                    null will default to {@link Globals}'s class loader instead.
     * @param external The list of external classes to store
     * @throws InterpreterException if something goes wrong lol
     */
    public Globals(IConfig<Object> config, ClassLoader classLoader, @NotNull List<Class<? extends BaseLibrary>> external) throws JaivaException {
        super();
        allClassLibraries.addAll(putGlobals(config, classLoader == null ? Globals.class.getClassLoader() : classLoader));
        for (Class<? extends BaseLibrary> ext : external) {
            String path = BaseLibrary.externalLibraryRequirements(ext);
            LibraryLike lk = LibraryLike.of(ext);
            builtInGlobals.put(path, lk);
            allClassLibraries.add(lk);
            externalLibraries.add(lk);
        }
    }

    public ArrayList<LibraryLike> putGlobals(IConfig<Object> config, ClassLoader cl) throws JaivaException {

        // add the actual global stuff to this current isntance of globals
        add(new GlobalFunctions(config));

        // cuz its a file we have to add manually
        builtInGlobals.put("arrays", LibraryLike.of("arrays.jiv"));
        // we could also add a class def LibraryLike accepts it and eworks normally
        // builtInGlobals.put("path", LibraryLike.of(Something.class));
        // but nowadays classes are found reflectively in com.jaiva.interpreter.libs
        return putJaivaLibraries(
                findInternalLibraries(cl)
        );
    }

    /**
     * Puts the built-in libraries
     * @param libs The list of classes
     * @return An arraylist of {@link LibraryLike} objects wrapping the {@link BaseLibrary} class
     * to be used downstream.
     */
    @NotNull
    private ArrayList<LibraryLike> putJaivaLibraries(@NotNull List<Class<? extends BaseLibrary>> libs) {
        ArrayList<LibraryLike> libraries = new ArrayList<>();
        for (Class<? extends BaseLibrary> lib : libs) {
            JaivaLibrary library = lib.getAnnotation(JaivaLibrary.class);
            String path = library.path();

            LibraryLike l = LibraryLike.of(lib);
            libraries.add(l);
            builtInGlobals.put(path, l);
        }
        return libraries;
    }

    public ArrayList<LibraryLike> getAllClassLibraries() {
        return new ArrayList<>(allClassLibraries);
    }

    public ArrayList<LibraryLike> getExternalLibraries() {
        return new ArrayList<>(externalLibraries);
    }

    public String returnGlobalsOf(@NotNull String label, IConfig<Object> i) throws Exception {
        Vfs VFS = getVfs(i, this);
        if (!label.equals("jaiva/global")) {
            String label2 = label.replace("jaiva/", "").replace("jaiva\\", "");
            LibraryLike l = builtInGlobals.get(label2);
            if (l != null)
                VFS = l.load(i, this);
        }
        return BaseLibrary.vfsToJson(VFS);
    }

    @Nullable
    public Vfs getBuiltInGlobal(@NotNull String name) throws Exception {
        if (name.startsWith("jaiva/") || name.startsWith("jaiva\\")) {
            name = name.substring(6);
        }
        if (!builtInGlobals.containsKey(name)) return null;
        return builtInGlobals.get(name).load(new IConfig<>(), this);
    }

    @NotNull
    public static List<Class<? extends BaseLibrary>> findInternalLibraries(
            ClassLoader cl
    ) {
        try (ScanResult scan = new ClassGraph()
                .enableClassInfo()
                .overrideClassLoaders(cl)
                .enableAnnotationInfo()
                .acceptPackages("com.jaiva.interpreter.libs")
                .scan()) {

            ClassInfoList candidates = scan.getClassesWithAnnotation(JaivaLibrary.class.getName())
                    .filter(ci -> !ci.isInterface() && !ci.isAbstract());

            List<Class<? extends BaseLibrary>> result = new ArrayList<>();
            for (var ci : candidates) {
                @SuppressWarnings("unchecked")
                Class<? extends BaseLibrary> cls = (Class<? extends BaseLibrary>) ci.loadClass();
                if (cls == Globals.class) continue;
                result.add(cls);
            }
            return result;
        }
    }


}

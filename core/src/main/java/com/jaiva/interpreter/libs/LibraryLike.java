package com.jaiva.interpreter.libs;

import com.jaiva.errors.JaivaException;
import com.jaiva.errors.LoadException;
import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.runtime.IConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class that represents either a built-in library class or an external library by name.
 * It provides a method to hold a library reference to load only when needed. (lazy loading)
 * <p>
 *     This class can be instantiated with either a Class object representing a subclass of BaseLibrary
 *     for built-in libraries, or a String representing the name of an external library.
 * </p>
 */
public final class LibraryLike {
    private final Object lib;
    private LibraryLike(Class<? extends BaseLibrary> lib) {
        this.lib = lib;
    }
    private LibraryLike(String libName) {
        this.lib = libName;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static LibraryLike of(Class<? extends BaseLibrary> lib) {
        return new LibraryLike(lib);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static LibraryLike of(String libName) {
        return new LibraryLike(libName);
    }

    public BaseLibrary loadClassLibrary(IConfig<Object> i, Globals globals) throws JaivaException {
        if (lib instanceof BaseLibrary b) return b;
        if (!(lib instanceof Class<?> c)) throw new LoadException.LibraryLikeGetException(
                "The object stored by this lazily initializer is not a class."
        );

        return BaseLibrary.instantiate(c, i, globals);
    }

    @NotNull
    public Vfs load(IConfig<Object> i, Globals globals) throws Exception {
        switch (lib) {
            case Class<?> ignored -> {
                return loadClassLibrary(i, globals).getVfs(i, globals);
            }
            case BaseLibrary r -> {
                return r.getVfs(i, globals);
            }
            case String s -> {
                try {
                    ExternalLibraryLoader loader = new ExternalLibraryLoader();
                    return loader.loadLibrary(s, i);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to load library: " + s, e);
                }
            }
            default -> throw new IllegalStateException("Invalid library type: " + lib.getClass().getName());
        }
    }

    public boolean hasClass() {
        return lib instanceof Class<?> c;
    }

    @Nullable
    public Class<? extends BaseLibrary> getLibClass() {
        if (!hasClass()) return null;
        return (Class<? extends BaseLibrary>) lib;
    }
}

package com.jaiva.interpreter.libs;

import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.libs.annotation.Exports;
import com.jaiva.interpreter.libs.global.Globals;
import com.jaiva.interpreter.runtime.IConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Why is this a record? To be explicit of course.
 * <p>
 *     An Import Promise, is basically a deferred import, in that when  {@link BaseLibrary#instantiate(Class, IConfig, Globals)}
 *     encounters a {@link BaseLibrary} with {@link Exports} annotation, instead of resolving the graph immediately, it creates
 *     a "promise" to resolve the import when {@link BaseLibrary#getVfs(IConfig, Globals)} is called instead.
 * </p>
 * <p>
 *     This just pushes away logic, in the sense that:
 *     <ol>
 *         <ul>
 *             For tooling purposes, we only care about {@link LibrarySymbol} or alike, if {@link BaseLibrary#instantiate(Class, IConfig, Globals)}
 *             was eager, we would have no way to differentiate which symbols came from something else for tooling such as
 *             markdown output, however
 *         </ul>
 *         <ul>
 *             For actual interpreter purposes, it cares about {@link Vfs} which is inherently, everything this library has
 *             even the exports, this is for things such as making a new scope and a library was imported, or even the only
 *             tool that wants everything this library offers, the JSON output.
 *         </ul>
 *     </ol>
 * </p>
 * @see BaseLibrary#instantiate(Class, IConfig, Globals)
 * @see BaseLibrary#getVfs(IConfig, Globals)
 * @see Exports
 * @see LibrarySymbol
 * @see Vfs
 * @see BaseLibrary
 * @see ImportChain
 * @author Lehlogonolo Poole
 * @param classToImportFrom The class that the parent class promises to import.
 */
public record ImportPromise(
        Class<? extends BaseLibrary> classToImportFrom
) {
    /**
     * Determines the intersection between 2 lists of import promises by looping through each and collecting them.
     * @param a The first list of import promises
     * @param b The second list of import promises
     * @return The Set of which contains classes which appear in a promise from both lists. Otherwise an empty set if none
     * were found
     */
    @NotNull
    public static HashSet<Class<? extends BaseLibrary>> intersect(@NotNull ArrayList<ImportPromise> a, ArrayList<ImportPromise> b) {
        HashSet<Class<? extends BaseLibrary>> result = new HashSet<>();
        if (a.isEmpty() || b.isEmpty()) return result;

        for (ImportPromise i : a) {
            for (ImportPromise j : b) {
                if (i.equals(j)) result.add(i.classToImportFrom());
            }
        }
        return result;
    }
}

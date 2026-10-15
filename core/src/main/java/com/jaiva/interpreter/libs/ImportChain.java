package com.jaiva.interpreter.libs;

import com.jaiva.interpreter.libs.global.Globals;
import com.jaiva.interpreter.runtime.IConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Import Chain, is essentially a linked-list type of call stack of multiple {@link BaseLibrary} instances
 * calling each other.
 * <p>
 *     It allows for the current {@link BaseLibrary} being resolved in {@link BaseLibrary#getVfs(IConfig, Globals)}
 *     to inspect all the classes before that triggered its own instantiation to detect and error upon circular dependencies
 *     ({@code A -> B -> A}) and duplicated inherited libraries ({@code A -> B -> C} but {@code A} also tries to export {@code C}
 *     directly)
 * </p>
 * @param importedFrom The class which caused the instantiation of this node
 * @param link The previous {@link ImportChain}, if {@link Optional#empty()} then this is the head of the entire chain
 */
public record ImportChain(
        Class<? extends BaseLibrary> importedFrom,
        Optional<ImportChain> link
) {
    @NotNull
    public ArrayList<Class<? extends BaseLibrary>> getClassesInChain() {
        ArrayList<Class<? extends BaseLibrary>> list = new ArrayList<>();

        ImportChain current = this;

        while (current != null) {
            list.add(current.importedFrom);
            current = current.link.orElse(null);
        }

        return list;
    }
}

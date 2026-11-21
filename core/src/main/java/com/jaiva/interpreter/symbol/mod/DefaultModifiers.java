package com.jaiva.interpreter.symbol.mod;

import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.Symbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DefaultModifiers {

    CONSTANT("frozen",
            Collections.emptyList(),
            "const", "read-only", "constant", "f"),
    META("meta", Collections.emptyList(), "metadata");

    private final SymbolModifier instance;

    DefaultModifiers(String canonicalName, List<String> mutuallyExclusiveTo, String... aliases) {
        instance = new SymbolModifier(canonicalName, mutuallyExclusiveTo, aliases);
    }

    public SymbolModifier getInstance() {
        return instance;
    }

    @Nullable
    public static SymbolModifier fromModString(String modString) {
        for (DefaultModifiers mod : values()) {
            if (mod.getCanonicalName().equals(modString) || mod.getAliases().contains(modString)) {
                return mod.instance;
            }
        }
        return null;
    }

    @Nullable
    public static SymbolModifier fromModString(String modString, @NotNull IConfig<Object> config) {
        ArrayList<SymbolModifier> merged = new ArrayList<>();
        merged.addAll(Stream.of(values()).map(DefaultModifiers::getInstance).toList());
        merged.addAll(config.getCustomModifiers());
        for (SymbolModifier mod : merged) {
            if (mod.getCanonicalName().equals(modString) || mod.getAliases().contains(modString)) {
                return mod;
            }
        }
        return null;
    }

    public String getCanonicalName() {
        return getInstance().getCanonicalName();
    }

    public ArrayList<String> getAliases() {
        return getInstance().getAliases();
    }

    public List<String> getMutuallyExclusiveTo() {
        return getInstance().getMutuallyExclusiveTo();
    }

    @NotNull
    public ArrayList<String> getAllAliases() {
        return getInstance().getAllAliases();
    }

    public static ArrayList<String> getAll() {
        return Arrays.stream(values())
                .map(DefaultModifiers::getAllAliases)
                .flatMap(ArrayList::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

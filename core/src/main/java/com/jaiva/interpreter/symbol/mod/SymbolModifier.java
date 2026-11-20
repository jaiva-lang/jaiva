package com.jaiva.interpreter.symbol.mod;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolModifier {
    private final String canonicalName;
    private final List<String> mutuallyExclusiveTo;
    private final ArrayList<String> aliases;

    public SymbolModifier(String canonicalName, List<String> mutuallyExclusiveTo, String... aliases) {
        this.canonicalName = canonicalName;
        this.mutuallyExclusiveTo = mutuallyExclusiveTo;
        this.aliases = new ArrayList<>(Arrays.asList(aliases));
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public ArrayList<String> getAliases() {
        return aliases;
    }

    public List<String> getMutuallyExclusiveTo() {
        return mutuallyExclusiveTo;
    }

    @NotNull
    public ArrayList<String> getAllAliases() {
        ArrayList<String> result = new ArrayList<>(aliases);
        result.addFirst(canonicalName);
        return result;
    }
}

package com.jaiva.interpreter.symbol.mod;

import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.Warnings;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.tokenizer.tokens.TSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ModifierValidator {
    public static void validateModifiers(IConfig<Object> config, Scope sc, int lineNumber, @NotNull TSymbol symbol)
            throws InterpreterException.SymbolModifiersException, InterpreterException.NoWarningsException {
        ArrayList<String> mods = symbol.getModifiers();
        HashMap<String, SymbolModifier> modifiersHashMap = new HashMap<>();
        for (String mod : mods) {
            SymbolModifier m = DefaultModifiers.fromModString(mod, config);
            if (m == null) continue;
            if (modifiersHashMap.containsValue(m)) {
                Warnings.println(
                        lineNumber,
                        "The modifier " + mod + " is already covered by " + modifiersHashMap.entrySet().stream()
                                .filter(e -> e.getValue() == m)
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElse(""),
                        sc, config
                );
            }
            modifiersHashMap.put(mod, m);
            if (m == null) continue;

            for (String exclusive :
                    m.getMutuallyExclusiveTo()) {
                SymbolModifier excl = DefaultModifiers.fromModString(exclusive, config);
                if (excl != null) {
                    if (mods.stream().anyMatch(excl.getAllAliases()::contains))
                        throw new InterpreterException.SymbolModifiersException(sc, lineNumber, mod, exclusive);
                }
            }
        }
    }
}

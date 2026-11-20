package com.jaiva.tokenizer.tokens;

import java.util.ArrayList;

/**
 * TSymbol is an empty interface which groups all common tokens
 * that can be exported and used within other Jaiva files, documented
 * and just either hold and use data.
 */
public interface TSymbol {
    ArrayList<String> getModifiers();
    void setModifiers(ArrayList<String> mods);
}

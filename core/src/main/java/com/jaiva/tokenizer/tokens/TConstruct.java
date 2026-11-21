package com.jaiva.tokenizer.tokens;

import com.jaiva.tokenizer.tokens.metadata.CodeRegion;

import java.util.ArrayList;

/**
 * TConstruct is an empty interface which holds all the tokens
 * that have a CodeRegion with more tokens/executable code with in.
 */
public interface TConstruct {
    ArrayList<CodeRegion> getCodeBlocks();
}

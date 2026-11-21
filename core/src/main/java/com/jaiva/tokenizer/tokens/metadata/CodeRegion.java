package com.jaiva.tokenizer.tokens.metadata;

import com.jaiva.tokenizer.tokens.Token;
import io.github.yetnt.utils.builders.SimpleJSON;
import io.github.yetnt.utils.builders.ToJSON;

import java.util.ArrayList;

/**
 * Represents a code block such as {@code -> maak x <- 10; maak y <- 20! <~}
 * This class usually isn't used directly, but rather as a part of another
 * instance
 */
public final class CodeRegion implements ToJSON<SimpleJSON, Exception> {
    /**
     * The lines of code in the code block.
     */
    public ArrayList<Token<?>> lines;
    /**
     * The line number where the ending delimiter of the code block is found. (<~)
     */
    public int lineNumberEnd;

    public int lineNumber;

    /**
     * Constructor for CodeRegion
     *
     * @param lines The lines of code in the code block.
     * @param ln    The line number where the code block starts.
     * @param endLn The line number where the code block ends.
     */
    public CodeRegion(ArrayList<Token<?>> lines, int ln, int endLn) {
        this.lineNumber = ln;
        this.lines = lines;
        this.lineNumberEnd = endLn;
    }

    @Override
    public SimpleJSON toJson() throws Exception {
        SimpleJSON json = new SimpleJSON();
        json.set("lineNumber", lineNumber);
        json.set("lineEnd", lineNumberEnd);
        json.set("lines", lines);
        return json;
    }

}

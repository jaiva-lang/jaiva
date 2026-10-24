package com.jaiva.tokenizer.tokens;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.Convertible;
import com.jaiva.tokenizer.NewJson;
import com.jaiva.tokenizer.jdoc.JDoc;

import java.nio.file.Path;

/**
 * TokenDefault class is a default implementation of all the other
 * subclasses/tokens defined in {@link Token}
 */
public class TokenDefault<T extends TokenDefault<T>> implements Convertible {
    public Token<T> toToken() {
        return null;
    }

    /**
     * The name of the token.
     * <p>
     * If the token is one that doesnt have a definitive name, such as an if
     * statement, it will be the token class name.
     */
    public String name = "";
    /**
     * The tooltip or description of the token.
     */
    public Object tooltip = "Jaiva Construct";
    /**
     * The line number associated with the token.
     */
    public int lineNumber = 0;
    /**
     * The JSON representation of the token.
     */
    public NewJson json;
    /**
     * Boolean to check if the token is a symbol that can be exported.
     */
    public boolean exportSymbol = false;

    private Object fileOrigin;
    /**
     * Constructor for a basic token
     * 
     * @param name Token name
     * @param line Line number where token appears
     */
    public TokenDefault(String name, int line) {
        this.name = name;
        this.lineNumber = line;
        this.json = new NewJson(false, name, getClass().getSimpleName(), line, tooltip);
    }

    /**
     * Constructor for a symbol token that can be exported
     *
     * @param exportSymbol Boolean to mark this symbol as exportable.
     * @param name Token name
     * @param line Line number where token appears
     */
    public TokenDefault(boolean exportSymbol, String name, int line) {
        this.name = name;
        this.lineNumber = line;
        this.exportSymbol = exportSymbol;
        this.json = new NewJson(exportSymbol, name, getClass().getSimpleName(), line, tooltip);
    }

    /**
     * Constructor for a globals.
     * 
     * @param name Token name
     * @param line Token line number
     * @param tt Token tooltip
     */
    public TokenDefault(String name, int line, JDoc tt) {
        this.name = name;
        this.lineNumber = line;
        this.tooltip = tt;
        this.json = new NewJson(false, name, getClass().getSimpleName(), line, tt);
    }

    /**
     * Returns the JSON representation of the object as a string.
     * 
     * @return the final JSON representation.
     * @throws JaivaException For any errors
     */
    @Override
    public NewJson toJson() throws JaivaException {
        return json;
    }

    public Object getFileOrigin() {
        return fileOrigin;
    }

    public void setFileOrigin(Object fileOrigin) {
        this.fileOrigin = fileOrigin;
    }
}
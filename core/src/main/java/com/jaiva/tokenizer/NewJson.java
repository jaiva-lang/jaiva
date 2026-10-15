package com.jaiva.tokenizer;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.yetnt.utils.builders.SimpleJSON;
import org.jetbrains.annotations.NotNull;

public class NewJson extends SimpleJSON {
    public NewJson(boolean exportSymbol, String name, String type, int ln, Object tooltip) {
        set("type", type);
        set("name", name);
        set("exportSymbol", exportSymbol);
        setJdoc("toolTip", tooltip); // usually a string or JDoc object.
        set("lineNumber", ln);
        // Add special support for Token
        addSupportFor(Token.class, (token) -> {
            try {
                return token.value().toJson();
            } catch (JaivaException e) {
                // usually wont happen but regardless-
                throw new RuntimeException(e);
            }
        });
        // TokenDefault implements ToJson so its handled by default.
    }

    public boolean keyExists(String key) {
        return map.containsKey(key);
    }

    public void removeKey(String key) {
        map.remove(key);
    }

    // When Maven publishing limit has been freed, update SimpleJSON to also allow converting
    // via ToJSON to an ArrayList and not a SimpleJSON object only. (So JDoc can be supported)

    public void setJdoc(String name, @NotNull Object value) {
        if (value instanceof JDoc v)
            set(name, v.simpleJsonList());
        else if (value instanceof String s)
            set(name, s);
        else throw new UnsupportedOperationException();
    }
}

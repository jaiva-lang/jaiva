package com.jaiva.tokenizer;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TCodeblock;
import com.yetnt.utils.builders.SimpleJSON;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import java.util.Map;

public class NewJson extends SimpleJSON {

    public NewJson(boolean exportSymbol, String name, String type, int ln, Object tooltip) {
        this();
        set("type", type);
        set("name", name);
        set("exportSymbol", exportSymbol);
        set("toolTip", tooltip); // usually a string or JDoc object.
        set("lineNumber", ln);
    }

    public NewJson() {
        addSupportFor(Token.class, (token) -> {
            try {
                return token.value().toJson();
            } catch (JaivaException e) {
                // usually wont happen but regardless-
                throw new RuntimeException(e);
            }
        });
    }

    public NewJson(Map<String, Object> map) {
        super(map);
    }

    public boolean keyExists(String key) {
        return map.containsKey(key);
    }

    public void removeKey(String key) {
        map.remove(key);
    }

    public void nullSet(String name, Object body1) {
        if (body1 == null)
            set(name);
        else
            set(name, body1);
    }
}

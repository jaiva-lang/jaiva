package com.jaiva.tokenizer;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.yetnt.utils.builders.SimpleJSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewJson extends SimpleJSON {
    public NewJson(boolean exportSymbol, String name, String type, int ln, Object tooltip) {
        set("type", type);
        set("name", name);
        set("exportSymbol", exportSymbol);
        if (tooltip instanceof String s) {
            set("toolTip", s);
        } else if (tooltip instanceof JDoc jDoc) {
            set("toolTip", jDoc.toJson());
        }
        set("lineNumber", ln);
    }

    public <T> void set(String name, T v) throws JaivaException {
        switch (v) {
            case null -> set(name);
            case TokenDefault td -> set(name, td);
            case Token t -> set(name, t);
            case String s -> set(name, s);
            case Boolean s -> map.put(name, s);
            case Number s -> set(name, s);
            case ArrayList a -> list(name, a);
            default -> {
                if (v.getClass().isArray())
                    set(name, (Object[])v);
                else
                    throw new RuntimeException("");
            }
//            default -> map.put(name, v);
        }
    }

    private void list(String name, Iterable a) throws JaivaException {
        ArrayList<Object> objects = new ArrayList<>();
        for (Object o : a) {
            if (o instanceof Token<?> z)
                objects.add(z.value().toJson());
            else if (o instanceof TokenDefault<?> d)
                objects.add(d.toJson());
            else objects.add(o);
        }
        set(name, objects);
    }

    public <T> void set(String name, T[] v) throws JaivaException {
        list(name, new ArrayList<>(Arrays.asList(v)));
    }

    public boolean keyExists(String key) {
        return map.containsKey(key);
    }

    public void removeKey(String key) {
        map.remove(key);
    }

    public void set(String name, Token value) throws JaivaException {
        set(name, value.value());
    }

    public void set(String name, JDoc value) throws JaivaException {
        set(name, value.toJson());
    }
    public void set(String name, TokenDefault value) throws JaivaException {
        if (value == null) set(name);
        else set(name, value.toJson());
    }
}

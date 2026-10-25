package com.jaiva.tokenizer.jdoc.tags;

import com.jaiva.errors.TokenizerException;
import com.jaiva.lang.Chars;

public final class DParameter extends Tag {
    public String varName;
    public boolean optional;
    public String type;
    public String desc;

    public DParameter(String input, int lineNumber) throws TokenizerException.MalformedJDocException {
        super(TagType.PARAMETER);
        // given an input such as "par <- type docs"
        if (!input.contains("<-")) {
            throw new TokenizerException.MalformedJDocException(lineNumber,
                    "Parameter tag input must contain '<-' between the parameter name and it's type. Given: " + input);
        }
        String[] parts = input.split(Chars.ASSIGNMENT);
        varName = parts[0].trim();
        optional = varName.endsWith("?");
        if (optional) varName = varName.substring(0, varName.length() - 1);
        String trimmed = parts[1].trim();
        int firstSpace = trimmed.indexOf(' ');
        type = firstSpace == -1 ? trimmed : trimmed.substring(0, firstSpace).trim();
        desc = firstSpace == -1 ? "" : trimmed.substring(firstSpace + 1).trim();

        attributes.put("var", varName);
        attributes.put("type", type);
        attributes.put("optional", optional);
        if (addToDescription(desc)) attributes.put("description", desc);
    }

    @Override
    public boolean addToDescription(String description) {
        if (desc.equals(description)) return super.addToDescription(description);
        desc = desc + " " + description;
        return super.addToDescription(" " + description);
    }

    public DParameter(String varName, String type, String description, boolean optional) {
        super(TagType.PARAMETER);
        this.varName = varName;
        this.type = type;
        this.optional = optional;
        this.desc = description;
        attributes.put("var", varName);
        attributes.put("type", type);
        attributes.put("optional", optional);
        attributes.put("description", description);
    }

    @Override
    public String toString() {
        return super.toString() +
                ",\"var\":\"" + attributes.get("var") + "\","
                + "\"type\":\"" + attributes.get("type") + "\","
                + "\"optional\":" + attributes.get("optional") + "}";
    }
}

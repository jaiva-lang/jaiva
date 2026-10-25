package com.jaiva.tokenizer.jdoc.tags;

public final class DFrom extends Tag {
    public DFrom(String input) {
        // given an input such as "jaiva version"
        super(TagType.FROM);
        attributes.put("version", input.trim());
    }

    @Override
    public String toString() {
        return super.toString()
                + "\"version\":\"" + attributes.get("version") + "\"}";
    }
}

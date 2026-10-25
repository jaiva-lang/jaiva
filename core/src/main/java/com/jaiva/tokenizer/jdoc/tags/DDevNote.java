package com.jaiva.tokenizer.jdoc.tags;

public final class DDevNote extends Tag {
    public DDevNote(String input) {
        // given an input such as "devstring"
        super(TagType.DEVNOTE);
        if (addToDescription(input)) attributes.put("description", input);
    }

    @Override
    public String toString() {
        return super.toString() + "}";
    }
}

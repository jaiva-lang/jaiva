package com.jaiva.tokenizer.jdoc.tags;

public final class DReturns extends Tag {
    public DReturns(String input) {
        super(TagType.RETURNS);
        // given an input such as "docs"
        if (addToDescription(input)) attributes.put("description", input);
    }

    @Override
    public String toString() {
        return super.toString() + "}";
    }
}

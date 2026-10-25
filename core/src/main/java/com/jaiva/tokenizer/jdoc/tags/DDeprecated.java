package com.jaiva.tokenizer.jdoc.tags;

public final class DDeprecated extends Tag {
    public DDeprecated(String input) {
        // given an input such as "deprString"
        super(TagType.DEPRECATED);
        if (addToDescription(input)) attributes.put("description", input);
    }

    @Override
    public String toString() {
        return super.toString() + "}";
    }
}

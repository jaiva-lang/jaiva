package com.jaiva.tokenizer.jdoc.tags;

/**
 * Generic is a class which only holds a description It isnt a tag but its the comment about a symbol
 * that precedes a tag.
 */
public final class DGeneric extends Tag {
    public DGeneric(String input) {
        super(TagType.GENERIC);
        if (addToDescription(input)) attributes.put("description", input);
    }

    @Override
    public String toString() {
        return super.toString() + "}";
    }
}

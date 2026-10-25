package com.jaiva.tokenizer.jdoc.tags;

/**
 * Empty is class is but a helper class to add on to the description of another tag.
 */
public final class DEmpty extends Tag {
    public DEmpty(String input) {
        super(TagType.EMPTY);
        if (addToDescription(input)) attributes.put("description", input);
    }
}

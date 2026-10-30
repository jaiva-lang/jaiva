package com.jaiva.tokenizer.jdoc.tags;

import com.jaiva.lang.Keywords;

import java.util.ArrayList;
import java.util.Arrays;

public enum TagType {
    PARAMETER("parameter","par","param", "p", "arg", "argument"),
    RETURNS("returns", "ret",  "rets", "r", Keywords.RETURN, "return"),
    DEPENDS("depends", "deps", "dependencies", "depend", "requires"),
    DEVNOTE("devnote", "note", "devn", "dn"),
    DEPRECATED("deprecated", "deprec", "depr"),
    FROM("from", "f", "version", "since", "ver"),
    EXAMPLE("example", "ex", "eg"),
    UNKNOWN(),
    EMPTY(""),
    GENERIC("GENERIC"); // Generic represents just a plain description. Not a tag specifically but its tested for.

    private final ArrayList<String> tags = new ArrayList<>();

    /**
     * Constructor for `TagType` that initializes the tag aliases.
     *
     * @param tag The aliases associated with the tag type.
     */
    TagType(String ...tag) {
        tags.addAll(Arrays.asList(tag));
    }

    /**
     * Retrieves the list of aliases associated with the tag type.
     *
     * @return An `ArrayList` of tag aliases.
     */
    public ArrayList<String> getTag() {
        return tags;
    }


    @Override
    public String toString() {
        return "TagType{" +
                "tags=" + tags +
                '}';
    }
}

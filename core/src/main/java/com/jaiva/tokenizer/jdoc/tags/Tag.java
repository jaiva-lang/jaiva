package com.jaiva.tokenizer.jdoc.tags;

import com.jaiva.errors.JaivaException;
import com.jaiva.errors.TokenizerException;
import com.jaiva.tokenizer.NewJson;
import com.yetnt.utils.builders.ToJSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * The `Tag` class represents a documentation tag with a specific type and associated attributes.
 * It provides functionality to createFunction tags, manipulate their attributes, and handle various tag types.
 */
public sealed class Tag
        implements ToJSON<NewJson, JaivaException>
        permits DDepends, DDeprecated, DDevNote, DEmpty,
        DExample, DFrom, DGeneric, DParameter, DReturns {
    /**
     * A map of attributes associated with the tag, such as description, type, or version.
     */
    public TagType tagType = TagType.UNKNOWN;

    /**
     * A map of attributes associated with the tag, such as description, type, or version.
     */
    public LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();

    /**
     * Default constructor for Tag.
     */
    public Tag() {

    }

    /**
     * Maps a tag and its content to a specific subclass of `Tag` based on the tag type.
     *
     * @param tag The tag type as a string.
     * @param content The content associated with the tag.
     * @return A specific subclass of `Tag` corresponding to the tag type, or `null` if the tag type is unrecognized.
     */
    public static Tag tagToClass(String tag, String content, int lineNumber) throws TokenizerException.MalformedJDocException {
        for (TagType t : TagType.values()) {
            if (t.getTag().contains(tag)) {
                return switch (t) {
                    case PARAMETER -> new DParameter(content, lineNumber);
                    case RETURNS -> new DReturns(content);
                    case DEVNOTE -> new DDevNote(content);
                    case DEPRECATED -> new DDeprecated(content);
                    case FROM -> new DFrom(content);
                    case DEPENDS -> new DDepends(content);
                    case EMPTY -> new DEmpty(content);
                    case EXAMPLE -> new DExample(content);
                    case GENERIC -> new DGeneric(content);
                    default -> null;
                };
            }
        }

        return null;
    }

    /**
     * Constructor for Tag with a specific tag type.
     * If the tag type is DEPRECATED, RETURNS, or PARAMETER,
     * it initializes the description attribute with the first tag of the type.
     * @param tagType the type of the tag, which determines its behavior and attributes
     * @see TagType
     */
    public Tag(TagType tagType) {
        this.tagType = tagType;
        if (!Arrays.asList(TagType.DEPENDS, TagType.FROM, TagType.EXAMPLE).contains(tagType))
                attributes.put("description", null);
        attributes.put("tagType", tagType == TagType.GENERIC ? tagType.name() : tagType.name().toLowerCase());
    }

    @Override
    public String toString() {
        return "{" +
                "\"tagType\":\"" + tagType.getTag().getFirst() + "\"," + (attributes.containsKey("description")
                ? "\"description\":\"" +
                attributes.get("description").toString().replace("\"", "\\\"").replace("\n", "\\n") + "\""
                : "");
    }

    /**
     * Adds a description to the tag if it exists.
     * This is used to add more information to the tag.
     * @param description the description to add
     * @return false if the description was added, true if the tag does not have a description attribute
     */
    public boolean addToDescription(String description) {
        if (attributes.containsKey("description")) {
            attributes.compute("description", (k, currentDescription) -> (currentDescription == null ? "" : currentDescription + " ") + description);
            return false;
        }
        // if it does not exist, it returns true.
        return true;
    }

    public NewJson toJson() {
        return new NewJson(attributes);
    }
}

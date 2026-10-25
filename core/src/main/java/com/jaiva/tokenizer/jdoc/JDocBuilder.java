package com.jaiva.tokenizer.jdoc;

import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.tokenizer.jdoc.tags.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A builder class for creating {@link JDoc} objects.
 * This class provides a fluent API to add various Javadoc-like tags to a JDoc instance.
 */
public class JDocBuilder {
    public ArrayList<Tag> tags = new ArrayList<>();

    /**
     * Constructs a new JDocBuilder.
     */
    JDocBuilder() {

    }

    /**
     * Adds a generic description tag to the JDoc.
     *
     * @param str The description string.
     * @return This JDocBuilder instance for method chaining.
     */
    public JDocBuilder addDesc(String str) {
        tags.add(new DGeneric(str));
        return this;
    }

    /**
     * Adds a parameter tag to the JDoc.
     *
     * @param name The name of the parameter.
     * @param type The type of the parameter.
     * @param description A description of the parameter.
     * @param optional True if the parameter is optional, false otherwise.
     * @return This JDocBuilder instance for method chaining.
     */
    public JDocBuilder addParam(String name, String type, String description, boolean optional) {
        tags.add(new DParameter(name, type, description, optional));
        return this;
    }

    /**
     * Adds a returns tag to the JDoc.
     *
     * @param description A description of the return value.
     * @return This JDocBuilder instance for method chaining.
     */
    public JDocBuilder addReturns(String description) {
        tags.add(new DReturns(description));
        return this;
    }

    /**
     * Adds a developer note tag to the JDoc.
     *
     * @param note The developer note string.
     * @return This JDocBuilder instance for method chaining.
     */
    public JDocBuilder addNote(String note) {
        tags.add(new DDevNote(note));
        return this;
    }

    /**
     * Adds one or more dependency tags to the JDoc.
     *
     * @param dependencies An array of dependency strings.
     * @return This JDocBuilder instance for method chaining.
     */
    public JDocBuilder addDependencies(String... dependencies) {
        tags.add(new DDepends(dependencies));
        return this;
    }

    /**
     * Adds a "since version" tag to the JDoc.
     *
     * @param vers The version string from which this element is available.
     * @return This JDocBuilder instance for method chaining.
     */
    public JDocBuilder sinceVersion(String vers) {
        tags.add(new DFrom(vers));
        return this;
    }

    /**
     * Adds a deprecated tag to the JDoc.
     *
     * @param reason The reason for deprecation.
     * @return This JDocBuilder instance for method chaining.
     */
    public JDocBuilder markDeprecated(String reason) {
        tags.add(new DDeprecated(reason));
        return this;
    }

    /**
     * Adds an example tag to the JDoc.
     *
     * @param example The example string, potentially containing multiple lines.
     * @return This JDocBuilder instance for method chaining.
     */
    public JDocBuilder addExample(String example) {
        tags.add(new DExample(new ArrayList<>(
                Arrays.stream(example.split("\n")).toList()
        )));
        return this;
    }

    /**
     * Adds an example tag to the JDoc.
     *
     * @param exampleLines An ArrayList of strings representing lines of the example.
     * @return This JDocBuilder instance for method chaining.
     */
    public JDocBuilder addExample(ArrayList<String> exampleLines) {
        tags.add(new DExample(exampleLines));
        return this;
    }

    /**
     * Builds and returns a new {@link JDoc} object based on the tags added to this builder.
     *
     * @return A new JDoc instance.
     */
    public JDoc build() {
        return new JDoc(tags);
    }

    public JDocBuilder addParams(Arguments arguments) {
        for (Argument arg : arguments.getArguments()) {
            addParam(
                    arg.name(),
                    arg.argumentType().getType(),
                    arg.getDescription(),
                    arg.isOptional()
            );
        }
        return this;
    }
}

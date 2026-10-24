package com.jaiva.interpreter.runtime;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class ResourceLoader {

    /**
     * Load a resource as an InputStream (works in both IDE and JAR hopefully)
     */
    @NotNull
    public static InputStream getResourceAsStream(String path) {
        InputStream stream = ResourceLoader.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new RuntimeException("Resource not found: " + path);
        }
        return stream;
    }

    /**
     * Convert InputStream to a single String with newlines preserved
     */
    @NotNull
    public static String streamToString(InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            StringBuilder content = new StringBuilder();
            String line;
            boolean firstLine = true;

            while ((line = bufferedReader.readLine()) != null) {
                if (!firstLine) {
                    content.append("\n");
                }
                content.append(line);
                firstLine = false;
            }

            return content.toString();
        }
    }

    /**
     * One-stop method: get resource as string with preserved newlines
     */
    @NotNull
    public static String getResourceAsString(String path) throws IOException {
        InputStream stream = getResourceAsStream(path);
        return streamToString(stream);
    }
}
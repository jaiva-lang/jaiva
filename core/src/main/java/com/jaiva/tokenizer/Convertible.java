package com.jaiva.tokenizer;

import com.jaiva.errors.JaivaException;

/**
 * Let's go!
 * <p></p>
 * - Dababy.
 */
public interface Convertible {
    /**
     * Converts the implementing object to a JSON string representation.
     *
     * @return A JSON string representing the object.
     * @throws JaivaException If an error occurs during the JSON conversion process.
     */
    NewJson toJson() throws JaivaException;
}

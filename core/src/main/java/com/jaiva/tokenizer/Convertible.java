package com.jaiva.tokenizer;

import com.jaiva.errors.JaivaException;
import com.yetnt.utils.builders.ToJSON;

/**
 * Let's go!
 * <p></p>
 * - Dababy.
 */
public interface Convertible extends ToJSON<NewJson, JaivaException> {
    /**
     * Converts the implementing object to a JSON string representation.
     *
     * @return A JSON string representing the object.
     * @throws JaivaException If an error occurs during the JSON conversion process.
     */
    @Override
    NewJson toJson() throws JaivaException;
}

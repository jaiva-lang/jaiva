package com.jaiva.tokenizer.tokens;

/**
 * Represents a reference type in the Jaiva language.
 * This is usually a function call or a variable reference which can have a length or be spread into an array.
 */
public interface TReference extends TAtomicValue {
    /**
     * Indicates whether the reference has a length called on it via the ~ operator.
     * @return A boolean indicating if length is called.
     */
    boolean getLength();

    /**
     * Indicates whether the reference is being spread into an array via the ::: operator.
     * @return A boolean indicating if the reference is spread into an array.
     */
    boolean getSpreadArr();
    int getLineageIndexAmount();
}

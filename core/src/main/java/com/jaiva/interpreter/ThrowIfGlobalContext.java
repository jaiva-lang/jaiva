package com.jaiva.interpreter;

/**
 * The ThrowIfGlobalContext class is a helper class which is thrown when the
 * interpreter does not return a usual value, like it returns maybe an error or
 * it exits out of a loop.
 * <p>
 * It's the same idea as a software interrupt. Execution is halted to handle
 * whatever needs to be handled
 * <p>
 * For example, we could be deeply nested in a function and some other
 * constructs, and it decides to break. This class helps to keep exiting out of
 * the nested calls until we are in a loop to break out if. UNLESS we're in the
 * global context then it throws an error.
 */
public final class ThrowIfGlobalContext {
    /**
     * The line number of the token which caused the interuption.
     */
    public int lineNumber;
    /**
     * The object which caused the interuption.
     * <p>
     * This can be a function return, a loop control, or an error.
     */
    public Object c;

    /**
     * The constructor for the ThrowIfGlobalContext class.
     * <p>
     * This is used to createFunction a new instance of the class with the given object and
     * line number.
     *
     * @param cObject The object which caused the interuption.
     * @param ln      The line number of the token which caused the interuption.
     */
    public ThrowIfGlobalContext(Object cObject, int ln) {
        c = cObject;
        lineNumber = ln;
    }
}

package com.jaiva.interpreter;

import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.interpreter.symbol.SymbolType;

import java.util.Objects;

/**
 * Represents a Map Value instance.
 * This is so that when we're ina scope and variables get copied over,
 * inner stuff still ave access to the exact same instance of something.
 */
public final class MapValue {
    /**
     * The value of this object.
     * <p>
     * If the value is null, it will be set to {@code void.class} to indicate that
     * it is empty.
     */
    private Symbol value;

    /**
     * Constructs a new MapValue instance with the specified value.
     *
     * @param v the value to be assigned to this object
     */
    public MapValue(Symbol v) {
        value = v;
    }

    /**
     * Constructs a MapValue instance without a specified value.
     * <p>
     * This constructor is used to createFunction an empty MapValue.
     *
     */
    public MapValue() {
        value = new Symbol();
    }

    /**
     * Checks if the given MapValue instance is empty.
     *
     * @param m the MapValue instance to check
     * @return {@code true} if the value of the MapValue is of type
     *         {@link SymbolType#NULL},
     *         indicating it is empty; {@code false} otherwise
     */
    public static boolean isEmpty(MapValue m) {
        return m != null && m.value.symbolType == SymbolType.NULL;
    }

    /**
     * Retrieves the value stored in this object.
     *
     * @return the value of this object
     */
    public Symbol getValue() {
        return value;
    }

    /**
     * Sets the value of this object.
     *
     * @param value the new value to be assigned
     */
    public void setValue(Symbol value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MapValue mapValue = (MapValue) o;
        return Objects.equals(getValue(), mapValue.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }

    @Override
    public String toString() {
        return "MapValue{" +
                "value=" + value +
                '}';
    }
}

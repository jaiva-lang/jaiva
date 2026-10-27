package com.jaiva.interpreter.symbol;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.libBuilders.var.VariableBuilder;
import com.jaiva.tokenizer.tokens.specific.TokenDefault;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * BaseVariable class is a base class for all variables in Jaiva.
 */
public class BaseVariable extends Symbol {
    /**
     * The scalar value of the variable.
     * <p>
     * This value is used when the variable is a scalar type.
     */
    private Object scalar;
    // we just give access to the array because why not.
    /**
     * The array value of the variable.
     * <p>
     * This value is used when the variable is an array type.
     */
    private ArrayList<Object> array = new ArrayList<>();
    /**
     * The type of the variable.
     * <p>
     * This value is used to determine if the variable is a scalar or an array type.
     */
    public VariableType variableType;
    private boolean preferArray = false;

    /**
     * Update the variable type.
     * 
     * @param newType The new type of the variable.
     */
    public void updateVariableType(VariableType newType) {
        if (variableType == VariableType.UNKNOWN) {
            variableType = newType;
        } else if ((variableType == VariableType.SCALAR && newType == VariableType.ARRAY)
                || (variableType == VariableType.ARRAY && newType == VariableType.SCALAR)) {
            variableType = VariableType.A_FUCKING_AMALGAMATION;
        }
    }

    @Override
    public String toDebugString() {
        return scalar != null ? scalar.toString() : array.toString();
    }

    /**
     * Constructs a new BaseVariable instance with the specified name, token, and
     * scalar value.
     *
     * @param name   the name of the variable
     * @param t      the token associated with the variable
     * @param scalar the scalar value of the variable
     */
    public BaseVariable(String name, TokenDefault<?> t, Object scalar) {
        super(name, SymbolType.VARIABLE, t);
        this.scalar = scalar;
        variableType = VariableType.SCALAR;
    }

    /**
     * Constructs a new BaseVariable instance with the specified name, token, and
     * array value.
     *
     * @param name the name of the variable
     * @param t    the token associated with the variable
     * @param arr  the array value of the variable
     */
    public BaseVariable(String name, TokenDefault<?> t, ArrayList<Object> arr) {
        super(name, SymbolType.VARIABLE, t);
        this.array.addAll(arr);
        variableType = VariableType.ARRAY;
    }

    public BaseVariable(VariableBuilder builder) {
        super(builder.getVarName(), SymbolType.VARIABLE, builder.toToken());
        if (builder.getType() ==  VariableType.SCALAR) {
            variableType = VariableType.SCALAR;
            this.scalar = builder.getValue();
        } else {
            variableType = VariableType.ARRAY;
            this.array.addAll((Collection<?>) builder.getValue());
        }
    }

    /**
     * Constructs a new BaseVariable instance with the specified name and token.
     *
     * @param name the name of the variable
     * @param t    the token associated with the variable
     */
    public BaseVariable(String name, TokenDefault<?> t) {
        super(name, SymbolType.VARIABLE, t);
        variableType = VariableType.UNKNOWN;
    }

    /**
     * Constructs a new BaseVariable instance with the specified name.
     *
     * @param name the name of the variable
     */
    public BaseVariable(String name) {
        super(name, SymbolType.VARIABLE);
        variableType = VariableType.UNKNOWN;
    }

    public void setPreferArray(boolean preferArray) {
        this.preferArray = preferArray;
    }

    public boolean isPreferArray() {
        return preferArray;
    }

    /**
     * Sets the array value of the variable.
     * 
     * @param t The array to set this variable to.
     * @param scope Context Trace
     * @throws InterpreterException.FrozenSymbolException When trying to modify the variable while it's frozen.
     */
    public void a_set(ArrayList<Object> t, Scope scope) throws InterpreterException.FrozenSymbolException {
        if (isFrozen)
            throw new InterpreterException.FrozenSymbolException(scope, this);
        array = t;
    }

    public void a_objSet(Object o, Scope scope) throws InterpreterException {
        if (!(o instanceof ArrayList<?> t))
            throw new InterpreterException.WtfAreYouDoingException(
                    scope, "idk how you got this error bro" +
                    ". Essentially, you managed to make the interpreter pass" +
                    " some random object instead of an array because it thought" +
                    " that the nonsense you were doing in the colonize loop" +
                    " was array related. But it wasn't. And now we are at a stand" +
                    " still.", this.token.lineNumber
            );
        a_set(new ArrayList<>(t), scope);
    }


    /**
     * Sets the array value of the variable.
     * <p>
     *     GithubBlockQuote: This is intended for {@code FileApi.VThis} which doesn't have access to the current scope.
     *     Use the other override or else anything can just be edited.
     * </p>
     *
     * @param t The array to set this variable to.
     */
    public void a_unsafeSet(ArrayList<Object> t) {
        array = t;
    }

    /**
     * Adds an array to the array (addAll())
     * 
     * @param a The array to add onto.
     * @param scope Context Trace
     * @throws InterpreterException.FrozenSymbolException When trying to modify the variable while it's frozen.
     */
    public void a_addAll(List<String> a, Scope scope) throws InterpreterException.FrozenSymbolException {
        if (isFrozen)
            throw new InterpreterException.FrozenSymbolException(scope, this);
        array.addAll(a);
    }

    /**
     * Appends an element to the array.
     * 
     * @param a The object to add to the array.
     * @throws InterpreterException.FrozenSymbolException When trying to modify the variable while it's frozen.
     */
    public void a_add(Object a, Scope scope) throws InterpreterException.FrozenSymbolException {
        if (isFrozen)
            throw new InterpreterException.FrozenSymbolException(scope, this);
        array.add(a);
    }

    /**
     * Gets the array value of the variable.
     * 
     * @return The array this variable holds.
     */
    public ArrayList<Object> a_getAll() {
        return array;
    }

    /**
     * Gets an element from the array.
     * 
     * @param i Index
     * @return Element at the given index
     */
    public Object a_get(int i) {
        return array.get(i);
    }

    /**
     * Get array size
     * 
     * @return The size of the array.
     */
    public int a_size() {
        return array.size();
    }

    /**
     * Set scalar value
     * 
     * @param value The new scalar value
     * @throws InterpreterException.FrozenSymbolException When trying to modify the variable while it's frozen.
     */
    public void s_set(Object value, Scope scope) throws InterpreterException.FrozenSymbolException {
        if (isFrozen)
            throw new InterpreterException.FrozenSymbolException(scope, this);
        this.scalar = value;
    }

    /**
     * get scalar value
     * 
     * @return The scalar value.
     */
    public Object s_get() {
        return scalar;
    }

    /**
     * This creates a user-defined variable.
     * <p>
     * If this variable only has a scalar value, createFunction an arraylist with only that
     * 1 value.
     * 
     * @param name Name of the variable.
     * @param t    The TVarRef token.
     * @param arr  The arraylist.
     * @return A User Defined Variable.
     */
    public static DefinedVariable create(String name, TokenDefault<?> t, ArrayList<Object> arr, boolean setArray) {
        if (arr.size() == 1 && !setArray)
            return new DefinedVariable(name, t, arr.get(0));
        return new DefinedVariable(name, t, arr);
    }


    /**
     * A class that represents a user-defined variable.
     */
    public static class DefinedVariable extends BaseVariable {
        /**
         * Constructs a new DefinedVariable instance with the specified name, token,
         * and scalar value.
         *
         * @param name   the name of the variable
         * @param t      the token associated with the variable
         * @param scalar the scalar value of the variable
         */
        DefinedVariable(String name, TokenDefault<?> t, Object scalar) {
            super(name, t, scalar);
        }

        /**
         * Constructs a new DefinedVariable instance with the specified name, token,
         * and array value.
         *
         * @param name the name of the variable
         * @param t    the token associated with the variable
         * @param arr  the array value of the variable
         */
        DefinedVariable(String name, TokenDefault<?> t, ArrayList<Object> arr) {
            super(name, t, arr);
        }
    }

    /**
     * Enum that represents the type of variable.
     */
    public enum VariableType {
        /**
         * Scalar type.
         */
        SCALAR,
        /**
         * Array type.
         */
        ARRAY,
        /**
         * Amalgamation of scalar and array types.
         */
        A_FUCKING_AMALGAMATION,
        /**
         * Unknown type.
         */
        UNKNOWN
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((scalar == null) ? 0 : scalar.hashCode());
        result = prime * result + ((array == null) ? 0 : array.hashCode());
        result = prime * result + ((variableType == null) ? 0 : variableType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseVariable other = (BaseVariable) obj;
        if (scalar == null) {
            if (other.scalar != null)
                return false;
        } else if (!scalar.equals(other.scalar))
            return false;
        if (array == null) {
            if (other.array != null)
                return false;
        } else if (!array.equals(other.array))
            return false;
        return variableType == other.variableType;
    }

    @Override
    public String toString() {
        return "BaseVariable{" +
                "scalar=" + scalar +
                ", array=" + array +
                ", variableType=" + variableType +
                '}';
    }
}
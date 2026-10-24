package com.jaiva.interpreter.symbol;

import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.*;
import com.jaiva.lang.EscapeSequence;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A Function that is defined by the user at runtime.
 */
public sealed class DefinedFunction extends BaseFunction permits Lambda {

    DefinedFunction(String name, TFunction token) {
        super(name, token);
    }

    @Override
    public Object call(CallProperties callProperties)
            throws Exception {
        // tFuncCall contains the the token, we pass it for any extra checks we need to
        // do later.
        // params contains the input for the function.
        // this DefinedFunction, contains a TFunction where we need to check the params
        // so we can make name value pairs.
        String[] paramNames = ((TFunction) this.token).args;
        Vfs newVfs = callProperties.scope().getVfs().clone();
        if (((TFunction) this.token).varArgs) {
            ArrayList<Object> varArgsArr = new ArrayList<>();
            // below if because a single null param is, not supposed to be even possible, but here we are.
            if (!callProperties.params().isEmpty())
                for (Object o : callProperties.params()) {
                    // Parse each param
                    if (o instanceof String)
                        o = EscapeSequence.fromEscape((String) o, callProperties.getLineNumber());
                    varArgsArr.add(Primitives.toPrimitive(o, false, callProperties.config(), callProperties.scope()));
                }
            newVfs.put(paramNames[0], new BaseVariable(paramNames[0],
                    new TArrayVar(paramNames[0], varArgsArr, callProperties.getLineNumber()),
                    varArgsArr));
        } else {
            checkParams(callProperties.toCheckParams());
            for (int i = 0; i < paramNames.length; i++) {
                String name = paramNames[i];
                Object value;
                try {
                    value = callProperties.params().get(i);
                } catch (IndexOutOfBoundsException e) {
                    // optional as we checked params above, so set it to TVoidValue
                    value = Token.voidValue(callProperties.getLineNumber());
                }
                if (value instanceof String)
                    value = EscapeSequence.fromEscape((String) value, callProperties.getLineNumber());
                Object wrappedValue = Token.voidValue(callProperties.getLineNumber());


                if (name.startsWith("F~")) {
                    // value is definitely a TVarRef, so look for the function in vfs, if not found
                    // throw an error
                    // if found, createFunction aq copy of that MapValue, and name it to instead this new
                    // name and add to the vfs.
                    if (value instanceof TLambda) wrappedValue = Primitives.toPrimitive(value, false, callProperties.config(), callProperties.scope());
                    else if (value instanceof BaseFunction f) wrappedValue = f;
                    if ((value instanceof Token<?> && ((Token<?>) value).value() instanceof TVarRef tVarRef)) {
                        MapValue v = callProperties.scope().getVfs().get(tVarRef.varName);
                        if (v == null)
                            throw new InterpreterException.UnknownVariableException(callProperties.scope(), tVarRef);
                        if (!(v.getValue() instanceof BaseFunction))
                            throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), v.getValue(), BaseFunction.class,
                                    tVarRef.lineNumber);
                        wrappedValue = v.getValue();
                    }


                }
                else if (name.startsWith("V~") || (value instanceof Token<?> && ((Token<?>) value).value() instanceof TVarRef)) {
                    // value is definitely a TVarRef, so look for the variable in vfs, if not found
                    // throw an error
                    // if found, createFunction aq copy of that MapValue, and name it to instead this new
                    // name and add to the vfs.
                    Object o = Primitives.toPrimitive(value, false, callProperties.config(), callProperties.scope());
                    // wrappedValue = new BaseVariable(name, tFuncCall, o);
                    wrappedValue = createScalarOrArr(name, o, callProperties.tFuncCall());
                } else if (Primitives.isPrimitive(value)) {
                    // primitivers ong
                    wrappedValue = createScalarOrArr(name, value, callProperties.tFuncCall());

                } else {
                    // cacthes nested calls, operations and others
                    Object o = Primitives.toPrimitive(value, false, callProperties.config(), callProperties.scope());
                    if (o instanceof BaseFunction l)
                        wrappedValue = l;
                    else
                        wrappedValue = BaseVariable.create(name,
                                new TUnknownScalar(name, o, callProperties.getLineNumber()),
                                o instanceof ArrayList ? (ArrayList) o : new ArrayList<>(Collections.singletonList(o)), false);
                }
                newVfs.put(name.replace("F~", "").replace("V~", ""), (Symbol) wrappedValue);
            }
        }
        Object t = Interpreter.interpret(((TFunction) this.token).body.lines,
                new Scope(Context.FUNCTION, token, callProperties.scope(), newVfs), callProperties.config());
        if (t instanceof ThrowIfGlobalContext) {
            return ((ThrowIfGlobalContext) t).c;
        } else {
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    /**
     * Creates a BaseVariable that is either a scalar or an array based on the value type.
     *
     * @param name      The name of the variable.
     * @param value     The value of the variable, which can be a scalar or an ArrayList.
     * @param tFuncCall The function call token for line number reference.
     * @return A BaseVariable instance representing the scalar or array.
     * @implNote This depends on how BaseVariable.create works. As of now
     * if it's an array, it's passed as is and marked as true so it's an array.
     * otherwise, it's parsed as a singleton list, with it's boolean marked as false.
     * therefore allowing BaseVariable to extract the single value and label the variable as scalar.
     */
    @NotNull
    @Contract("_, _, _ -> new")
    private BaseVariable createScalarOrArr(String name, Object value, TFuncCall tFuncCall) throws JaivaException {
        return BaseVariable.create(name,
                value instanceof ArrayList ? new TArrayVar(name, (ArrayList) value, tFuncCall.lineNumber)
                        : new TUnknownScalar(name, value, tFuncCall.lineNumber),
                value instanceof ArrayList ? (ArrayList) value : new ArrayList<>(Collections.singletonList(value)),
                value instanceof ArrayList);
    }
}

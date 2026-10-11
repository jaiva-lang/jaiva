package com.jaiva.interpreter.symbol;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.lang.EscapeSequence;
import com.jaiva.tokenizer.tokens.TReference;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;
import com.jaiva.tokenizer.tokens.specific.TFunction;
import com.jaiva.tokenizer.tokens.specific.TVoidValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * BaseFunction class is a base class for all functions in Jaiva.
 */
public class BaseFunction extends Symbol {

    private boolean evaluateParamsEarly = true;

    public boolean isEvaluateParamsEarly() {
        return evaluateParamsEarly;
    }

    protected void setEvaluateParamsEarly(boolean evaluateParamsEarly) {
        this.evaluateParamsEarly = evaluateParamsEarly;
    }

    /**
     * Constructs a new BaseFunction instance with the specified name and token.
     * 
     * @param name  the name of the function
     * @param token the token associated with the function
     */
    public BaseFunction(String name, TFunction token) {
        super(name, SymbolType.FUNCTION, token);
    }

    /**
     * Constructs a new BaseFunction instance with da specified function builder
     * @param functionBuilder da builder.
     */
    public BaseFunction(FunctionBuilder functionBuilder) {
        super(functionBuilder.getName(), SymbolType.FUNCTION, functionBuilder.toToken());
    }

    @Override
    public String toString() {
        return ((TFunction)token).toDefinitionString();
    }

    @Override
    public String toDebugString() {
        return this.toString();
    }

    /**
     * Calls the function with the given call properties.
     * It's generally required that libraries override this method to enable function functionality.
     *
     * @param callProperties The call properties
     * @return The return value of the function, if not overridden it will return
     *      {@link TVoidValue} (idk)
     * @throws Exception If an error occurs during the function call.
     */
    public Object call(CallProperties callProperties) throws Exception {
        return Token.voidValue(callProperties.tFuncCall().lineNumber);
    }

    /**
     * Original method
     * @deprecated Use {{@link #call(CallProperties)} instead}
     * @param tFuncCall func call
     * @param params params
     * @param config config
     * @param scope scope
     * @return something
     * @throws Exception if anthing wrong lol
     */
    public Object call(TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
        return call(new CallProperties(tFuncCall, params, config, scope));
    }

    public Object call(ArrayList<Object> params,
                       IConfig<Object> config, Scope scope, TFuncCall originalCall) throws Exception {
        // inject stuff into scope
        // TODO this is most definitely unsafe. only called when a library function needs to call a function, sorta like composing in java using andThen()
        ArrayList<Object> tokens = new ArrayList<>();

        params.forEach(p -> {
            switch (p) {
                case Number t -> tokens.add(t);
                case String t2 -> tokens.add(t2);
                case Boolean t3 -> tokens.add(t3);
                case ArrayList<?> t4 -> tokens.add(t4);
                default -> tokens.add(p); // TODO: pray.
            }
        });

        return call(
                new CallProperties(new TFuncCall(
                        this.name,
                        tokens,
                        originalCall.lineNumber, false, false
                ), params, config, scope));
    }

    /**
     * Resolves the parameters for a function call, handling spread operators.
     *
     * @param f          The function being called.
     * @param tFuncCall  The function call token containing the arguments.
     * @param config     The interpreter configuration.
     * @param scope      The current scope for error reporting.
     * @return A list of resolved parameters for the function call.
     * @throws Exception If an error occurs during parameter resolution.
     */
    public static ArrayList<Object> resolveParameters(BaseFunction f, TFuncCall tFuncCall, IConfig<Object> config, Scope scope) throws Exception {
        ArrayList<Object> newTokenArgs = new ArrayList<>();
        // Go through each arg.

        // fast path, filter out non spread args, if new size 0, then return.
        boolean hasSpread = tFuncCall.args.stream().map(
                arg -> {
                    if (arg instanceof Token<?>(Object value)) {
                        arg = value;
                    }
                    return arg;
                }
        ).anyMatch(
                arg -> arg instanceof TReference referenceToken && referenceToken.getSpreadArr()
        );
        if (!hasSpread)
            return tFuncCall.args;
        for (Object arg : tFuncCall.args) {
            if (arg instanceof Token<?>(Object value)) {
                arg = value;
            }
            if (!((TFunction)f.token).varArgs && newTokenArgs.size() > ((TFunction)f.token).isArgOptional.size())  {
                // We could hard fail, or be funnier and just cut off the rest of the data.
                // Cut off the rest of the data.
                while (newTokenArgs.size() > ((TFunction)f.token).isArgOptional.size())
                    newTokenArgs.removeLast();
                break;
            }
            if (arg instanceof TReference referenceToken && referenceToken.getSpreadArr()) {
                Object evaledToken = Primitives.toPrimitive(((TokenDefault<?>)referenceToken).toToken(), false, config, scope);
                if (evaledToken instanceof ArrayList<?> arr) {
                    newTokenArgs.addAll(arr);
                } else if (evaledToken instanceof String string) {
                    string = EscapeSequence.toEscape(string);
                    newTokenArgs.addAll(Arrays.asList(string.split("")));
                } else {
                    throw new InterpreterException.WtfAreYouDoingException(scope,
                            "Spread operator can only be used on arrays or strings.",
                            tFuncCall.lineNumber);
                }
            } else {
                newTokenArgs.add(arg);
            }
        }

        return newTokenArgs;
    }

    /**
     * Checks if the parameters passed to a function call match the function's definition.
     * This method validates the number of arguments and their optionality.
     *
     * @param tFuncCall The function call token containing the arguments.
     * @param scope     The current scope for error reporting.
     * @throws InterpreterException If there's a mismatch in parameters (e.g., missing required arguments).
     */
    protected void checkParams(TFuncCall tFuncCall, Scope scope) throws Exception {
        TFunction tFunc = (TFunction) this.token;

        // this if sanitizes tFuncCall, as if it has 1 singular entry and that entry is
        // null, we remove it so we have clear args.
        if (tFuncCall.args.size() == 1 && tFuncCall.args.getFirst() == null) {
            tFuncCall.args.clear();
        }

        // Check if this case
        if (tFunc.args.length == 1 && (tFunc.args[0] == null || tFunc.args[0].isEmpty())) {
            return;
        }

        // first check if we jsut dont have args.
        if (Collections.frequency(tFunc.isArgOptional, true) == tFunc.isArgOptional.size() && tFuncCall.args.isEmpty())
            return;
        if (tFuncCall.args.isEmpty()) {
            throw new InterpreterException.FunctionParametersException(scope, this,
                    Integer.toString(1),
                    tFuncCall.lineNumber);
        }
        for (int i = 0; i < tFunc.isArgOptional.size(); i++) {
            boolean isOptional = (boolean) tFunc.isArgOptional.get(i);
            // check if the current param was given an input of TVoidValue
            // if so, error as we dont want them to pass in idk to a paramter thats not
            // optional
            if (tFuncCall.args.size() != i) {
                if (tFuncCall.args.get(i) instanceof TVoidValue && !isOptional)
                    throw new InterpreterException.FunctionParametersException(scope, this, Integer.toString(i + 1),
                            tFuncCall.lineNumber);
            } else {
                // the function call has less arguments than required by the function.
                // check if all the remaining arguments are defined as optional.
                for (int j = i; j < tFunc.isArgOptional.size(); j++) {
                    if (!(boolean) tFunc.isArgOptional.get(j)) {
                        throw new InterpreterException.FunctionParametersException(scope, this,
                                Integer.toString(j + 1),
                                tFuncCall.lineNumber);
                    }
                }

                // if we made it here, we've checked everything, so we can break out the outer
                // loop
                break;
            }
        }
    }


    @Override
    public Symbol clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Creates a new user-defined function with the specified name and token.
     * 
     * @param name  Function name
     * @param token Function token
     * @return A User Defined Function.
     */
    public static DefinedFunction createFunction(String name, TFunction token) {
        return new DefinedFunction(name, token);
    }

    /**
     * Creates a new user-defined lambda
     * @param name The lambda name, not really needed
     * @param token The lambda token
     * @param scope It's scope
     * @return A Lambda
     */
    public static Lambda createLambda(String name, TFunction token, Scope scope) {return new Lambda(name, token, scope);}

}
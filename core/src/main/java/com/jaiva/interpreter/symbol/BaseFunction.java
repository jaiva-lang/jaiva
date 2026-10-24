package com.jaiva.interpreter.symbol;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Interpreter;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * BaseFunction class is, the function. It is what Jaiva classifies as a callable entity.
 * <p>
 *     Instances of {@link BaseFunction} can be passed around in {@link Primitives}
 *     as if they are normal values. (higher order functions.)
 * </p>
 * <p>
 *     A user defined function, is a {@link DefinedFunction}, where as a user defined
 *     lambda is a further specialisation, being a {@link Lambda}. But they are all functions
 *     nonetheless. (Except for the fact that {@link Lambda} stores it's creation scope).
 * </p>
 * @see #call(CallProperties)
 * @see DefinedFunction
 * @see Lambda
 * @see Symbol
 * @see Interpreter
 * @see TFunction
 * @see TFuncCall
 * @see Primitives
 * @author Lehlogonolo Poole
 */
public class BaseFunction extends Symbol {

    /**
     * Whether to evaluate parameters early or delegate to the function itself's job.
     */
    private boolean evaluateParamsEarly = true;

    /**
     * Constructs a new BaseFunction instance with the specified name and token.
     * 
     * @param name  the name of the function
     * @param token the token associated with the function
     */
    public BaseFunction(String name, TFunction token) {
        super(name, SymbolType.FUNCTION, token);
    }

    BaseFunction(String name, TFunction token, String lib) {
        super(name, SymbolType.FUNCTION, token);
        token.setFileOrigin(lib);
    }

    /**
     * Constructs a new BaseFunction instance with da specified function builder
     * @param functionBuilder da builder.
     */
    public BaseFunction(@NotNull FunctionBuilder functionBuilder) {
        this(functionBuilder.getName(), functionBuilder.toToken(), functionBuilder.getLib());
    }

    /**
     * Prints the function definition string from the token
     * @return The definition string
     */
    @Override
    public final String toString() {
        return ((TFunction)token).toDefinitionString();
    }

    /**
     * dbg str
     * @return dbg str
     */
    @Override
    public final String toDebugString() {
        return this.toString();
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
    @Deprecated(since = "6.2.0")
    public Object call(TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
        return call(new CallProperties(tFuncCall, params, config, scope));
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
        return Token.voidValue(callProperties.getLineNumber());
    }

    public final Object call(@NotNull ArrayList<Object> params,
                       IConfig<Object> config, Scope scope, @NotNull TFuncCall originalCall) throws Exception {
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
     * Whether parameters will be evaluated to primitive values before the function
     * is called. If you need to override this behaviour, call {@link #evaluateParamsLater()}
     * in the function constructor.
     * @return true if so (Default). False otherwise
     */
    public final boolean isEvaluateParamsEarly() {
        return evaluateParamsEarly;
    }

    /**
     * Sets this function to be responsible for parameter validation.
     * @implNote <p>
     *     What this means is, the given {@link CallProperties#params()}
     *     may either contain:
     *     <ul>
     *         <li>A primitive value</li>
     *         <li>A {@link Token}</li>
     *         <li>
     *             A mix of them (This usually only happens when the user function call
     *             has an argument which has been spread into the function call
     *             arguments. In that case the spreaded arguments arrive already parsed
     *             where as other arguments may have not been aprsed.)
     *         </li>
     *     </ul>
     *     By calling this, it is up to the function to
     *     call the following method on the parameter of choice to evaluate it.
     *     <pre>{@code
     *     Primitives.toPrimitive(params.getFirst(), false, config, scope);
     *     }</pre>
     * </p>
     * This usually only comes in helpful for functions which are more reflective
     * over the {@link TFuncCall} token then actual runtime parsing. in that case
     * it's better to call this.
     * @implSpec This is to be called in a function implementor's constructor.
     * @see Primitives#toPrimitive(Object, boolean, IConfig, Scope)
     */
    protected final void evaluateParamsLater() {
        this.evaluateParamsEarly = false;
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
    @NotNull
    @Contract(pure = true)
    public static ArrayList<Object> resolveSpreadOperator(BaseFunction f, @NotNull TFuncCall tFuncCall, IConfig<Object> config, Scope scope) throws Exception {
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

    public record CheckParams(
            TFuncCall tFuncCall,
            Scope scope,
            ArrayList<Object> params
    ) {}

    /**
     * Checks if the parameters passed to a function call match the function's definition.
     * This method validates the number of arguments and their optionality.
     *
     * @param checkParams The parameters object.
     * @throws InterpreterException If there's a mismatch in parameters (e.g., missing required arguments).
     */
    protected void checkParams(@NotNull CheckParams checkParams) throws InterpreterException {
        TFuncCall tFuncCall = checkParams.tFuncCall;
        ArrayList<Object> params =  checkParams.params;
        Scope scope = checkParams.scope;
        TFunction tFunc = (TFunction) this.token;

        // this if sanitizes tFuncCall, as if it has 1 singular entry and that entry is
        // null, we remove it so we have clear args.
        if (tFuncCall.args.size() == 1 && tFuncCall.args.getFirst() == null) {
            tFuncCall.args.clear();
            // if it happened to params too, clear it.
            if (params.size() == 1 && params.getFirst() == null)
                params.clear();
        }

        // Check if this case
        if (tFunc.args.length == 1 && (tFunc.args[0] == null || tFunc.args[0].isEmpty())) {
            return;
        }

        // first check if we jsut dont have args.
        if (Collections.frequency(tFunc.isArgOptional, true) == tFunc.isArgOptional.size() && params.isEmpty())
            return;
        if (params.isEmpty()) {
            throw new InterpreterException.FunctionParametersException(scope, this,
                    Integer.toString(1),
                    tFuncCall.lineNumber);
        }
        for (int i = 0; i < tFunc.isArgOptional.size(); i++) {
            boolean isOptional = tFunc.isArgOptional.get(i);
            // check if the current param was given an input of TVoidValue
            // if so, error as we dont want them to pass in idk to a paramter thats not
            // optional
            if (params.size() != i) {
                if (params.get(i) instanceof TVoidValue && !isOptional)
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

    /**
     * Checks if the parameters passed to a function call match the function's definition.
     * This method validates the number of arguments and their optionality.
     * @deprecated Use {{@link #checkParams(CheckParams)} instead}
     *
     * @param tFuncCall The function call token containing the arguments.
     * @param scope     The current scope for error reporting.
     * @throws InterpreterException If there's a mismatch in parameters (e.g., missing required arguments).
     */
    @Deprecated(since = "6.2.0")
    protected final void checkParams(@NotNull TFuncCall tFuncCall, Scope scope, ArrayList<Object> params) throws InterpreterException {
        checkParams(new CheckParams(tFuncCall, scope, params));
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
    @NotNull
    @Contract("_, _ -> new")
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
    @NotNull
    @Contract("_, _, _ -> new")
    public static Lambda createLambda(String name, TFunction token, Scope scope) {
        return new Lambda(name, token, scope);
    }

}
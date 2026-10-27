package com.jaiva.interpreter;

import com.jaiva.Main;
import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.InterpreterException.*;
import com.jaiva.errors.JaivaException;
import com.jaiva.errors.JaivaException.DebugException;
import com.jaiva.errors.Warnings;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.runtime.ImportVfs;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.BaseVariable.VariableType;
import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.lang.Keywords;
import com.jaiva.tokenizer.tokens.TAtomicValue;
import com.jaiva.tokenizer.tokens.TSymbol;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.*;
import com.yetnt.utils.builders.AnsiColour;
import com.yetnt.utils.functional.consumer.ThrowableBiConsumer;
import com.yetnt.utils.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

/**
 * The Interpreter class is one of the 3 main classes which handle Jaiva code.
 * <p>
 * It is responsible for interpreting Jaiva code and executing it.
 */
public final class Interpreter {

    /**
     * The throwIfGlobalContext method is used to check if the context is global and
     * if so, it throws an error.
     * <p>
     * This is used to handle the case where a function return or a loop control is
     * used in the global context.
     *
     * @param scope     The context of the code being interpreted.
     * @param lc         The object which caused the interuption.
     * @param lineNumber The line number of the token which caused the interuption.
     * @return Returns a new instance of the ThrowIfGlobalContext class with the
     *         given object and line number.
     * @throws WtfAreYouDoingException If the context is global
     *                                 and the object is a
     *                                 function return or a
     *                                 loop control.
     * @throws CimaException           If the context is global
     *                                 and the object is an
     *                                 error.
     */
    @NotNull
    @Contract("_, _, _ -> new")
    public static ThrowIfGlobalContext throwIfGlobalContext(Scope scope, Object lc, int lineNumber)
            throws Exception {
        if (lc instanceof ThrowIfGlobalContext) {
            lc = ((ThrowIfGlobalContext) lc).c;
        }
        if (lc instanceof DebugException dlc)
            throw dlc; // user shouldnt catch it anywhere fr.
        else if (lc instanceof Keywords.LoopControl) {
            if (scope.getCurrent() == Context.GLOBAL)
                throw new WtfAreYouDoingException(scope, "So. You tried using "
                        + (lc.toString().equals("BREAK") ? Keywords.LC_BREAK : Keywords.LC_CONTINUE)
                        + ". But like, we're not in a loop yknow? ", lineNumber);
        } else if (Primitives.isPrimitive(lc)) {
            // a function return thing then
            if (scope.getCurrent() == Context.GLOBAL)
                throw new WtfAreYouDoingException(scope,
                        "What are you trying to return out of if we're not in a function??", lineNumber);

        } else if (lc instanceof TThrowError) {
//            if (scope.current == Context.GLOBAL)
            Object message = Primitives.toPrimitive(((TThrowError) lc).errorMessage, false, null, scope);
                throw new CimaException(scope, message.toString(), lineNumber);
        }
        return new ThrowIfGlobalContext(lc, lineNumber);
    }

    /**
     * The isVariableToken method checks if the given token is a variable token.
     * <p>
     * This is used to determine if the token is a variable or not.
     *
     * @param t The token to check.
     * @return Returns true if the token is a variable token, false otherwise.
     */
    public static boolean isVariableToken(Object t) {
        return t instanceof TSymbol || t instanceof TVarReassign || t instanceof TAtomicValue
                || Primitives.isPrimitive(t);
    }

    /**
     * helper method that freezes symbols on creation based on the flag.
     * @param s The symbol in question
     * @param s2 The current scope.
     */
    private static void freezeIfScopeConfig(Symbol s, @NotNull Scope s2) {
        if (s2.getConfig().freezeAll()) s.freeze();
    }

    /**
     * Handles variable operations such as creation, reassignment, and primitive
     * parsing.
     * This method processes different types of tokens representing variables or
     * operations
     * on variables and updates the provided variable storage map accordingly.
     *
     * @param t      The token representing the variable or operation to handle.
     *               This
     *               can be
     *               one of several types, including TNumberVar, TBooleanVar,
     *               TStringVar,
     *               TUnknownVar, TArrayVar, or TVarReassign.
     * @param config The configuration object containing settings for the
     *               interpreter,
     *               including the file directory and import settings.
     * @return Returns the primitive value of the token if it is not a
     *         variable-related
     *         token, or null if the operation involves variable creation or
     *         reassignment.
     * @throws Exception If an error occurs during variable handling, such as
     *                   referencing
     *                   an unknown variable, attempting to reassign a frozen
     *                   variable, or
     *                   performing an invalid operation.
     */
    @Nullable
    public static Object handleVariables(Object t, IConfig<Object> config, Scope scope)
            throws Exception {
        // fast path
        if (t instanceof TSymbol && config.getImportVfs().active && !config.getImportVfs().shouldImport(((TokenDefault<?>)t).name.replace("F~", ""))) {
            return null;
        }
        switch (t) {
            case TNumberVar tNumberVar -> {
                Object number = Primitives.toPrimitive(tNumberVar.value, false, config, scope);
                if (number instanceof TThrowError) {
                    throwIfGlobalContext(scope, number, tNumberVar.lineNumber);
                }
                BaseVariable var = BaseVariable.create(((TokenDefault<?>) t).name, (TokenDefault<?>) t,
                        new ArrayList<>(Collections.singletonList(number)), false);
                freezeIfScopeConfig(var, scope);
                scope.getVfs().put(tNumberVar.name, var);
            }
            case TBooleanVar tBooleanVar -> {
                Object bool = Primitives.toPrimitive(tBooleanVar.value, false, config, scope);

                if (bool instanceof TThrowError) {
                    throwIfGlobalContext(scope, bool, tBooleanVar.lineNumber);
                }
                BaseVariable var = BaseVariable.create(((TokenDefault<?>) t).name, (TokenDefault<?>) t,
                        new ArrayList<>(Collections.singletonList(bool)), false);
                freezeIfScopeConfig(var, scope);
                scope.getVfs().put(tBooleanVar.name, var);
            }
            case TStringVar tStringVar -> {
                Object string = Primitives.toPrimitive(tStringVar.value, false, config, scope);
                if (string instanceof TThrowError) {
                    throwIfGlobalContext(scope, string, tStringVar.lineNumber);
                }
                    BaseVariable var = BaseVariable.create(((TokenDefault<?>) t).name, (TokenDefault<?>) t,
                        new ArrayList<>(Collections.singletonList(string)), false);
                freezeIfScopeConfig(var, scope);
                scope.getVfs().put(tStringVar.name, var);
            }
            case TUnknownScalar<?, ?> tUnknownScalar -> {
                Object something = Primitives.toPrimitive(tUnknownScalar.value, false, config, scope);
                Symbol var;
                if (something instanceof TThrowError) {
                    throwIfGlobalContext(scope, something, tUnknownScalar.lineNumber);
                }
                if (something instanceof BaseFunction function) {
                    // this assigns
                    function.token.tooltip = tUnknownScalar.tooltip;
                    function.token.json();
                    var = function;
                }
                else
                    var = BaseVariable.create(((TokenDefault<?>) t).name, (TokenDefault<?>) t,
                            something instanceof ArrayList ? (ArrayList) something
                                    : new ArrayList<>(Collections.singletonList(something)),
                            false);
                freezeIfScopeConfig(var, scope);
                scope.getVfs().put(tUnknownScalar.name, var);
            }
            case TArrayVar tArrayVar -> {
                ArrayList<Object> arr = new ArrayList<>();
                tArrayVar.contents.forEach(obj -> {
                    try {
                        if (obj instanceof TThrowError) {
                            throwIfGlobalContext(scope, obj, tArrayVar.lineNumber);
                        }
                        if (obj instanceof ArrayList) {
                            arr.add(obj);
                        } else {
                            arr.add(Primitives.toPrimitive(obj, false, config, scope));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                BaseVariable var = BaseVariable.create(((TokenDefault<?>) t).name, (TokenDefault<?>) t, arr, true);
                freezeIfScopeConfig(var, scope);
                scope.getVfs().put(tArrayVar.name, var);
            }
            case TFunction function -> {
                if (function instanceof TLambda)
                    return Primitives.toPrimitive(t, false, config, scope);
                String name = function.name.replace("F~", "");
                BaseFunction func = BaseFunction.createFunction(name, function);
                freezeIfScopeConfig(func, scope);
                scope.getVfs().put(name, func);
            }
            case TVarReassign tVarReassign -> {
                MapValue mapValue = scope.getVfs().get(tVarReassign.name);
                if (mapValue == null)
                    throw new InterpreterException.UnknownVariableException(
                            scope, tVarReassign
                    );
                if (MapValue.isEmpty(mapValue)
                        || (mapValue.getValue() == null || !(mapValue.getValue() instanceof BaseVariable
                        || mapValue.getValue() instanceof BaseFunction)))
                    throw new UnknownVariableException(scope, tVarReassign);

                Symbol var = mapValue.getValue();
                if (var.isFrozen)
                    throw new FrozenSymbolException(
                            scope, var, tVarReassign.lineNumber);

                Object o = Primitives.toPrimitive(tVarReassign.newValue, false,
                        config, scope);


                if (o instanceof TThrowError) {
                    throwIfGlobalContext(scope, o, tVarReassign.lineNumber);
                }

                if (o instanceof BaseFunction func) {
                    mapValue.setValue(func);
                } else if (var instanceof BaseVariable v) {
                    if (o instanceof ArrayList a) {
                        v.a_set(a, scope);
                    } else {
                        v.s_set(o, scope);
                    }
                } else if (var instanceof BaseFunction && Primitives.isPrimitive(o)) {
                    // create BaseVariable instead
                    Warnings.println(tVarReassign.lineNumber, "Reassigning what once was a function to a variable can cause token errors and confusion. Avoid doing this.", scope, config);
                    mapValue.setValue(BaseVariable.create(tVarReassign.name, tVarReassign,
                            o instanceof ArrayList<?> a ? (ArrayList<Object>) a : new ArrayList<>(List.of(o)), false
                    ));
                }

                // so hopefully this changes the instance and yeah 👍
            }
            default -> {
                // here its a primitive being parsed or recursively called
                // or TVarRef or TFuncCall or TStatement
                return Primitives.toPrimitive(t, false, config, scope);
            }
        }

        return null; // return null because we are not returning anything.
    }

    /**
     * The interpret method is the main method which handles the interpretation of
     * Jaiva code.
     * <p>
     * It takes a list of tokens and a context and interprets the code accordingly.
     *
     * @param tokens The list of tokens to interpret.
     * @param scope The current scope of the array list of tokens being interpreted
     * @param config The configuration object containing settings for the
     *               interpreter,
     *               including the file directory and import settings.
     * @return Returns the result of the interpretation, which can be a value or a
     *         void class.
     * @throws Exception If an error occurs during interpretation, such
     *                   as an
     *                   unknown
     *                   variable or an invalid operation.
     */
    public static Object interpret(
            @NotNull ArrayList<Token<?>> tokens,
            @NotNull Scope scope,
            @NotNull IConfig<Object> config
    ) throws Exception {
        // prepare a new vfs.
        scope.setVfs(
                config.getImportVfs().active && scope.getCurrent() == Context.GLOBAL
                        ? new Vfs()
                        : scope.getVfs()
        );

        scope.setFilePath(config.getFilePath());

//        vfs = vfs != null ? vfs.clone() : vfs;

        // Step 2: go throguh eahc token
        for (Token<?> t : tokens) {
            TokenDefault<?> token = t.value();
            // first check if we're in a debug environment
            if (config.dc.active) {
                if (config.dc.getBreakpoints().contains(token.lineNumber)) {
                    // if we are, then we pause the execution and wait for the user to continue.
                    config.dc.print(token.lineNumber, null, t,  scope);
                }
                if (config.dc.stepOver.equals(new Pair<>(true, false))) {
                    config.dc.print(token.lineNumber, null, t, scope);
                    continue;
                } else if (config.dc.stepOver.equals(new Pair<>(false, true))) {
                    config.dc.print(token.lineNumber, null, t, scope);
                }
            }
            if (token instanceof TVoidValue) {
                // Well, the user placed idk by itself, so i also don't know what to do with it.
                continue;
            } else if (token instanceof TImport tImport) {
//                Globals g = new Globals(config);
                Path importPath = Path.of(tImport.filePath);

                boolean isLib = tImport.isLib;
                String fileName = tImport.fileName;
                int lineNumber = tImport.lineNumber;
                ArrayList<String> symbols = tImport.symbols;

                Vfs vfsFromFile
                        = importHandle(scope, config, fileName, isLib, importPath, lineNumber, symbols, token);
                if (vfsFromFile == null) continue; // Nohing to import.
                scope.getVfs().putAll(vfsFromFile);
            } else if (isVariableToken(token)) {
                if (token instanceof TFuncCall || token instanceof TVarRef || token instanceof TLambda)
                    handleVariables(t, config, scope);
                else
                    handleVariables(token, config, scope);
                // If it returns a meaningful value, then oh well, because in this case they
                // basically called a function that returned soemthing but dont use that value.
            } else if (token instanceof TFuncReturn tFuncReturn && !config.getImportVfs().active) {
                Object c = handleVariables(tFuncReturn.value, config, scope);
                return throwIfGlobalContext(scope, c, token.lineNumber);
            } else if (token instanceof TLoopControl loopControl && !config.getImportVfs().active) {
                Object lc = loopControl.type;
                return throwIfGlobalContext(scope, lc, loopControl.lineNumber);
            } else if (token instanceof TThrowError lc && !config.getImportVfs().active) {
                return throwIfGlobalContext(scope, lc, lc.lineNumber);
            } else if (token instanceof TWhileLoop whileLoop && !config.getImportVfs().active) {
                // while loop
                Object cond = Primitives.setCondition(whileLoop, config, scope);

                boolean terminate = false;

                while ((Boolean) cond && !terminate) {
                    Object out = Interpreter.interpret(whileLoop.body.lines,
                            new Scope(Context.WHILE, token, scope), config);
                    if (out instanceof ThrowIfGlobalContext old) {
                        if (old.c == Keywords.LoopControl.BREAK)
                            break;
                        if (old.c == Keywords.LoopControl.CONTINUE) {
                            cond = Primitives.setCondition(whileLoop, config, scope);
                            continue;
                        }
                        ThrowIfGlobalContext checker = throwIfGlobalContext(scope, out, old.lineNumber);
                        if (checker.c == Keywords.LoopControl.BREAK)
                            break;
                        if (checker.c == Keywords.LoopControl.CONTINUE) {
                            cond = Primitives.setCondition(whileLoop, config, scope);
                            continue;
                        }
                        return checker;
                    }

                    cond = Primitives.setCondition(whileLoop, config, scope);
                }
                // while loop
            } else if (token instanceof TIfStatement ifStatement && !config.getImportVfs().active) {
                // if statement handling below
                if (!(ifStatement.condition instanceof TExpression))
                    throw new WtfAreYouDoingException(scope,
                            "Okay well idk how i will check for true in " + ifStatement,
                            token.lineNumber);
                Object cond = Primitives.setCondition(ifStatement, config, scope);

                // if if, lol i love coding
                if ((Boolean) cond) {
                    // run if code.
                    Object out = Interpreter.interpret(ifStatement.body.lines,
                            new Scope(Context.IF, token, scope), config);
                    if (out instanceof ThrowIfGlobalContext g) {
                        return throwIfGlobalContext(scope, out, g.lineNumber);
                    }
                } else {
                    // check for else branches first
                    boolean runElseBlock = true;
                    for (TIfStatement elseIf : ifStatement.elseIfs) {
                        if (!(elseIf.condition instanceof TExpression))
                            throw new WtfAreYouDoingException(scope,
                                    "Okay well idk how i will check for true in " + elseIf, token.lineNumber);
                        Object cond2 = Primitives.setCondition(elseIf, config, scope);
                        if ((Boolean) cond2) {
                            // run else if code.
                            Object out = Interpreter.interpret(elseIf.body.lines,
                                    new Scope(Context.ELSE, token, scope), config);
                            if (out instanceof ThrowIfGlobalContext g) {
                                return throwIfGlobalContext(scope, out, g.lineNumber);
                            }
                            runElseBlock = false;
                            break;
                        }
                    }
                    if (runElseBlock && ifStatement.elseBody != null) {
                        // run else block.
                        Object out = Interpreter.interpret(ifStatement.elseBody.lines,
                                new Scope(Context.ELSE, token, scope), config);
                        if (out instanceof ThrowIfGlobalContext g) {
                            return throwIfGlobalContext(scope, out, g.lineNumber);
                        }
                    }
                }
                // if statement handling above
            } else if (token instanceof TForLoop tForLoop && !config.getImportVfs().active) {
                // for loop
                handleVariables(tForLoop.variable, config, scope);
                Object vObject = scope.getVfs().get(tForLoop.variable.name).getValue();
                if (!(vObject instanceof BaseVariable v))
                    throw new WtfAreYouDoingException(scope, vObject, BaseVariable.class,
                            tForLoop.lineNumber);
                if (tForLoop.array != null && tForLoop.increment == null && tForLoop.condition == null) {
                    // for each
                    MapValue mapValue = scope.getVfs().get(tForLoop.array.varName);
                    if (mapValue == null || !((mapValue.getValue()) instanceof BaseVariable))
                        throw new UnknownVariableException(scope, tForLoop.array);
                    BaseVariable arr = (BaseVariable) mapValue.getValue();

                    if (arr.variableType != VariableType.ARRAY
                            && arr.variableType != VariableType.A_FUCKING_AMALGAMATION
                            && !(arr.s_get() instanceof String))
                        throw new WtfAreYouDoingException(scope,
                                "Ayo that variable argument on " + tForLoop.lineNumber + " gotta be an array.",
                                token.lineNumber);

                    List<Object> list = (arr.s_get() instanceof String)
                            // Type String[] of the last argument to method asList(Object...) doesn't
                            // exactly match the vararg parameter type. Cast to Object[] to confirm the
                            // non-varargs invocation, or pass individual arguments of type Object for a
                            // varargs invocation.
                            ? Arrays.asList(((String) arr.s_get()).split(""))
                            : arr.a_getAll();

                    for (Object o : list) {
                        v.s_set(o, scope);
                        Object out = Interpreter.interpret(tForLoop.body.lines,
                                new Scope(Context.FOR, token, scope), config);
                        if (out instanceof ThrowIfGlobalContext g) {
                            if (g.c == Keywords.LoopControl.BREAK)
                                break;
                            if (g.c == Keywords.LoopControl.CONTINUE)
                                continue;
                            return throwIfGlobalContext(scope, out, g.lineNumber);
                        }
                    }
                } else {
                    // normal for loop.
                    assert tForLoop.increment != null;
                    Object increment =
                            tForLoop.increment instanceof String tF
                                    ? tF.charAt(0)
                                    : tForLoop.increment;
                    ThrowableBiConsumer<
                            Object, Scope, InterpreterException
                            > consumer = v.variableType == VariableType.ARRAY ?  v::a_objSet : v::s_set;

                    Object cond = Primitives.setCondition(tForLoop, config, scope);
                    while ((Boolean) cond) {

                        Object out = Interpreter.interpret(tForLoop.body.lines,
                                new Scope(Context.FOR, token, scope), config);
                        if (out instanceof ThrowIfGlobalContext g) {
                            if (g.c == Keywords.LoopControl.BREAK)
                                break;
                            if (g.c == Keywords.LoopControl.CONTINUE) {
                                handleColonzieLoopIncrementExpresion(
                                        consumer, increment, v, config, scope
                                );
                                cond = Primitives.setCondition(tForLoop, config, scope);
                                continue;
                            }
                            return throwIfGlobalContext(scope, out, g.lineNumber);
                        }
                        handleColonzieLoopIncrementExpresion(
                                consumer, increment, v, config, scope
                        );
                        cond = Primitives.setCondition(tForLoop, config, scope);
                    }
                }

                scope.getVfs().remove(v.name);
                // for loop
            } else if (token instanceof TTryCatch throwError && !config.getImportVfs().active) {

                Vfs errorVfs = ((Vfs) scope.getVfs().clone());
                errorVfs.entrySet().removeIf(vf -> !vf.getKey().contains("error"));

                String varName = "error" + (!errorVfs.isEmpty() ? errorVfs.size() : "");

                try {
                    Object out = Interpreter.interpret(throwError.tryBlock.lines,
                            new Scope(Context.TRY, token, scope), config);
                    if (out instanceof ThrowIfGlobalContext g) {
                        if (g.c instanceof Keywords
                                || isVariableToken(g.c)) {
                            return throwIfGlobalContext(scope, out, g.lineNumber);
                        } else if (g.c instanceof TThrowError err) {
                            Token<?> tContainer = new Token<>(null);
                            scope.getVfs().put(varName,
                                    BaseVariable.create(
                                            varName,
                                            new TStringVar(
                                                    varName, err.errorMessage.toString(),
                                                    err.lineNumber),
                                            new ArrayList<>(Collections.singletonList(
                                                    err.errorMessage)),
                                            false));
                            Object out2 = Interpreter.interpret(throwError.catchBlock.lines,
                                    new Scope(Context.CATCH, token, scope),
                                    config);
                            scope.getVfs().remove(varName);
                            if (out2 instanceof ThrowIfGlobalContext g2) {
                                return throwIfGlobalContext(scope, out2, g2.lineNumber);
                            }

                            // return void.class;
                        }
                    }
                } catch (JaivaException e) {
                    // catch any exception, and call the catch block with the error.
                    Token<?> tContainer = new Token<>(null);

                    String string = AnsiColour.remove(e.getMessage());
                    scope.getVfs().put(varName,
                            BaseVariable.create(
                                    varName,
                                    new TStringVar(
                                            varName, string,
                                            throwError.catchBlock.lineNumber),
                                    new ArrayList<>(Collections.singletonList(
                                            string)),
                                    false));
                    Object out2 = Interpreter.interpret(throwError.catchBlock.lines,
                            new Scope(Context.CATCH, token, scope), config); // line
                    // 542
                    scope.getVfs().remove(varName);
                    if (out2 instanceof ThrowIfGlobalContext g2) {
                        return throwIfGlobalContext(scope, out2, g2.lineNumber);
                    }

                    // return void.class;
                }
            }
            // return contextValue;
        }
        // System.out.println("heyy");

        if (config.dc.active && scope.getCurrent() == Context.GLOBAL) {
            // if we're in the global context, then we end the debugging session.
            config.dc.endOfFile(scope);
        }
        return config.getImportVfs().active || config.isREPL() ? scope.getVfs() : void.class;
    }

    @Nullable
    public static Vfs importHandle(@NotNull Scope scope, @NotNull IConfig<Object> config, String fileName, boolean isLib, Path importPath, int lineNumber, ArrayList<String> symbols, TokenDefault<?> token) throws Exception {
        Vfs vfsFromFile;
        if (scope.getGlobals().builtInGlobals.containsKey(fileName) && isLib)
            vfsFromFile = scope.getGlobals().builtInGlobals.get(fileName)
                    .load(config, scope.getGlobals());
        else {
            // Check if the path is not absolute
            if (!importPath.isAbsolute())
                // Resolve it relative to the current file's directory,
                // then normalise to tidy up any relative path elements.
                importPath = config.getFileDirectory().resolve(importPath).normalize();


            importPath = importPath.toAbsolutePath();

            ArrayList<Token<?>> tks = null;
            try {
                tks = Main.parseTokens(importPath.toString(), true);
            } catch (JaivaException e) {
                throw new TseaException(scope,  lineNumber, e);
            }

            if (tks.isEmpty())
                return null;

            IConfig<Object> newConfig = new IConfig<Object>(config.getSanitisedArgs(), importPath.toString()
            );

            newConfig.setImportVfs(new ImportVfs(true, symbols)); // This tells the interpreter to only parse exported symbols. (Functions
                                        // and variables)

            try {
                vfsFromFile = ((Vfs) Interpreter.interpret(tks, new Scope(Context.IMPORT, token, scope), newConfig));
            } catch (JaivaException e) {
                throw new TseaException(scope,  lineNumber, e);
            }

            if ((vfsFromFile == null))
                // error? maybe not yet but throw.
                throw new CatchAllException(scope,
                        importPath
                                + " either had nothing to export or something went horribly wrong.",
                        lineNumber);
                // continue;

        }

        if (!symbols.isEmpty())
            vfsFromFile.entrySet().removeIf(e -> !symbols.contains(e.getKey()));

        return vfsFromFile;
    }

    public static void handleColonzieLoopIncrementExpresion(
            ThrowableBiConsumer<
                                Object, Scope, InterpreterException
                                > consumer, Object in, BaseVariable v, IConfig<Object> config, Scope scope
            ) throws Exception{

        Object val;
        if (in instanceof Character c) {
            val = switch (v.s_get()) {
                case Integer i -> i + (c == '+' ? 1 : -1);
                case Long i -> i + (c == '+' ? 1 : -1);
                case Double i -> i + (c == '+' ? 1 : -1);
                case null, default -> throw new WtfAreYouDoingException(
                        scope, "did you just try to increment " + v.name + " when it's not even a" +
                        " number?", v.token.lineNumber
                );
            };
        } else {
            val = Primitives.toPrimitive(
                    in, false, config, scope
            );
        }

        consumer.apply(val, scope);
    }
}

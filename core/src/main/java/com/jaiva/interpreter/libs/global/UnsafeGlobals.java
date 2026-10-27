package com.jaiva.interpreter.libs.global;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Interpreter;
import com.jaiva.interpreter.MapValue;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.symbol.*;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.TVarRef;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * These functions are also available in {@link GlobalFunctions} which aren't really that safe to use.
 */
public class UnsafeGlobals extends BaseLibrary {
    public UnsafeGlobals() {
        add(new FLinker(), new FTseaFunction());
    }

    static class FLinker extends BaseFunction {
        FLinker() {
            super(
                    FunctionBuilder.start()
                            .name("linker")
                            .setLib("global")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(new AArgument("a", "The symbol which holds the MapValue to be linked.", false, Argument.Type.ANY))
                                            .add(new AArgument("b", "The symbol who's MapValue will either be created or overwritten", false, Argument.Type.ANY))
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Links the MapValue instance of 'a' into 'b' such that they hold the same value and if one is edited the other will also have that edit.")
                                            .sinceVersion("5.0.4") // yes this was added in 7.0.0, but it existed before in jaiva/debug as d_link so its not new.
                                            .addReturns("idk")
                                            .addNote("""
                                            A usual (b <- a) syntax would suffice if you'd like to copy the value of a into b.
                                            However when a is changed, b will stay the value you set earlier. This function fixes that where it will
                                            link the exact MapValue from a into b, discarding b's old MapValue. such that editing any one of the symbols
                                            via the reassignment syntax will update the linked variable. In the case that the "b" parameter does not actually exist in the symbol table, d_link will try to make it, itself.
                                            """)
                                            .addExample("""
                                            maak a <- f~() : 10! @ Lambda that returns 10
                                            maak b <- true! @ boolean value true
                                            
                                            @ With normal reassignment syntax, setting b to a then changing b does not update a
                                            b <- a!
                                            b <- 10!
                                            khuluma(a)! @ prints the lambda signature and not 10.
                                            
                                            @ With d_link, the exact MapValue held by that alias is copied.
                                            d_link(a, b)!
                                            b <- 10!
                                            khuluma(a)! @ prints 10
                                            a <- 100!
                                            khuluma(b)! @ prints 100
                                            """)
                            )
            );
            freeze();
            evaluateParamsLater();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            TokenDefault<?> refA = null;
            TokenDefault<?> refB = null;

            if (callProperties.tFuncCall().args.getFirst() instanceof Token<?>(Object value))
                refA = (TokenDefault<?>) value;
            else if (callProperties.tFuncCall().args.getFirst() instanceof TokenDefault<?> t)
                refA = t;
            if (callProperties.tFuncCall().args.get(1) instanceof Token<?>(Object value))
                refB = (TokenDefault<?>) value;
            else  if (callProperties.tFuncCall().args.get(1) instanceof TokenDefault<?> t)
                refB = t;

            assert refA != null;
            assert refB != null;
            if (!(refA instanceof TVarRef))
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "The first parameter has to be a reference or variable.", callProperties.getLineNumber());
            if  (!(refB instanceof TVarRef))
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "The second parameter has to be a reference or variable.", callProperties.getLineNumber());

            MapValue mv = callProperties.scope().getVfs().get(((TVarRef) refA).varName.toString());
            if (mv == null || MapValue.isEmpty(mv))
                throw new InterpreterException.UnknownVariableException(callProperties.scope(), refA.name, callProperties.getLineNumber());

            callProperties.scope().getVfs().put(((TVarRef) refB).varName.toString(), mv); // This does mean, if the second reference doesnt exist this function woll create it.
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    static class FTseaFunction extends BaseFunction {
        public FTseaFunction() {
            super(
                    FunctionBuilder.start()
                            .name("tseaf")
                            .setLib("global")
                            .arguments(Arguments.getInstance()
                                    .add(new AArgument("path", "The path to import from.", false, Argument.Type.STRING))
                                    .add(new AArgument("symbol", "The symbol to import from. This can either be a string, or a non-existing variable reference.", false, Argument.Type.STRING))
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Imports and immediately returns the imported symbol from the given file or library")
                                            .sinceVersion("7.0.0")
                                            .addReturns("The value, if the requested symbol is of a variable, the function if the requested symbol is a function, but otherwise idk if the library/file doesnt expose such a symbol.")
                                            .addExample("""
                                                    @ Normally, you have to go via import syntax just to import a single variable
                                                    tsea "jaiva/somewhere" <- func!
                                                    func()!
                                                    
                                                    @ Using tseaf you can do the following
                                                    tseaf("jaiva/somewhere", "func")()!
                                                    
                                                    @ Or otherwise alias it yourself
                                                    maak func_1 <- tseaf("jaiva/somewhere", "func")!
                                                    func_1()!
                                                    
                                                    @ Works for files that expose symbols too
                                                    maak arr <- tseaf("../path", "array")!
                                                    khuluma(arr~)! @ Prints array length
                                                    
                                                    @ And for the symbol name, it's not required to pass a string.
                                                    @ Provided the reference does not exist in the scope, then it will
                                                    @ use that non-existing reference's name. Otherwise if it exists it
                                                    @ will use that reference's value like normal.
                                                    
                                                    maak funcp <- 10!
                                                    tseaf("jaiva/somewhere", funca)! @ Will retrieve funca from the list
                                                    tseaf("jaiva/somewhere", funcp)! @ Will fail, since funcp will pass the number 10 as the argument.
                                                    """)
                                            .addNote(
                                                    "This isn't special, the \"jaiva/\" prefix for libraries like normal tsea syntax is" +
                                                            " still requried to differentiate a file from a library import."
                                            )
                            )
            );
            freeze();
            evaluateParamsLater();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());

            Object first = Primitives.toPrimitive(
                    callProperties.params().getFirst(),
                    false, callProperties.config(), callProperties.scope()
            );
            if (!(first instanceof String path))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "1", first, String.class, callProperties.getLineNumber()
                );

            String symbol = null;
            Object o = null;
            try {
                o = Primitives.toPrimitive(
                        callProperties.params().get(1),
                        false, callProperties.config(), callProperties.scope()
                );
            } catch (InterpreterException.UnknownVariableException ignored) {
                Object s = callProperties.tFuncCall().args.get(1);
                if (s instanceof Token<?>(Object value)) {
                    if (!(value instanceof TVarRef tVarRef))
                        throw new InterpreterException.WtfAreYouDoingException(
                                callProperties.scope(),
                                "Okay so if you're going to pass a reference which does not exist, it must at least, " +
                                        "at the VERY least. Be a variable reference", callProperties.getLineNumber()
                        );
                    if (!(tVarRef.varName instanceof String n))
                        throw new InterpreterException.WtfAreYouDoingException(
                                callProperties.scope(),
                                "I dont even know how the fuck you did this.", callProperties.getLineNumber()
                        );
                    symbol = n;
                } else {
                    throw new InterpreterException.WtfAreYouDoingException(
                            callProperties.scope(),
                            "Okay so if you're going to pass a reference which does not exist, it must at least, " +
                                    "at the VERY least. Be a variable reference", callProperties.getLineNumber()
                    );
                }
            }

            if (symbol == null && !(o instanceof String))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "2", o, String.class, callProperties.getLineNumber()
                );
            else if (symbol == null) {
                symbol = (String)o;
            }

            Vfs vfs = Interpreter.importHandle(
                    callProperties.scope(),
                    callProperties.config(),
                    path.startsWith("jaiva")
                            ? path
                            .replaceFirst(Pattern.quote("jaiva/"), "")
                            .replaceFirst(Pattern.quote("jaiva\\"), "")
                            : path,
                    path.startsWith("jaiva"),
                    Path.of(path),
                    callProperties.getLineNumber(),
                    new ArrayList<>(List.of(symbol)),
                    callProperties.tFuncCall()
            );

            if (vfs == null)
                throw new InterpreterException.TseaException(
                        callProperties.scope(),
                        callProperties.getLineNumber(),
                        "The provided file does not expose any symbols."
                );

            List<MapValue> list = vfs.values().stream().toList();
            if (list.isEmpty())
                return Token.voidValue(callProperties.getLineNumber());
            MapValue first1 = list
                    .getFirst();
            // there should only ever be a single entry since we provided to the handleImprot
            // a single thing to fetch.
            if (first1 == null)
                // No such thing exists.
                return Token.voidValue(callProperties.getLineNumber());
            Symbol sym = first1
                    .getValue();

            if (sym instanceof BaseVariable bv)
                return bv.variableType == BaseVariable.VariableType.SCALAR
                ? bv.s_get() : bv.a_getAll();

            return sym; // otherwise its a function. meaning we can just return it.

        }
    }
}

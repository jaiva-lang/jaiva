package com.jaiva.interpreter.libs.debug;

import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.JaivaException.DebugException;
import com.jaiva.interpreter.MapValue;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libBuilders.func.arg.AVarArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.TVarRef;

import java.util.ArrayList;
import java.util.List;

@JaivaLibrary(path = "debug", description = "Random stuff you should not be working with. These aren't related to the CLI debugger.")
public class Debug extends BaseLibrary {

    public Debug() {
        add(new FEmit(), new FVfs(), new FLink(), new FGetScope());
    }

    static class FLink extends BaseFunction {
        FLink() {
            super(
                    FunctionBuilder.start()
                            .name("d_link")
                            .setLib("debug")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(new AArgument("a", "The symbol which holds the MapValue to be linked.", false, Argument.Type.ANY))
                                            .add(new AArgument("b", "The symbol who's MapValue will either be created or overwritten", false, Argument.Type.ANY))
                            )
                            .docs(
                                    JDoc.builder()
                                            .markDeprecated(
                                                    "This function has moved to the global scope and is now named \"linker\". Use that instead as this may be removed"
                                            )
                                            .addDesc("Links the MapValue instance of 'a' into 'b' such that they hold the same value and if one is edited the other will also have that edit.")
                                            .sinceVersion("5.0.4")
                                            .addReturns("idk")
                                            .addNote("""
                                            A usual (b <- a) syntax would suffice if you'd like to copy the value of a into b.
                                            However when a is changed, b will stay the value you set earlier. This function fixes that where it will
                                            link the exact MapValue from a into b, discarding b's old MapValue. such that editing any one of the symbols
                                            via the reassignment syntax will update the linked variable.
                                            
                                            In the case that the "b" parameter does not actually exist in the symbol table, d_link will try to make it, itself.
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

    static class FEmit extends BaseFunction {
        FEmit() {
            super(FunctionBuilder.start()
                    .name("d_emit")
                    .arguments(
                            Arguments.getInstance()
                                    .addVarArg(new AVarArgument("arr", "The array of values to pass to the exception"))
                    )
                    .docs(
                           JDoc.builder()
                                   .addDesc("Throws a DebugException to be caught by a Java test class and emits the given variables")
                                   .addReturns("Physically can't return. As it always throws an error")
                                   .addNote(
                                           "If you aren't familiar with Java, the language Jaiva is developed in. " +
                                                   "This will essentially forcefully stop the execution of the interpreter, with the intent" +
                                                   " for said error to be caught and dealt with by another Java class. This serves 0 purpose if " +
                                                   " you're just running a Jaiva file."
                                   )
                                   .sinceVersion("1.0.2")
                    )
            );
            freeze();
            evaluateParamsLater();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws DebugException {
            // Any implementation of normal interpreter functions which could throw such an
            // exception should be in the catch.
            ArrayList<Object> components = new ArrayList<>();
            try {
                checkParams(callProperties.toCheckParams()); // throws if params aren't correct. So naturally we loose having
                                                // varargs, so its
                // fine.
                if (!callProperties.params().isEmpty()) {
                    for (Object param : callProperties.params()) {
                        components.add(Primitives.toPrimitive(
                                param,
                                false,
                                callProperties.config(), callProperties.scope()
                        ));
                    }
                }
            } catch (Exception e) {
                throw new DebugException(e);
            }
            throw new DebugException(components, callProperties.scope(), callProperties.config(), callProperties.getLineNumber());
        }
    }

    static class FVfs extends BaseFunction {
        FVfs() {
            super(FunctionBuilder.start()
                    .name("d_vfs")
                    .docs(JDoc.builder()
                            .addDesc("Returns the current context's vfs.")
                            .sinceVersion("4.1.0")
                            .addReturns("A 2d array, first array containing the keys, second array the values.")
                            .addNote("This function does not allow you to edit the current vfs, only to get" +
                                    " everything that is currently within the vfs as an array." +
                                    " Everytime this function is called a new array containing all the stuff is made.")
                    )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<Object> symbols = new ArrayList<>();
            callProperties.scope().getVfs().forEach((v, f)-> {
                names.add(v);
                Symbol sym = f.getValue();
                if (sym instanceof BaseVariable var) {
                    if (var.variableType == BaseVariable.VariableType.ARRAY)
                        symbols.add(var.a_getAll());
                    else
                        symbols.add(var.s_get());
                } else if (sym instanceof BaseFunction function) {
                    symbols.add(function);
                }

            });
            return new ArrayList<>(List.of(names, symbols));
        }
    }

    static class FGetScope extends BaseFunction {
        public FGetScope() {
            super(FunctionBuilder.start()
                    .name("d_getScope")
                    .docs(JDoc.builder()
                            .sinceVersion("5.0.4")
                            .addDesc("gets the scope. wahtd you expect")
                            .addReturns("the scope string")
                            .addNote("This is the exact same scope string that an error outputs")
                    )
            );
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            return callProperties.scope().toString();
        }
    }
}

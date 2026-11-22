package com.jaiva.interpreter.libs.reflect;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.MapValue;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.*;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.TVarRef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@JaivaLibrary(
        path = "reflect",
        description = "playin around with interpreter internals yknow."
)
public class ReflectLib extends BaseLibrary {

    public ReflectLib() {
        add(new FFuncEvalParamsLater(), new FVarPreferArr(), new FSymUnfreeze(), new FSymGetModifiers());
    }

    private static Symbol getSymbol(CallProperties callProperties, BaseFunction func, Object obj) throws InterpreterException {

        if (obj instanceof BaseFunction f)
            return f;

        obj = callProperties.tFuncCall().args.getFirst();

        TVarRef tokenDefault = switch (obj) {
            case TokenDefault<?> tok -> getTokenDefault(callProperties, func, obj, tok);
            case Token<?>(Object value) -> getTokenDefault(callProperties, func, obj, (TokenDefault<?>) value);
            case null, default -> throw new InterpreterException.FunctionParametersException(
                    callProperties.scope(), func, "1", obj, TVarRef.class,
                    callProperties.getLineNumber()
            );
        };

        if (!(tokenDefault.varName instanceof String label))
            throw new InterpreterException.WtfAreYouDoingException(
                    callProperties.scope(), "Brother use a simple identifier",
                    callProperties.getLineNumber()
            );

        MapValue mv = callProperties.scope().getVfs().get(label);

        if (mv == null)
            throw new InterpreterException.UnknownVariableException(
                    callProperties.scope(), tokenDefault
            );

        return mv.getValue();
    }

    private static BaseFunction getAsFunction(CallProperties callProperties, BaseFunction original, Object obj) throws InterpreterException {
        Symbol symbol = getSymbol(callProperties, original, obj);
        if (!(symbol instanceof BaseFunction ret))
            throw new InterpreterException.WtfAreYouDoingException(
                    callProperties.scope(), "Lowkey expected the input to be a function from the scope.",
                    callProperties.getLineNumber()
            );
        return ret;
    }

    private static BaseVariable getAsVariable(CallProperties callProperties, BaseFunction original, Object obj) throws InterpreterException {
        Symbol symbol = getSymbol(callProperties, original, obj);
        if (!(symbol instanceof BaseVariable ret))
            throw new InterpreterException.WtfAreYouDoingException(
                    callProperties.scope(), "Lowkey expected the input to be a variable from the scope.",
                    callProperties.getLineNumber()
            );
        return ret;
    }

    private static TVarRef getTokenDefault(CallProperties callProperties, BaseFunction func, Object obj, TokenDefault<?> tok) throws InterpreterException.FunctionParametersException {
        if (tok instanceof TVarRef tokVarRef)return  tokVarRef;
        else {
            throw new InterpreterException.FunctionParametersException(
                    callProperties.scope(), func, "1", obj, TVarRef.class,
                    callProperties.getLineNumber()
            );
        }
    }

    static class FVarPreferArr extends BaseFunction {
        public FVarPreferArr() {
            super(FunctionBuilder.start()
                    .name("r_vPreferArr")
                    .setLib("reflect")
                    .arguments(Arguments.getInstance()
                            .add(new AArgument("varRef", "The variable reference.", false, Argument.Type.ANY))
                            .add(new AArgument("newValue", "Whether the variable should prefer arrays or not. Defaults to true", true, Argument.Type.BOOLEAN))
                    )
                    .docs(
                            JDoc.builder()
                                    .addDesc("Set's the \"preferArr\" property of a variable to true, or otherwise another value")
                                    .addNote("Variables in Jaiva are dual storage, in that they have 2 separate slots for a singular" +
                                            " scalar value and an array value. Usually this distinction is made at variable creation time" +
                                            " however it does not change, meaning if you define a variable to hold 1, then reassign it the" +
                                            " value of an array, your array was stored successfully be any normal calls to the variable" +
                                            " will only return 1. This function flips it to force it to instead prefer the stored array" +
                                            " over the scalar value.")
                                    .addReturns("idk")
                                    .sinceVersion("7.0.0")
                                    .addExample("""
                                            tsea "jaiva/reflect" <- r_vPreferArr!
                                            
                                            maak var <- 10!
                                            var <- arrLit(1, 2, 3)!
                                            
                                            khuluma(var)! @ Prints 10
                                            r_vPreferArr(var)!
                                            khuluma(var)! @ Prints [1, 2, 3] Although now you have the inverse issue.
                                            r_vPreferArr(var, false)!
                                            khuluma(var)! @ Prints 10
                                            """)
                    )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            BaseVariable variable = getAsVariable(callProperties, this, callProperties.params().getFirst());
            boolean bool = true;
            if (callProperties.params().size() > 1) {
                Object objBool = callProperties.params().get(1);
                if (objBool instanceof Boolean b) bool = b;
            }

            variable.setPreferArray(bool);
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    @SymbolConfig(unsafe = true)
    static class FFuncEvalParamsLater extends BaseFunction {
        public FFuncEvalParamsLater() {
            super(FunctionBuilder.start()
                    .name("r_fEvalParamsLater")
                    .setLib("reflect")
                    .arguments(Arguments.getInstance()
                            .add(new AArgument("funcRef", "The function reference.", false, Argument.Type.ANY))
                            .add(new AArgument("newValue", "Whether the function should evaluate parameters later or not.", true, Argument.Type.BOOLEAN))
                    )
                    .docs(
                            JDoc.builder()
                                    .addNote("Majority of Functions In Jaiva, rely on the interpreter to parse and resolve input " +
                                            "arguments for it, before it runs. However a minority such as d_emit from jaiva/debug " +
                                            "prefer to resolve parameters itself, in which case they have a property \"evalParamsLater\" " +
                                            "set to true. What does this mean? Changing this property for any function is 99.98% going " +
                                            "to brick it. Only library functions can decide how to handle special input otherwise " +
                                            "Jaiva defined functions should never have this value set to true. And i mean you can test and find out.")
                                    .addDesc("Sets the \"evalParamsLater\" property of a function to true/false.")
                                    .sinceVersion("7.0.0")
                                    .addReturns("idk")
                                    .addExample("""
                                            tsea "jaiva/reflect" <- r_fEvalParamsLater!
                                            tsea "jaiva/math" <- m_sin!
                                            
                                            r_fEvalParamsLater(m_sin)!
                                            @ In this case, it still works since the tokenizer passes the integer 1
                                            khuluma(m_sin(1))!
                                            @ However the minute you make it a variable,
                                            @ m_sin gets a token and fails
                                            maak a <- 1!
                                            khuluma(m_sin(a))!
                                            """)
                    )
            );
            freeze();
        }

        @Override
        public Object call(@NotNull CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            BaseFunction func = getAsFunction(callProperties, this, callProperties.params().getFirst());
            boolean bool = false;
            if (callProperties.params().size() > 1) {
                Object objBool = callProperties.params().get(1);
                if (objBool instanceof Boolean b) bool = b;
            }

            func.setEvaluateParamsEarly(bool);
            return Token.voidValue(callProperties.getLineNumber());
        }
    }


    @SymbolConfig(unsafe = true)
    static class FSymUnfreeze extends BaseFunction {
        public FSymUnfreeze() {
            super(FunctionBuilder.start()
                    .name("r_sUnfreeze")
                    .setLib("reflect")
                    .arguments(Arguments.getInstance()
                            .add(new AArgument("symbol", "The symbol reference. Function or variable.", false, Argument.Type.ANY))
                    )
                    .docs(
                            JDoc.builder()
                                    .addNote("Unfreezes a frozen symbol. This does not imply that the instance of the variable " +
                                            "is now writable, but that the container it owns in the scope's VFS can be written " +
                                            "and hence overwrite the symbol. This also does not remove any modifiers that the symbol " +
                                            "may have been annotated with it simply changes the internal property.")
                                    .sinceVersion("7.0.0")
                                    .addDesc("Unfreezes a symbol, making it's \"pointer\" mutable in any scope.")
                                    .addExample("""
                                            tsea "jaiva/reflect" <- r_sUnfreeze!
                                            
                                            r_sUnfreeze(khuluma)!
                                            
                                            @ Now you can edit khuluma to your liking
                                            khuluma <- 1!
                                            @ khuluma function itself is just overwritten.
                                            @ So you have lost the ability to print.
                                            @ Congrats.
                                            """)
                                    .addReturns("idk")
                    )
            );
            freeze();
        }

        @Override
        public Object call(@NotNull CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            Symbol symbol = getSymbol(callProperties, this, callProperties.params().getFirst());
            symbol.unfreeze();
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    static class FSymGetModifiers extends BaseFunction {
        public FSymGetModifiers() {
            super(FunctionBuilder.start()
                    .name("r_sGetModifiers")
                    .setLib("reflect")
                    .arguments(Arguments.getInstance()
                            .add(new AArgument("symbol", "The symbol reference. Function or variable.", false, Argument.Type.ANY))
                    )
                    .docs(
                            JDoc.builder()
                                    .addNote("Literally just retries the modifiers this symbol has as a bare bones string array.")
                                    .sinceVersion("7.0.0")
                                    .addDesc("Returns the modifiers a symbol has as an array of strings.")
                                    .addExample("""
                                            tsea "jaiva/reflect" <- r_sGetModifiers!
                                            
                                            maak [test, r, f f] x <- 1!
                                            
                                            khuluma(r_sGetModifiers(x))! @ prints ["test", "r", "f f"]
                                            """)
                                    .addReturns("An array of strings")
                    )
            );
            freeze();
        }

        @Override
        public Object call(@NotNull CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            Symbol symbol = getSymbol(callProperties, this, callProperties.params().getFirst());
            return new ArrayList<>(symbol.getModifiers());
        }
    }
}

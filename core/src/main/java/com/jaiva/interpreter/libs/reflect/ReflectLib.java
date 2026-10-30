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
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.TVarRef;

@JaivaLibrary(
        path = "reflect",
        description = "Unsafe stuff. lol"
)
public class ReflectLib extends BaseLibrary {

    public ReflectLib() {
        add(new FFuncEvalParamsLater(), new FVarPreferArr(), new FSymUnfreeze());
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

    @SymbolConfig(unsafe = true)
    static class FVarPreferArr extends BaseFunction {
        public FVarPreferArr() {
            super(FunctionBuilder.start()
                    .name("r_vPreferArr")
                    .arguments(Arguments.getInstance()
                            .add(new AArgument("varRef", "The variable reference.", false, Argument.Type.ANY))
                            .add(new AArgument("newValue", "Whether the variable should prefer arrays or not", true, Argument.Type.BOOLEAN))
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
                    .arguments(Arguments.getInstance()
                            .add(new AArgument("funcRef", "The function reference.", false, Argument.Type.ANY))
                            .add(new AArgument("newValue", "Whether the function should evaluate parameters later or not.", true, Argument.Type.BOOLEAN))
                    )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
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
                    .arguments(Arguments.getInstance()
                            .add(new AArgument("symbol", "The symbol reference. Fucntion or variable.", false, Argument.Type.ANY))
                    )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            Symbol symbol = getSymbol(callProperties, this, callProperties.params().getFirst());
            symbol.unfreeze();
            return Token.voidValue(callProperties.getLineNumber());
        }
    }
}

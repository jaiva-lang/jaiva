package com.jaiva.interpreter.symbol.fn;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AVarArgument;
import com.jaiva.tokenizer.tokens.specific.TFunction;
import io.github.yetnt.utils.collection.SetDeque;

public final class LinkedFunction extends DefinedFunction {

    private final BaseFunction first;
    private final BaseFunction second;
    private final LinkedFuncType type;

    public static enum LinkedFuncType {
        OR, AND
    }

    public LinkedFunction(String name, BaseFunction a, BaseFunction b, LinkedFuncType type) {
        super(FunctionBuilder.start()
                .name(name+"-linked")
                .arguments(Arguments.getInstance()
                        .addVarArg(new AVarArgument("args", "List of arguments")))
        );
        this.first = a;
        this.second = b;
        this.type = type;
    }

    @Override
    public Object call(CallProperties callProperties) throws Exception {
        SetDeque<BaseFunction> deque = new SetDeque<>();

        deque.add(this.first);
        deque.add(this.second);

        if (
                ((TFunction)this.second.token).args.size() == callProperties.params().size()
                || (
                        (((TFunction)this.first.token).args.size() < callProperties.params().size())
                        && !(((TFunction)this.first.token).varArgs)
                )
        ) deque.addFirst(this.second);


        if (type == LinkedFuncType.OR) {
            try {
                return deque.getFirst().call(callProperties);
            } catch (Exception e) {
                return deque.getLast().call(callProperties);
            }
        } else {
            try {
                return deque.getFirst().call(callProperties);
            } catch (InterpreterException.FunctionParametersException e) {
                return deque.getLast().call(callProperties);
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + " -> (" + first.toString() + (type == LinkedFuncType.OR ? " ∪ " : " ∩ ") + second.toString() + ")";
    }
}

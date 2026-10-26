package com.jaiva.tokenizer.tokens;

import com.jaiva.tokenizer.tokens.specific.TExpression;
import com.jaiva.tokenizer.tokens.specific.TExtendParams;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;

import java.util.ArrayList;

/**
 * An interface describing functionality that if this particular token is or references a singular
 * function call, and a TContinueParams token follows it, the Continue Params token is allowed to mutate
 * said function call to add parameters into the list.
 */
public interface TParamsExtendable {
    default boolean endsWithFuncCall() {
        return get() != null;
    }
    TFuncCall get();
    default void addArguments(TExtendParams tExtendParams) {
        if (get().args.size() == 1 && get().args.getFirst() == null)
            get().args.removeFirst();
        for (int i = 0; i < tExtendParams.repeat; i++) {
            get().args.addAll(tExtendParams.args);
        }
    }
    default TFuncCall checkObject(Object t) {
        if (t instanceof TFuncCall f) return f;
        if (t instanceof TExpression expr) {
            return checkObject(expr.rHandSide);
        }
        return switch (t) {
            case Token<?> w when w.value() instanceof TFuncCall f -> f;
            case Token<?> w when w.value() instanceof TExpression expr -> checkObject(expr.rHandSide);
            default -> null;
        };
    }
}

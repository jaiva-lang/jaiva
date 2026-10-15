package com.jaiva.interpreter.symbol;

import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.Scope;
import com.jaiva.tokenizer.tokens.specific.TFunction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class Lambda extends DefinedFunction {
    /**
     * This one field enables currying lmao.
     */
    private final Scope lambdaScope;

    Lambda(String name, TFunction token, Scope scope) {
        super(name, token);
        lambdaScope = scope;
    }

    /**
     * Prepares parameters by converting them to their primitive forms.
     *
     * @return A list of prepared parameters.
     * @throws Exception If an error occurs during parameter preparation.
     */
    private ArrayList<Object> prepareParams(@NotNull CallProperties callProperties) throws Exception {
        ArrayList<Object> preparedParams = new ArrayList<>();
        for (Object param : callProperties.params()) {
            if (param == null && callProperties.params().size() == 1) return preparedParams;
            preparedParams.add(Primitives.toPrimitive(param, false, callProperties.config(), callProperties.scope()));
        }
        return preparedParams;
    }

    @Override
    public Object call(@NotNull CallProperties callProperties) throws Exception {
        // Because we're going into a closure now, m
        return super.call(
                new CallProperties(callProperties.tFuncCall(),
                prepareParams(callProperties),
                callProperties.config(),
                lambdaScope)
        );
    }
}

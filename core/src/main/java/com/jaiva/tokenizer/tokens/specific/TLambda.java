package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.tokenizer.tokens.TAtomicValue;

import java.util.ArrayList;
import java.util.List;

public final class TLambda extends TFunction implements TAtomicValue {
    public TLambda(String name, String[] args, Object input, int ln) throws JaivaException {
        super(name, args, new TCodeblock(
                new ArrayList<>(List.of(
                        new TFuncReturn(input, ln).toToken()
                )), ln, ln
        ), ln);
    }
}

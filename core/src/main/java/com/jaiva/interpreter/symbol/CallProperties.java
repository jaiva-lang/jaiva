package com.jaiva.interpreter.symbol;

import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;

import java.util.ArrayList;

public record CallProperties(TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) {
}
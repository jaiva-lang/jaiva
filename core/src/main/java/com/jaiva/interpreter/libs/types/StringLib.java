package com.jaiva.interpreter.libs.types;

import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.jdoc.JDoc;

@JaivaLibrary(path = "strings", description = "Working with strings man")
public class StringLib extends BaseLibrary {

    static class FSplit extends BaseFunction {
        public FSplit() {
            super(
                    FunctionBuilder.start()
                            .setLib("strings")
                            .name("s_split")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(new AArgument("string", "The input string to split", false, Argument.Type.STRING))
                                            .add(new AArgument("char", "The character to split by, should be a string of length 1", false, Argument.Type.STRING))
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Splits a string by a given character into an array consisting of the substrings found between the given char")
                                            .addNote("This method is both quote aware (\"), and brace (() and []) aware. In that a given char found in between those will be ignored.")
                                            .sinceVersion("7.0.0")
                                            .addReturns("An array of substrings")
                            )
            );
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());

            String string = fetch("string", String.class, callProperties);
//            String char = fe
        }
    }
}

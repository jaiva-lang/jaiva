package com.jaiva.interpreter.libs.types;

import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.fn.CallProperties;
import com.jaiva.interpreter.symbol.fn.ImplementedFunction;
import com.jaiva.interpreter.symbol.fn.interfaces.ArgumentChecker;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.utils.Find;
import org.jetbrains.annotations.NotNull;

@JaivaLibrary(path = "strings", description = "Working with strings man")
public class StringLib extends BaseLibrary {

    public StringLib() {
        add(new FSplit());
    }

    static class FSplit extends ImplementedFunction {
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
        public Object call(@NotNull CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());

            String string = fetch("string", String.class, callProperties);
            String charI = fetch("char", String.class, callProperties,
                    ArgumentChecker.simpleError((s) -> s.length() != 1, "The char input has to be... a single character gng")
            );

            return Find.split(string, charI.charAt(0));
        }
    }
}

package com.jaiva.interpreter.libs.cont;

import com.jaiva.interpreter.libBuilders.var.VariableBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.continuous.CFuncInput;
import com.jaiva.tokenizer.jdoc.JDoc;
import io.github.yetnt.utils.builders.MarkDownLiteral;

@JaivaLibrary(
        path = "continuous",
        description = "Contains constants for providing input into continuous functions." +
                " A continuous function, is one which closes over some form of system resources" +
                " whether it be a file or some networking socket. See "
        +"[Continuous Function](../Continuous-Functions.md)"
)
public class ContinuousConst extends BaseLibrary {
    public ContinuousConst() {
        for (CFuncInput value : CFuncInput.values()) {
            String canonicalName = "C_" + value.name();
            BaseVariable v = new BaseVariable(
                    VariableBuilder.start()
                            .name(canonicalName)
                            .value(value.getKey())
                            .docs(JDoc.builder()
                                    .sinceVersion("6.1.0")

                                    .addDesc(value.getDoc())
                                    .addNote("[Continuous Function](../Continuous-Functions.md)")
                                    .addExample("""
                                            tsea "jaiva/continuous"!
                                            
                                            @ Provided "func" returns a continuous function.
                                            
                                            maak cont <- func()!
                                            cont(""" + canonicalName + ")()! @ Calls the returned function.")
                            )
            );
            if (value.getAliases().length > 0) {
                addWithAliases(v, value.getAliases());
            } else {
                add(v);
            }
        }
    }
}

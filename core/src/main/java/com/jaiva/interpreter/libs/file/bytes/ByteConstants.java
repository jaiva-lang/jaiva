package com.jaiva.interpreter.libs.file.bytes;

import com.jaiva.interpreter.libBuilders.var.VariableBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.yetnt.utils.tuple.Pair;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@JaivaLibrary(
        path = "file/bytes/const",
        description = "constants to input into the reader and writer"
)
public class ByteConstants extends BaseLibrary {

    private enum Values {
        INTEGER(true, true, "", "INT"),
        JAVA_UTF8(true, true, "", "MOD_UTF8"),
        UTF8(
                false,
                true,
                "This has no read variant due to the nature of not knowing how many bytes " +
                        "the entire UTF8 chunk could be. Your problem.", "STRING", "UTF_8"),
        DOUBLE(true),
        LONG(true),
        BOOLEAN(true, true, "", "BOOL"),
        BYTE(
                true,
                true,
                "Jaiva doesn't actually have a byte type, it's converted to an integer " +
                        "of range 0 to 255, so keep that in mind."),
        BYTES(
                true,
                false,
                "Attempts to read up to N remaining bytes. (You need to provide N)"
        );

        private final boolean readConstant;
        private final boolean writeConstant;
        private final String extraInfo;
        private final String[] aliases;

        Values(boolean both) {
            this.readConstant = both;
            this.writeConstant = both;
            extraInfo = "";
            aliases = new String[0];
        }

        public String[] getAliases() {
            return aliases;
        }

        Values(boolean readConstant, boolean writeConstant, String extraInfo, String ...aliases) {
            this.readConstant = readConstant;
            this.writeConstant = writeConstant;
            this.extraInfo = extraInfo;
            this.aliases = aliases;
        }

        public static ArrayList<Pair<Values, BaseVariable>> toVars() {
            ArrayList<Pair<Values, BaseVariable>> vars = new ArrayList<>();
            BiFunction<String, Values, BaseVariable> vMaker =
                    (prefix, value) -> new BaseVariable(
                            VariableBuilder.start()
                                    .name(prefix + "_" + value.name())
                                    .docs(JDoc.builder().sinceVersion("6.1.0")
                                            .addDesc(
                                                    "Continuous Function Input constant over file streams. "
                                                    + value.extraInfo
                                            ).addNote(
                                                    "This is for the " +
                                                            (prefix.startsWith("F_W") ? "write" : "read") +
                                                            " function."
                                            ))
                                    .value((prefix + "_" + value.name()).toLowerCase())
                    );

            for (Values v : Values.values()) {
                if (v.readConstant)
                    vars.add(new Pair<>(v, vMaker.apply("F_R", v)));
                if (v.writeConstant)
                    vars.add(new Pair<>(v, vMaker.apply("F_W", v)));
            }
            return vars;
        }

    }

    public ByteConstants() {
        ArrayList<Pair<Values, BaseVariable>> vars = Values.toVars();

        vars.forEach(pair -> {
           Values v = pair.getFirst();
           BaseVariable var = pair.getSecond();

           if (v.getAliases().length == 0) {
               add(var);
           } else {
               String[] aliases = new String[v.getAliases().length];
               for (int i = 0; i < aliases.length; i++) {
                   String alias = v.getAliases()[i];
                   aliases[i] = var.name.substring(0, 3) + "_" + alias;
               }
               addWithAliases(var, aliases);
           }
        });
    }
}

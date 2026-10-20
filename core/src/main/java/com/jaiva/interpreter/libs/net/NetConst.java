package com.jaiva.interpreter.libs.net;

import com.jaiva.interpreter.libBuilders.var.VariableBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.yetnt.utils.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiFunction;

@JaivaLibrary(
        path = "net/const",
        description = "constants to input into the reader and writer"
)
public class NetConst extends BaseLibrary {

    public enum TCPValues {

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
                true,
                "Attempts to read at most the amount of bytes currently available and returns that. this" +
                        " is tcp bro rather use TCP_RU_BYTES", "BYTEARR", "BYTEBUFF"
        ),
        U_BYTES(
                true,
                false,
                "This one's special since it'll wait and keep collecting bytes until" +
                        " the buffer has reached N bytes.", "BYTEARR", "BYTEBUFF"
        );

        private final boolean readConstant;
        private final boolean writeConstant;
        private final String extraInfo;
        private final String[] aliases;

        @Nullable
        public static TCPValues fromString(String s) {
            s = s.toLowerCase();
            for (TCPValues v : TCPValues.values()) {
                String name = v.name().toLowerCase();
                if (name.equalsIgnoreCase(s))
                    return v;

                if (s.contains("net_r")) {
                    if (!v.readConstant) continue;
                    if (s.contains("net_ru") && v != U_BYTES) continue;
                }

                if (s.contains("net_w"))
                    if (!v.writeConstant) continue;

                if (s.endsWith(name))
                    return v;

                for (String alias : v.getAliases()) {
                    if (s.endsWith(alias.toLowerCase())) return v;
                }
            }
            return null;
        }

        TCPValues(boolean both) {
            this.readConstant = both;
            this.writeConstant = both;
            extraInfo = "";
            aliases = new String[0];
        }

        public String[] getAliases() {
            return aliases;
        }

        TCPValues(boolean readConstant, boolean writeConstant, String extraInfo, String ...aliases) {
            this.readConstant = readConstant;
            this.writeConstant = writeConstant;
            this.extraInfo = extraInfo;
            this.aliases = aliases;
        }

        @NotNull
        public static ArrayList<Pair<TCPValues, BaseVariable>> toVars() {
            ArrayList<Pair<TCPValues, BaseVariable>> vars = new ArrayList<>();
            BiFunction<String, TCPValues, BaseVariable> vMaker =
                    (prefix, value) -> new BaseVariable(
                            VariableBuilder.start()
                                    .name(prefix + "_" +
                                            (value == U_BYTES
                                            ? value.name().substring(2)
                                            : value.name()))
                                    .docs(JDoc.builder().sinceVersion("6.2.0")
                                            .addDesc(
                                                    "Continuous Function Input constant over tcp client/server streams. "
                                                            + value.extraInfo
                                            ).addNote(
                                                    "This is for the " +
                                                            (prefix.contains("W_") ? "write" : "read") +
                                                            " function."
                                            ))
                                    .value((prefix + "_" + value.name()).toLowerCase())
                    );

            for (TCPValues v : TCPValues.values()) {
                if (v.readConstant) {
                    if (v != TCPValues.U_BYTES)
                        vars.add(new Pair<>(v, vMaker.apply("NET_R", v)));
                    else {
                        vars.add(new Pair<>(v, vMaker.apply("NET_RU", v)));
                    }
                }
                if (v.writeConstant)
                    vars.add(new Pair<>(v, vMaker.apply("NET_W", v)));
            }
            return vars;
        }

    }

    public NetConst() {
        ArrayList<Pair<TCPValues, BaseVariable>> vars = TCPValues.toVars();

        vars.forEach(pair -> {
            TCPValues v = pair.getFirst();
            BaseVariable var = pair.getSecond();

            if (v.getAliases().length == 0) {
                add(var);
            } else {
                String[] aliases = new String[v.getAliases().length];
                for (int i = 0; i < aliases.length; i++) {
                    String alias = v.getAliases()[i];
                    aliases[i] =
                            (var.name.contains("RU") ?
                                    "NET_RU"
                                    : var.name.substring(0,5)
                                    )
                                    + "_" + alias;
                }
                addWithAliases(var, aliases);
            }
        });
    }
}

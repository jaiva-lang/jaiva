package com.jaiva.interpreter.libs.net.interfaces;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.libs.types.TypeConverter;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.yetnt.utils.functional.function.ThrowableBiFunction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@JaivaLibrary(path = "net/interf/query", description = "instead of querying data yourself you can use these helper functions lol")
public class NetInterfsQuery extends BaseLibrary {

    interface ValueMapper extends ThrowableBiFunction<
            ArrayList<Object>,
            ThrowableBiFunction<ArrayList<Object>, Integer, ArrayList<ArrayList<Integer>>, InterpreterException>,
            ArrayList<String>,
            InterpreterException> {}

    enum Values {
        Name(
                0,
                "Gets the name of the network interface"
        ),
        DisplayName(
                1,
                "Get the display name of this network interface. usually a human readable string"
        ),
        AddrIpv4List(
                2,
                "Gets the list of Ipv4 IPs as byte arrays (or strings) associated with the interface",
                (Arguments a) ->
                        a.add(new AArgument("format", "Whether to format each byte array into the human readable IPv4 format", true, Argument.Type.BOOLEAN)),
                (ArrayList<Object> v4Addr, ThrowableBiFunction<ArrayList<Object>, Integer, ArrayList<ArrayList<Integer>>, InterpreterException> check) -> {
                    return check.apply(v4Addr, 4).stream().map(NetInterfsQuery::formatIPv4).collect(Collectors.toCollection(ArrayList::new));
                }

        ),
        AddrIpv6List(
                3,
                "Gets the list of Ipv6 IPs as byte arrays associated with the interface",
                (Arguments a) ->
                        a.add(new AArgument("format", "Whether to format each byte array into the human readable IPv6 format", true, Argument.Type.BOOLEAN)),
                (ArrayList<Object> v6Addr, ThrowableBiFunction<ArrayList<Object>, Integer, ArrayList<ArrayList<Integer>>, InterpreterException> check) -> {
                    return check.apply(v6Addr, 16).stream().map(NetInterfsQuery::formatIPv6).collect(Collectors.toCollection(ArrayList::new));
                }
        ),
        Active(
                4,
                "Gets the boolean indicating whether the interface is up and running"
        ),
        Loopback(
                5,
                "Gets the boolean indicating whether the interface is a loopback one."
        ),
        Virtual(
                6,
                "Gets the boolean indicating whether the interface is a virtual one. (or subinterface)"
        ),
        AddrMac(
                7,
                "gets the hardware address of this interface (The MAC Address) If there isn't one this returns idk"
        );

        final int i;
        final String desc;
        final Consumer<Arguments> extra;
        final ValueMapper checker;

        Values(int i, String desc) {
            this.i = i;
            this.desc = desc;
            extra = null;
            checker = null;
        }

        Values(int i, String desc, Consumer<Arguments> extra,  ValueMapper checker) {
            this.i = i;
            this.desc = desc;
            this.extra = extra;
            this.checker = checker;
        }
    }

    public NetInterfsQuery() {
        for (Values value : Values.values()) {
            String name = "net_i" + value.name();
            Arguments args =
                    Arguments.getInstance()
                            .add(new AArgument(
                                    "array",
                                    "The array which to index into and get information of. Represents a network interface",
                                    false,
                                    Argument.Type.ARRAY
                            ));
            if (value.extra != null)
                value.extra.accept(args);
            add(
                    new BaseFunction(
                            FunctionBuilder.start()
                                    .name(name)
                                    .setLib("net/interf/query")
                                    .arguments(args)
                                    .docs(JDoc.builder()
                                            .sinceVersion("6.2.0")
                                            .addDesc(value.desc)
                                            .addExample("""
                                                    tsea "jaiva/net/interf"!
                                                   \s
                                                    maak interfaces <- net_interfs()!
                                                    maak firstInterface <- interfaces[0]!
                                                    maak value <-\s""" + name + "(firstInterface)!")
                                    )
                    ) {
                        @Override
                        public Object call(CallProperties callProperties) throws Exception {
                            checkParams(callProperties.toCheckParams());

                            Object f = callProperties.params().getFirst();
                            if (!(f instanceof ArrayList<?> arr))
                                throw new InterpreterException.FunctionParametersException(
                                        callProperties.scope(),
                                        this, "1", f,
                                        ArrayList.class, callProperties.getLineNumber()
                                );

                            ArrayList<Object> array = (ArrayList<Object>)arr;

                            boolean format = false;
                            if (callProperties.params().size() > 1) {
                                Object t = callProperties.params().get(1);
                                if (!(t instanceof Boolean r))
                                    throw new InterpreterException.FunctionParametersException(
                                            callProperties.scope(),
                                            this, "2", t,
                                            Boolean.class, callProperties.getLineNumber()
                                    );
                                format = r;
                            }

                            Object returned;
                            try {
                                returned = array.get(value.i);
                            } catch (IndexOutOfBoundsException e) {
                                throw new InterpreterException.WtfAreYouDoingException(
                                        callProperties.scope(), "The given index was expected to a value... Are you sure this is a network interface array?", callProperties.getLineNumber()
                                );
                            }

                            if (value != Values.AddrIpv4List && value != Values.AddrIpv6List)
                                return returned;

                            if (!(returned instanceof ArrayList<?> byteArray))
                                throw new InterpreterException.WtfAreYouDoingException(
                                        callProperties.scope(), "The given index was expected to have an array of arrays... Are you sure this is a network interface array?", callProperties.getLineNumber()
                                );

                            if (value.checker != null && format) {
                                return value.checker.apply((ArrayList<Object>) byteArray, (arr2, intAmt) -> {
                                    ArrayList<ArrayList<Integer>> c = new ArrayList<>();
                                    for (Object con : byteArray) {
                                        if (!(con instanceof ArrayList<?> conv1))
                                            throw new InterpreterException.WtfAreYouDoingException(
                                                    callProperties.scope(), "The given input must be an array man", callProperties.getLineNumber()
                                            );

                                        ArrayList<Integer> conv2;
                                        try {
                                            conv2 = TypeConverter.validate((ArrayList<Object>) conv1);
                                        } catch (IllegalArgumentException e) {
                                            throw new InterpreterException.WtfAreYouDoingException(
                                                    callProperties.scope(), e.getMessage(), callProperties.getLineNumber()
                                            );
                                        }
                                        if (conv2.size() != intAmt)
                                            throw new InterpreterException.WtfAreYouDoingException(
                                                    callProperties.scope(), "idk how you got this far, but the length of the given arreay was suppsoed to be " + intAmt, callProperties.getLineNumber()
                                            );
                                        c.add(conv2);
                                    }
                                    return c;
                                });
                            } else {
                                return byteArray;
                            }
                        }
                    }
            );
        }
    }

    public static String formatIPv4(@NotNull ArrayList<Integer> parts) {
        return parts.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("."));
    }

    public static String formatIPv6(@NotNull ArrayList<Integer> bytes) {
        StringJoiner sj = new StringJoiner(":");

        // Group every 2 bytes together (e.g., bytes 0 and 1 make the first block)
        for (int i = 0; i < bytes.size(); i += 2) {
            int highByte = bytes.get(i);
            int lowByte = (i + 1 < bytes.size()) ? bytes.get(i + 1) : 0;

            // Combine them into a 16-bit short value
            int combined = (highByte << 8) | lowByte;

            // Format as lowercase hex (e.g., %04x forces 4 digits with leading zeros)
            sj.add(String.format("%04x", combined));
        }

        return sj.toString();
    }
}

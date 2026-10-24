package com.jaiva.interpreter.libs.net.interfaces;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.Exports;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.libs.types.TypeConverter;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.yetnt.utils.collection.buckets.Buckets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

@JaivaLibrary(path = "net/interf", description = "Library for working with network interfaces.")
@Exports(NetInterfsQuery.class)
public class NetInterfaces extends BaseLibrary {

    enum LocalIpConstValues {
        v4("127.0.0.1"),
        v6("fe80:0000:0000:0000:abb8:c8a7:6aa1:53ea");

        final String s;
        LocalIpConstValues(String v) {
            s = v;
        }
    }

    public NetInterfaces() {
        add(new FInterfaces());
        for (LocalIpConstValues v : LocalIpConstValues.values()) {
            String name = "IP" + v.name();
            String canonicalName = "net_localIp" + v.name();
            add(new BaseFunction(
                    FunctionBuilder.start()
                            .name(canonicalName)
                            .setLib("net/interf")
                            .docs(JDoc.builder()
                                    .addDesc("Returns your local " + name + " address")
                                    .sinceVersion("7.0.0")
                                    .addReturns("Your local IP Address formatted as a string.")

                            )
            ) {
                @Override
                public Object call(CallProperties callProperties) throws Exception {
                    return getLocalAddress(v == LocalIpConstValues.v6);
                }
            });
        }
    }

    @NotNull
    public static ArrayList<NetworkInterface>  getInterfaces() throws SocketException {
        return NetworkInterface.networkInterfaces()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Nullable
    public static String getLocalAddress(boolean ipv6) throws Exception {

        try (DatagramSocket socket = new DatagramSocket()) {
            if (ipv6) {
                socket.connect(
                        InetAddress.getByName("2001:4860:4860::8888"),
                        80
                );
            } else {
                socket.connect(
                        InetAddress.getByName("8.8.8.8"),
                        80
                );
            }
            InetAddress address = socket.getLocalAddress();
            return address != null
                    ? address.getHostAddress()
                    : null;
        }
    }

    static class FInterfaces extends BaseFunction {
        public FInterfaces() {
            super(FunctionBuilder.start().name("net_interfs")
                    .setLib("net/interf"));
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            ArrayList<NetworkInterface> ns = NetInterfaces.getInterfaces();
            ArrayList<Object> s = new ArrayList<>();

            try {
                for (NetworkInterface ni : ns) {
                    ArrayList<Object> stuff = new ArrayList<>();
                    ArrayList<InetAddress> addresses = Collections.list(ni.getInetAddresses());
                    ArrayList<ArrayList<ArrayList<Object>>> filt
                            = Buckets.of(addresses, Inet4Address.class, Inet6Address.class)
                            .stream()
                            .map(
                                    l ->
                                        l.stream()
                                                .map(InetAddress::getAddress)
                                                .map(t -> TypeConverter.asArr(TypeConverter.byteArrToIntArr(t)))
                                                .collect(Collectors.toCollection(ArrayList::new))
                            ).collect(Collectors.toCollection(ArrayList::new));

                    stuff.add(ni.getName());
                    stuff.add(ni.getDisplayName());
                    stuff.add(filt.getFirst());
                    stuff.add(filt.getLast());
                    stuff.add(ni.isUp());
                    stuff.add(ni.isLoopback());
                    stuff.add(ni.isVirtual());
                    stuff.add(
                            ni.getHardwareAddress() != null ?
                                    TypeConverter.asArr(TypeConverter.byteArrToIntArr(ni.getHardwareAddress()))
                                    : Token.voidValue(callProperties.getLineNumber())
                    );

                    s.add(stuff);
                }
            } catch (SocketException e) {
                throw new InterpreterException.NetException(callProperties.scope(), callProperties.getLineNumber(), e);
            }
            return s;
        }
    }
}

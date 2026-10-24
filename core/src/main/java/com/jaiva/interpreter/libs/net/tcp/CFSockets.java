package com.jaiva.interpreter.libs.net.tcp;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libs.net.NetConst;
import com.jaiva.interpreter.libs.types.TypeConverter;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.interpreter.symbol.continuous.CFClosures;
import com.jaiva.interpreter.symbol.continuous.ContinuousFunction;
import com.jaiva.interpreter.symbol.inf.BFMethodToConsumer;
import com.jaiva.interpreter.symbol.inf.BFMethodToSupplier;
import com.jaiva.tokenizer.tokens.Token;
import com.yetnt.utils.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class CFSockets {

    public static class WrappedCloseFunction extends BaseFunction {
        private final Socket socket;
        private final CFClosures.Closed closed;
        public WrappedCloseFunction(Socket socket, CFClosures.Closed closed) {
            super(FunctionBuilder.start().setLib("net/tcp"));
            freeze();
            this.socket = socket;
            this.closed = closed;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            closed.handleCallback(
                    new ArrayList<>(List.of(
                            socket.getLocalAddress().toString(),
                            socket.getRemoteSocketAddress().toString(),
                            DoneStatus.SELF.name()
                    ))
                    , callProperties);
            socket.close();
            return Token.voidValue(callProperties.getLineNumber());
        }
    }
    @NotNull
    @Contract(pure = true, value = "_,_,_->new")
    static Pair<WrappedCloseFunction, ContinuousFunction<DataInputStream, DataOutputStream>> newContinuousFunc(
            IConfig<Object> config, @NotNull Socket socket, ArrayList<Object> clientInfo
    ) throws IOException {
        CFClosures.EOF eofClosure = getEofClosure();
        CFClosures.Closed closedClosure = getClosedClosure();
        PushbackInputStream pbis = new PushbackInputStream(new BufferedInputStream(socket.getInputStream()));
        DataInputStream dis = new DataInputStream(pbis);
        DataOutputStream dos =  new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        ContinuousFunction<DataInputStream, DataOutputStream> cf = ContinuousFunction.from(
                config, dis, dos,
                getReadSupplier(pbis, dis, clientInfo, eofClosure, closedClosure, socket),
                getWriteConsumer(dos, clientInfo, closedClosure, socket),
                eofClosure,
                closedClosure,
                "net/tcp"
        );
        cf.getCloseClosure().addSocket(socket);
        return new Pair<>(new WrappedCloseFunction(socket, closedClosure), cf);
    }

    @NotNull
    @Contract(pure = true)
    private static BFMethodToConsumer<ArrayList<Object>> getWriteConsumer(DataOutputStream dos, ArrayList<Object> socketInfo, CFClosures.Closed closedClosure, Socket socket) {
        return callProperties -> {
            Object typeToRead = callProperties.params().getFirst();
            if (!(typeToRead instanceof String typeStr))
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(), "The input must be a string", callProperties.getLineNumber()
                );

            NetConst.TCPValues type = NetConst.TCPValues.fromString(typeStr);

            if (type == null)
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(), "The input must be a valid constant from the NET library", callProperties.getLineNumber()
                );

            Object val = callProperties.params().get(1);

            try {

                return switch (type) {
                    case INTEGER -> {
                            if (!(val instanceof Integer i2)) yield (o) -> {};
                            dos.writeInt(i2);
                            yield (o) -> {};
                    }
                    case BOOLEAN -> {
                        if (!(val instanceof Boolean i2)) yield (o) -> {};
                        dos.writeBoolean(i2);
                        yield (o) -> {};
                    }
                    case LONG -> {
                        if (!(val instanceof Long i2)) yield (o) -> {};
                        dos.writeLong(i2);
                        yield (o) -> {};
                    }
                    case DOUBLE -> {
                        if (!(val instanceof Double i2)) yield (o) -> {};
                        dos.writeDouble(i2);
                        yield (o) -> {};
                    }
                    case UTF8 -> {
                        if (!(val instanceof String i2)) yield (o) -> {};
                        dos.write(i2.getBytes(StandardCharsets.UTF_8));
                        yield (o) -> {};
                    }
                    case JAVA_UTF8 -> {
                        if (!(val instanceof String i2)) yield (o) -> {};
                        dos.writeUTF(i2);
                        yield (o) -> {};
                    }
                    case BYTE -> {
                        if (!(val instanceof Integer i2)) yield (o) -> {};
                        dos.writeDouble(TypeConverter.toByte(i2));
                        yield (o) -> {};
                    }
                    case BYTES -> {
                        if (!(val instanceof ArrayList i2)) yield (o) -> {};
                        byte[] b = TypeConverter.intArrToByteArr(i2); // does all conversions ad checks
                        dos.write(b);
                        yield (o) -> {};
                    }
                    default -> (o) -> {};
                };
            } catch (EOFException i) {
                // The connection dropped weirdly/abruptly mid-peek or mid-stream

                ArrayList<Object> props = new ArrayList<>(socketInfo);
                props.add(DoneStatus.ERR.name());
                closedClosure.handleCallback(props, callProperties);
                return (o) -> {};
            } catch (IOException i) {
                throw new InterpreterException.NetException(callProperties.scope(), callProperties.getLineNumber(), i);
            }
        };
    }

    @NotNull
    @Contract(pure = true)
    private static BFMethodToSupplier<?> getReadSupplier(PushbackInputStream pbis, DataInputStream dis, ArrayList<Object> clientInfo, CFClosures.EOF eofClosure, CFClosures.Closed closedClosure, Socket client) {
        return callProperties -> {
            Object typeToRead = callProperties.params().getFirst();
            if (!(typeToRead instanceof String typeStr))
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(), "The input must be a string", callProperties.getLineNumber()
                );

            NetConst.TCPValues type = NetConst.TCPValues.fromString(typeStr);

            if (type == null)
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(), "The input must be a valid constant from the NET library", callProperties.getLineNumber()
                );

            int len = -1;
            if (callProperties.params().size() > 1) {
                if (!(callProperties.params().get(1) instanceof Integer i))
                    throw new InterpreterException.WtfAreYouDoingException(
                            callProperties.scope(), "The input length must be an integer", callProperties.getLineNumber()
                    );
                if (i <= 0)
                    throw new InterpreterException.WtfAreYouDoingException(
                            callProperties.scope(), "...I must read " + i + " bytes huh?", callProperties.getLineNumber()
                    );
                len = i;
            }

            try {
                int peekByte = pbis.read();
                if (peekByte == -1) {
                    // closed properly.
                    ArrayList<Object> props = new ArrayList<>(clientInfo);
                    props.add(DoneStatus.INPUT.name());
                    closedClosure.handleCallback(props, callProperties);
                } else {
                    pbis.unread(peekByte);
                    try {
                        // code
                        switch (type) {
                            case BYTE -> {
                                int b = TypeConverter.fromByte(dis.readByte());
                                return () -> b;
                            }
                            case INTEGER -> {
                                int b = dis.readInt();
                                return () -> b;
                            }
                            case BOOLEAN -> {
                                boolean b = dis.readBoolean();
                                return () -> b;
                            }
                            case LONG -> {
                                long b = dis.readLong();
                                return () -> b;
                            }
                            case DOUBLE -> {
                                double b = dis.readDouble();
                                return () -> b;
                            }
                            case JAVA_UTF8 -> {
                                String s = dis.readUTF();
                                return () -> s;
                            }
                            // normal read
                            case BYTES -> {
                                if (len == -1) throw new InterpreterException.WtfAreYouDoingException(
                                        callProperties.scope(), "No input length was given.", callProperties.getLineNumber()
                                );
                                byte[] b = new byte[len];
                                int l = dis.read(b);
                                byte[] b2 = new byte[l];
                                System.arraycopy(b, 0, b2, 0, l);
                                return () -> b2; // User can just check the length to know how much was actually read.
                            }
                            // readfully variant
                            case U_BYTES -> {
                                if (len == -1) throw new InterpreterException.WtfAreYouDoingException(
                                        callProperties.scope(), "No input length was given.", callProperties.getLineNumber()
                                );
                                byte[] b = new byte[len];
                                dis.readFully(b);
                                return () -> b;
                            }
                            default -> {
                                if (type == NetConst.TCPValues.UTF8)
                                    throw new InterpreterException.WtfAreYouDoingException(
                                            callProperties.scope(), "You cant read a UTF8 value directly, read bytes instead.",
                                            callProperties.getLineNumber()
                                    );
                                return () -> Token.voidValue(callProperties.getLineNumber());
                            }
                        }
                    } catch (EOFException e) {
                        // eof was reached in the middle of read. Valid EOF.
                        eofClosure.handleCallback(clientInfo, callProperties);
                    }
                }
            } catch (EOFException e) {
                // The connection dropped weirdly/abruptly mid-peek or mid-stream
                ArrayList<Object> props = new ArrayList<>(clientInfo);
                props.add(DoneStatus.ERR.name());
                closedClosure.handleCallback(props, callProperties);
            } catch (IOException t) {
                throw new InterpreterException.NetException(callProperties.scope(), callProperties.getLineNumber(), t);
            }

            return () -> Token.voidValue(callProperties.getLineNumber());
        };
    }

    @NotNull
    @Contract(" -> new")
    private static CFClosures.Closed getClosedClosure() {
        return new CFClosures.Closed(
                ((callProperties, checkParams, cfClosureClosed) -> {
                    // We only expect a single parameter
                    checkParams.apply(callProperties.toCheckParams());

                    // it has to be a function
                    Object func2 = callProperties.params().getFirst();
                    if (!(func2 instanceof BaseFunction function))
                        throw new InterpreterException.FunctionParametersException(
                                callProperties.scope(), cfClosureClosed, "1", func2, BaseFunction.class,
                                callProperties.getLineNumber()
                        );

                    return function::call;
                }),
                "net/tcp"
        );
    }

    @NotNull
    @Contract(" -> new")
    private static CFClosures.EOF getEofClosure() {
        return new CFClosures.EOF(
                ((callProperties, checkParams, cfClosureEof) -> {
                    // We only expect a single parameter
                    checkParams.apply(callProperties.toCheckParams());

                    // it has to be a function
                    Object func2 = callProperties.params().getFirst();
                    if (!(func2 instanceof BaseFunction function))
                        throw new InterpreterException.FunctionParametersException(
                                callProperties.scope(), cfClosureEof, "1", func2, BaseFunction.class,
                                callProperties.getLineNumber()
                        );

                    return function::call;
                }), "net/tcp"
        );
    }
}

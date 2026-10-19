package com.jaiva.interpreter.symbol.continuous;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libs.types.TypeConverter;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.interpreter.symbol.inf.BFMethodComposition;
import com.jaiva.interpreter.symbol.inf.BFMethodToConsumer;
import com.jaiva.interpreter.symbol.inf.BFMethodToSupplier;
import com.jaiva.tokenizer.tokens.Token;
import com.yetnt.utils.functional.consumer.ThrowableConsumer;
import com.yetnt.utils.functional.function.ThrowableTriFunction;
import com.yetnt.utils.functional.generic.ThrowableSupplier;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CFClosures {

    public interface BFMethodWithExtras<T extends BaseFunction> extends ThrowableTriFunction<
                    CallProperties,
                    ThrowableConsumer<BaseFunction.CheckParams, InterpreterException>,
                    T,
                BFMethodComposition,
                    Exception> {}

    public static class EOF extends BaseFunction {
        private boolean eof = false;
        private BFMethodWithExtras<EOF> callbackInitializer = null;
        private BFMethodComposition callback = null;

        public EOF() {
            super(FunctionBuilder.start());
        }

        public EOF(BFMethodWithExtras<EOF> callbackInitializer) {
            super(FunctionBuilder.start());
            this.callbackInitializer = callbackInitializer;
        }

        public BFMethodWithExtras<EOF> getCallbackInitializer() {
            return callbackInitializer;
        }

        public BFMethodComposition getCallback() {
            return callback;
        }

        public void set() {
            eof = true;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            if (callbackInitializer != null && !callProperties.params().isEmpty()) {
                callback = callbackInitializer.apply(callProperties, this::checkParams, this);
                return Token.voidValue(callProperties.getLineNumber());
            } else {
                return eof;
            }
        }

        public boolean isEof() {
            return eof;
        }

        public void handleCallback(ArrayList<Object> params, CallProperties cp) throws Exception {
            if (isEof()) return;
            if (callback == null) return;
            eof = true;
            callback.apply(
                    params, cp.config(), cp.scope(), cp.tFuncCall()
            );
        }
    }

    public static class Input extends BaseFunction {
        BFMethodToSupplier<?> readStreamSupplier;

        public Input(BFMethodToSupplier<?> rss) {
            super(FunctionBuilder.start());
            this.readStreamSupplier = rss;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            // parse every object in params
            if (readStreamSupplier == null) return Token.voidValue(callProperties.getLineNumber());
            ArrayList<Object> parsedParams = new ArrayList<>();
            if (!callProperties.params().isEmpty() && (callProperties.params().size() != 1 || callProperties.params().getFirst() != null)) {
                for (Object p : callProperties.params()) {
                    parsedParams.add(Primitives.toPrimitive(p, false, callProperties.config(), callProperties.scope()));
                }
            }

            ThrowableSupplier<?, IOException> method
                    = readStreamSupplier.apply(callProperties);

            Object out = method.get();

            if (out instanceof Byte b) {
                return TypeConverter.fromByte(b);
            } else if (out instanceof byte[] b) {
                // most probably a byte array
                // return as integer list
                return TypeConverter.asArr(
                        TypeConverter.byteArrToIntArr(
                                b
                        ));
            } else {
                return out; // pray.
            }
        }
    }

    public static class Output extends BaseFunction {
        BFMethodToConsumer<ArrayList<Object>> writeStreamConsumer;

        public Output(BFMethodToConsumer<ArrayList<Object>> wsc) {
            super(FunctionBuilder.start());
            this.writeStreamConsumer = wsc;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            // parse every object in params
            if (writeStreamConsumer == null) return Token.voidValue(callProperties.getLineNumber());
            ArrayList<Object> parsedParams = new ArrayList<>();
            for (Object p : callProperties.params()) {
                parsedParams.add(Primitives.toPrimitive(p, false, callProperties.config(), callProperties.scope()));
            }

            ThrowableConsumer<ArrayList<Object>, IOException> method
                    = writeStreamConsumer.apply(callProperties);

            method.apply(parsedParams);
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    public static class Flush extends BaseFunction {
        Closeable cls;
        public Flush(Closeable cls) {
            super(FunctionBuilder.start());
            this.cls = cls;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            // we dont care about args.
            if (!(cls instanceof OutputStream os)) return Token.voidValue(callProperties.getLineNumber());
            os.flush();
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    public static class Close extends BaseFunction {
        UUID resourceId, secondResource;
        Socket socket = null;
        public Close(UUID resourceId) {
            super(FunctionBuilder.start());
            this.resourceId = resourceId;
            this.secondResource = null;
        }
        public Close(UUID inputId, UUID outputId) {
            super(FunctionBuilder.start());
            this.resourceId = inputId;
            this.secondResource = outputId;
        }

        public void addSocket(Socket socket) {
            this.socket = socket;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            if (socket != null) {
                // check for specific input
                if (!callProperties.params().isEmpty()) {
                    if (callProperties.params().getFirst() instanceof String str) {
                        // naturally, the user wont have the UUID
                        // so they can pass the CFuncInput type
                        if (!CFuncInput.isValid(str))
                            throw new InterpreterException.WtfAreYouDoingException(
                                    callProperties.scope(),
                                    "When specifying a resource to close, you can only put \"read\" or \"write\"",
                                    callProperties.getLineNumber()
                            );

                        CFuncInput funcInput = CFuncInput.fromChar(str.charAt(0));

                        assert funcInput != null; // it wont be null. cuz isValid. but what fucking ever.
                        callProperties.config().getGlobalResources().ofCurrentThread()
                                .releaseResource(switch (funcInput) {
                                    case READ -> {
                                        socket.shutdownInput();
                                        yield resourceId;
                                    }
                                    case WRITE -> {
                                        socket.shutdownOutput();
                                        yield secondResource;
                                    }
                                    default -> throw new InterpreterException.WtfAreYouDoingException(
                                            callProperties.scope(),
                                            "Input \"read\" or \"write\" not something else",
                                            callProperties.getLineNumber()
                                    );
                                });
                    }
                } else {
                    socket.close();
                    callProperties.config().getGlobalResources().ofCurrentThread().releaseResource(resourceId);
                    callProperties.config().getGlobalResources().ofCurrentThread().releaseResource(secondResource);
                }
            } else {
                callProperties.config().getGlobalResources().ofCurrentThread().releaseResource(resourceId);
            }
            return Token.voidValue(callProperties.getLineNumber());
        }
    }


    public static class Closed extends BaseFunction {
        private BFMethodWithExtras<Closed> callbackInitializer = null;
        private BFMethodComposition callback = null;
        boolean closed = false;

        public Closed() {
            super(FunctionBuilder.start());
        }

        public Closed(BFMethodWithExtras<Closed> callbackInitializer) {
            super(FunctionBuilder.start());
            this.callbackInitializer = callbackInitializer;
        }

        public BFMethodWithExtras<Closed> getCallbackInitializer() {
            return callbackInitializer;
        }

        public BFMethodComposition getCallback() {
            return callback;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            if (callbackInitializer != null &&  !callProperties.params().isEmpty()) {
                callback = callbackInitializer.apply(callProperties, this::checkParams, this);
                return Token.voidValue(callProperties.getLineNumber());
            }
            return isClosed();
        }

        public boolean isClosed() {
            return closed;
        }

        public void handleCallback(ArrayList<Object> params, CallProperties cp) throws Exception {
            if (isClosed()) return;
            if (callback == null) return;

            closed = true;
            callback.apply(
                    params, cp.config(), cp.scope(), cp.tFuncCall()
            );
        }
    }
}

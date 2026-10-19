package com.jaiva.interpreter.libs.net.tcp;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.interpreter.symbol.continuous.CFClosures;
import com.jaiva.interpreter.symbol.continuous.ContinuousFunction;
import com.jaiva.interpreter.symbol.inf.BFMethodToConsumer;
import com.jaiva.interpreter.symbol.inf.BFMethodToSupplier;
import com.jaiva.tokenizer.tokens.Token;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServSocketContainerLib extends BaseLibrary {
    public ServSocketContainerLib() {

    }

    static class WrappedAcceptFunction extends BaseFunction {
        private final ServerSocket serverSocket;
        public WrappedAcceptFunction(ServerSocket serverSocket) {
            super(FunctionBuilder.start());
            freeze();
            this.serverSocket = serverSocket;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            Socket s = serverSocket.accept();
            return new ArrayList<>(List.of(
                    newContinuousFunc(callProperties.config(), s),
                    s.getLocalAddress(),  // this should be a connection id but idk what exactly lmao.
                    s.getRemoteSocketAddress().toString()
            ));
        }
    }

    static class WrappedCloseFunction extends BaseFunction {
        private final ServerSocket serverSocket;
        public WrappedCloseFunction(ServerSocket serverSocket) {
            super(FunctionBuilder.start());
            freeze();
            this.serverSocket = serverSocket;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            serverSocket.close();
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    static class FTcpServe extends BaseFunction {
        public FTcpServe() {
            super(
                    // TODO: document
                    FunctionBuilder.start()
                            .name("tcp_serve")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(new AArgument("port", "The port", false, Argument.Type.NUMBER))
                                            .add(new AArgument("func",
                                                    "The server function to execute to start the server",
                                                    false, Argument.Type.FUNCTION))
                            )
            );
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {

            checkParams(callProperties.toCheckParams());

            Object p = callProperties.params().getFirst();
            if (!(p instanceof Integer port))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "1", p, Integer.class,
                        callProperties.getLineNumber()
                );
            Object func = callProperties.params().get(1);
            if (!(func instanceof BaseFunction function))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "2", p, BaseFunction.class,
                        callProperties.getLineNumber()
                );

            //  By here just pray the user is doing the proper stuff.

            // the usr's responsible.
            ServerSocket serverSocket = new ServerSocket(port);

            WrappedCloseFunction wcf = new WrappedCloseFunction(serverSocket);
            WrappedAcceptFunction waf = new WrappedAcceptFunction(serverSocket);

            try {
                function.call(
                        new ArrayList<>(List.of(waf, wcf)),
                        callProperties.config(),
                        callProperties.scope(),
                        callProperties.tFuncCall()
                );
            } catch (Exception e) {
                serverSocket.close();
                throw new RuntimeException(e);
            }

            return Token.voidValue(callProperties.getLineNumber());

        }
    }

    protected static ContinuousFunction<PushbackInputStream, DataOutputStream> newContinuousFunc(IConfig<Object> config, Socket client) throws IOException {
        CFClosures.EOF eofClosure = getEofClosure();
        CFClosures.Closed clsoedClosure = getClosedClosure();
        return ContinuousFunction.from(
                config,
                new PushbackInputStream(new DataInputStream(new BufferedInputStream(client.getInputStream()))),
                new DataOutputStream(new BufferedOutputStream(client.getOutputStream())),
                getReadSupplier(eofClosure, clsoedClosure, client),
                getWriteConsumer(clsoedClosure, client),
                eofClosure,
                clsoedClosure
        );
    }

    private static BFMethodToConsumer<Object> getWriteConsumer(CFClosures.Closed closedClosure, Socket client) {
        // TODO: functional handling and all that. Also if closed and the callback wasnt called. Call it
        return callProperties -> o -> {
            System.out.println("not implemented");
        };
    }

    private static BFMethodToSupplier<?> getReadSupplier(CFClosures.EOF eofClosure, CFClosures.Closed closedClosure, Socket client) {
        // TODO: functional handling and EOF vs Closed distinction via the PushBackInputStream. Same deal as above if either wasnt already set, call it.
        return callProperties -> () -> callProperties;
        /*
        Conceptually

            PushbackInputStream pbis = new PushbackInputStream(socket.getInputStream());

            // Inside the thread's evaluation loop:
            int peekByte = pbis.read();
            if (peekByte == -1)
                    // This is a clean closure. The remote peer closed the socket between evaluation cycles.
                    triggerClosedEvent();
            else
                    // Data is present. Push it back so the generic reader can consume it seamlessly.
                    pbis.unread(peekByte);

                    try
                            // THE FUNCTIONAL STUFF HERE
                    catch (EOFException e)
                            // If an EOF happens now, it IS a premature failure because we verified data existed.
                            triggerPrematureEOFError();
         */
    }

    @NotNull
    @Contract(" -> new")
    protected static CFClosures.Closed getClosedClosure() {
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

                    // TODO: The supplier and consumer have to pass the client porperties to this function call.
                    return function::call;
                })
        );
    }

    @NotNull
    @Contract(" -> new")
    protected static CFClosures.EOF getEofClosure() {
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

                    // TODO: The supplier and consumer have to pass the client porperties to this function call.
                    return function::call;
                })
        );
    }
}

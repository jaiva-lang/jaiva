package com.jaiva.interpreter.libs.net.tcp;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.tokens.Token;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServSocketContainerLib extends BaseLibrary {
    public ServSocketContainerLib() {
        add(new FTcpServe());
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
                    CFSockets.newContinuousFunc(
                            callProperties.config(), s,
                            new ArrayList<>(List.of(s.getLocalAddress().toString(), s.getRemoteSocketAddress().toString()))
                    ),
                    s.getLocalAddress().toString(),  // this should be a connection id but idk what exactly lmao.
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
}

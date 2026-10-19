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

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketContainerLib extends BaseLibrary {
    public SocketContainerLib() {
        add(new FTcpHandshake());
    }

    static class WrappedCloseFunction extends BaseFunction {
        private final Socket socket;
        public WrappedCloseFunction(Socket socket) {
            super(FunctionBuilder.start());
            freeze();
            this.socket = socket;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            socket.close();
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    static class FTcpHandshake extends BaseFunction {
        public FTcpHandshake() {
            super(
                    // TODO: document
                    FunctionBuilder.start()
                            .name("tcp_handshake")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(new AArgument("host", "The host address. .e.g \"localhost\"", false, Argument.Type.STRING))
                                            .add(new AArgument("port", "The port the server is on", false, Argument.Type.NUMBER))
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
            if (!(p instanceof String host))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "1", p, String.class,
                        callProperties.getLineNumber()
                );
            Object p2 = callProperties.params().get(1);
            if (!(p2 instanceof Integer port))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "2", p2, Integer.class,
                        callProperties.getLineNumber()
                );
            Object func = callProperties.params().get(2);
            if (!(func instanceof BaseFunction function))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "3", p, BaseFunction.class,
                        callProperties.getLineNumber()
                );

            // the usr's responsible.
            Socket socket = new Socket(host, port);

            WrappedCloseFunction wcf = new WrappedCloseFunction(socket);

            try {
                function.call(
                        new ArrayList<>(List.of(
                                CFSockets.newContinuousFunc(
                                        callProperties.config(),
                                        socket,
                                        new ArrayList<>(List.of(socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString()))
                                ),
                                socket.getLocalAddress().toString(),
                                socket.getRemoteSocketAddress().toString(),
                                wcf
                        )),
                        callProperties.config(),
                        callProperties.scope(),
                        callProperties.tFuncCall()
                );
            } catch (Exception e) {
                socket.close();
                throw e;
            }

            return Token.voidValue(callProperties.getLineNumber());

        }
    }
}

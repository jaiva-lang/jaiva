package com.jaiva.interpreter.libs.net.tcp;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.interpreter.symbol.continuous.ContinuousFunction;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.yetnt.utils.tuple.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketContainerLib extends BaseLibrary {

    private static String example = """
            tsea "jaiva/continuous"!
            tsea "jaiva/net"!
            tsea "jaiva/threads"!
            tsea "jaiva/types/numbers"!
            
            @* Send a packet through.
            @* p        $> con <- F~any The connection function
            @* p        $> msg <- string The message to send
            kwenza sendPacket(con, msg) ->
            	maak bArr <- t_strToByteArr(msg)!
            	con(C_WRITE)(NET_W_INT, bArr~)!
            	con(C_WRITE)(NET_W_BYTEBUFF, bArr)!
            	con(C_FLUSH)()!
            <~
            
            @* Reads an input tcp packet
            @* p        $> con <- F~any The connection function
            @* rets     $> A String.
            @* devn     $> This function essentially reads first an integer, then reads the next bytes of that
            @*          $> integer length expecting it as a UTF-8 String.
            kwenza readPacket(con) ->
            	maak msgLen <- con(C_READ)(NET_R_INT)!
            	maak pl <- con(C_READ)(NET_RU_BYTEBUFF, msgLen)!
            	@ Using NET_R_BYTEBUFF will only read whats available!
            	khutla t_strFromByteArr(pl:::)!
            <~
            
            
            @* Client function. Its the client itself.
            @* p        $> con <- F~any The connection function
            @* p        $> conAddress <- string The connection address
            @* p        $> conPort <- string The connection port
            @* p        $> close <- F~any close function which closes
            @*          $> this side of the socket when called.
            kwenza client(F~con, conAddress, conPort, F~close) ->
            
                maak conn_eof <- con(C_EOF)! @ EOF function to set calback and check
                maak conn_closed <- con(C_CLOSED)! @ Same thing as EOF
            
                @ Handle errs
                kwenza erHandler(_, _2, type) ->
                    @ we have conId and conAddress already.
                    maak identifier <- "Server on " + conAddress!
            
                    @ If its SELF, then this side was the one that closed via invoking the close() function
                    if (type != "SELF") ->
                        @ if i ever introduce a switch, itd naturally replace this.
                        sif (type = "CONNECTION") ?> khuluma(identifier + " was closed.")!
                        sif (type = "ERR") ?> khuluma(identifier + " crashe d suddenly!")!
                        sif (type = "INPUT") ?> khuluma(identifier + " closed it's input stream!")!
                        sif (type = "OUTPUT") ?> khuluma(identifier + " closed it's output stream!")!
                    <~
            
            	    close()! @ might as well close if this was invoked.
                <~
            
                conn_closed(erHandler)!
                conn_eof()! @ Rather use argument extensions here since its a long lambda.
                    |> f~(_, _2) : khuluma("EOF was reached while reading.... oh no.")!
            
                zama zama ->
                    @ read first packet
                    maak greet <- readPacket(con)!
            
                    khuluma("Received from server " + greet)!
                    khuluma()!
            
                    @ send to server
                    nikhil ((con(C_EOF)()') && (con(C_CLOSED)()')) ->
                        khuluma("Send to server: ", true)!
                        maak out <- mamela()! @ listen to da input blocking.
            
                        sif (out = "exit" || out~ = 0) ?> voetsek!
            
                        sendPacket(con, out)!
            
                        khuluma()!
                    <~
            	<~ chaai ->
                    @ if the connection closed, then this is a false alarm.
                    @ EOF is supposed to
                    sif (conn_closed() || conn_eof()) ?> khutla idk!
            	    khuluma("Connection to " + conAddress + " fucking died...: " + error)!
            	<~
            
            	khuluma("Closing communication with " + conAddress)!
            
            	@ tcp_handshake provides a convenience for this
                @ con(C_CLOSE)()!
            	@ with the close parameter.
            	close()!
            <~
            
            zama zama ->
                @ could use "localhost" but im testing whether this does return 10.0.0.17
            	tcp_handshake(net_localIpv4(), 5050, client)!
            <~ chaai ->
            	khuluma("Couldn't connect to server or some other error. " + error)!
            <~
            """;

    public SocketContainerLib() {
        add(new FTcpHandshake());
    }

    static class FTcpHandshake extends BaseFunction {
        public FTcpHandshake() {
            super(
                    // TODO: document
                    FunctionBuilder.start()
                            .name("tcp_handshake").setLib("net/tcp")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(new AArgument("host", "The host address. .e.g \"localhost\"", false, Argument.Type.STRING))
                                            .add(new AArgument("port", "The port the server is on", false, Argument.Type.NUMBER))
                                            .add(new AArgument("func",
                                                    "The server function to execute to start the server",
                                                    false, Argument.Type.FUNCTION))
                            )
                            .docs(JDoc.builder()
                                    .sinceVersion("7.0.0")
                                    .addExample(example)
                                    .addDesc("Creates a new socket and attempts to connect to the supplied" +
                                            " host and port.")
                                    .addNote("The input function is expected to take in 4 arguments, the first argument being" +
                                            " the connection function itself, the 2dn adn 3rd being the address and port, and the last being" +
                                            " the close function to close the socket.")
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

            Pair<
                    CFSockets.WrappedCloseFunction,
                    ContinuousFunction<DataInputStream, DataOutputStream>
                    > pair = CFSockets.newContinuousFunc(
                    callProperties.config(),
                    socket,
                    new ArrayList<>(List.of(socket.getLocalAddress().toString(), socket.getRemoteSocketAddress().toString()))
            );

            try {
                function.call(
                        new ArrayList<>(List.of(
                                pair.getSecond(),
                                socket.getLocalAddress().toString(),
                                socket.getRemoteSocketAddress().toString(),
                                pair.getFirst()
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

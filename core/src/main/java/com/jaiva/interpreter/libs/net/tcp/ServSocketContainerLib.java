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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServSocketContainerLib extends BaseLibrary {
    private static final String example = """
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
            
            @* Client function but server side.
            @* p        $> con <- F~any The connection function
            @* p        $> conAddress <- string The connection address
            @* p        $> conPort <- string The connection port
            @* p        $> close <- F~any close function which closes
            @*          $> this side of the socket when called.
            kwenza connectionHandler(F~con, conAddress, conPort, F~close) ->
                maak conn_eof <- con(C_EOF)! @ EOF function to set calback and check
                maak conn_closed <- con(C_CLOSED)! @ Same thing as EOF
            
                @* p    $> type <- string The type of the close call.
                @*      $> "SELF" indicates this own handler closed, "CONNECTION" is when the connection
                @*      $> closes gracefully. "INPUT"/"OUTPUT" means either of those from the client closed. "ERR" is what u expect
                kwenza closedHandler(_, _2, type) ->
                    khuluma("Socket was closed!")!
                    @ we have conPort and conAddress already.
                    maak identifier <- "$t|> " + conPort + " on " +  conAddress!
            
                    @ If its SELF, then this side was the one that close via invoking the close() function
                    if (type != "SELF") ->
                        @ if i ever introduce a switch, itd naturally replace this.
                        sif (type = "CONNECTION") ?> khuluma(identifier + " closed gracefully.")!
                        sif (type = "ERR") ?> khuluma(identifier + " crashed suddenly!")!
                        sif (type = "INPUT") ?> khuluma(identifier + " closed it's input stream!")!
                        sif (type = "OUTPUT") ?> khuluma(identifier + " closed it's output stream!")!
                    <~
            	    close()!
                <~
            
                @ Set the close callback
            	conn_closed(closedHandler)!
            
                @ Set the EOF callback
            	conn_eof()!
            		|> f~(_, _2) : khuluma(conAddress + " encountered EOF wile reading!")!
            
            	zama zama ->
            	    @ Send first packet to da client
            		sendPacket(con, "What's up bro")!
                    @ wait for any others.
            		nikhil ((conn_eof()') && (conn_closed()')) ->
            		    khuluma("Waiting for " + conAddress + " to send us a packet....")!
            			khuluma(" [RECEIVED FROM "+conAddress+":] " + readPacket(con))!
            		<~
            	<~ chaai ->
                    @ if the connection closed, then this is a false alarm.
                    if (conn_closed() || conn_eof()) ->
                        khutla idk!
                    <~ mara ->
                        @ client exited due to err
                        khuluma(conAddress + " "+ conPort + " disconnected weirdly..... " + error)!
            		<~
            	<~
            
            	khuluma("Closing communication with " + conAddress)!
            
            	close()!
            <~
            
            @* The server function
            @* p        $> acceptConn <- F~any The function which accepts connections.
            @*          $> This function will halt the thread (block) until a connection is made.
            @*          $> Once a connection is made, it returns a 4 element array. In particular
            @*          $> [F~connection, connectionAddress, connectionPort, F~closeFunction]
            @* devn     $> This function calls it's connectionHandler in a new thread since reading and writing
            @*          $> over the network in tcp is blocking on both ends. You cna handle this however but here it's
            @*          $> handled via threading each connection to its own thread.
            kwenza server(F~acceptConn, F~close) ->
                khuluma("Started server")!
                zama zama ->
                    khuluma("Waiting for connections...")!
                    nikhil (true;) ->
                        maak c <- acceptConn()! @ [F~connection, connectionAddress, connectionPort, F~closeFunction]
                        khuluma("Established connection from " + c[1])!
                        thread()!
                            |> f~() : connectionHandler(c:::)!
                            |> f~(status, msg) : khuluma("Client thread ended. " + status)!
                    <~
            	<~ chaai ->
            	    khuluma("Server experienced an error")!
            	    khuluma(error)!
            	    close()! @ close for good measure
            	<~
            <~
            
            tcp_serve(5050, server)!
            """;
    public ServSocketContainerLib() {
        add(new FTcpServe());
    }

    static class WrappedAcceptFunction extends BaseFunction {
        private final ServerSocket serverSocket;
        public WrappedAcceptFunction(ServerSocket serverSocket) {
            super(FunctionBuilder.start().setLib("net/tcp"));
            freeze();
            this.serverSocket = serverSocket;
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            Socket s = serverSocket.accept();
            Pair<
                    CFSockets.WrappedCloseFunction,
                    ContinuousFunction<DataInputStream, DataOutputStream>
                    > pair = CFSockets.newContinuousFunc(
                    callProperties.config(),
                    s,
                    new ArrayList<>(List.of(
                            s.getRemoteSocketAddress().toString(),
                            ((InetSocketAddress)s.getRemoteSocketAddress()).getPort()
                    ))
            );
            return new ArrayList<>(List.of(
                    pair.getSecond(),
                    s.getRemoteSocketAddress().toString(),
                    ((InetSocketAddress)s.getRemoteSocketAddress()).getPort(),
                    pair.getFirst()
            ));
        }
    }

    static class ServerWrappedCloseFunction extends BaseFunction {
        private final ServerSocket serverSocket;
        public ServerWrappedCloseFunction(ServerSocket serverSocket) {
            super(FunctionBuilder.start().setLib("net/tcp"));
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
                    FunctionBuilder.start()
                            .name("tcp_serve").setLib("net/tcp")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(new AArgument("port", "The port", false, Argument.Type.NUMBER))
                                            .add(new AArgument("func",
                                                    "The function to take in, which should take a minimum of 2 arguments.",
                                                    false, Argument.Type.FUNCTION))
                            )
                            .docs(JDoc.builder()
                                    .sinceVersion("7.0.0")
                                    .addExample(example)
                                    .addDesc("Creates a new server socket at the specific port.")
                                    .addNote("The input function is expected to take in 2 arguments, the first argument being" +
                                            " another function for accepting new connections and the second to close the server itself.")
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

            ServerWrappedCloseFunction wcf = new ServerWrappedCloseFunction(serverSocket);
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

# net/tcp (Library)

_Holds TCP related shiz yknow._
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| tcp_serve | [tcp_serve](#tcp_serve) |
| tcp_handshake | [tcp_handshake](#tcp_handshake) |
## Functions

### tcp_serve

This symbol can be reached by the following aliases: _`tcp_serve`_

_**Creates a new server socket at the specific port.**_


#### Definition

```jaiva
F~tcp_serve(port, func)
```

- **_port_**  **`<-`** _**number**_
	 - _The port_
- **_func_**  **`<-`** _**F~any**_
	 - _The function to take in, which should take a minimum of 2 arguments._

> [!NOTE]
> _The input function is expected to take in 2 arguments, the first argument being another function for accepting new connections and the second to close the server itself._


Since Version: _7.0.0_


#### Example: 

```jaiva
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
```


---


### tcp_handshake

This symbol can be reached by the following aliases: _`tcp_handshake`_

_**Creates a new socket and attempts to connect to the supplied host and port.**_


#### Definition

```jaiva
F~tcp_handshake(host, port, func)
```

- **_host_**  **`<-`** _**string**_
	 - _The host address. .e.g "localhost"_
- **_port_**  **`<-`** _**number**_
	 - _The port the server is on_
- **_func_**  **`<-`** _**F~any**_
	 - _The server function to execute to start the server_

> [!NOTE]
> _The input function is expected to take in 4 arguments, the first argument being the connection function itself, the 2dn adn 3rd being the address and port, and the last being the close function to close the socket._


Since Version: _7.0.0_


#### Example: 

```jaiva
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
```


---

## Variables

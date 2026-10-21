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


#### Definition

```jaiva
F~tcp_serve(port, func)
```

- **_port_**  **`<-`** _**number**_
	 - _The port_
- **_func_**  **`<-`** _**F~any**_
	 - _The server function to execute to start the server_

---


### tcp_handshake

This symbol can be reached by the following aliases: _`tcp_handshake`_


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

---

## Variables

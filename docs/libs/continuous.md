# continuous (Library)

_Contains constants for providing input into continuous functions. A continuous function, is one which closes over some form of system resources whether it be a file or some networking socket. See [Continuous Function](../Continuous-Functions.md)_
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| C_READ | [C_READ](#C_READ) |
| C_INPUT | [C_INPUT](#C_READ) |
| C_WRITE | [C_WRITE](#C_WRITE) |
| C_OUTPUT | [C_OUTPUT](#C_WRITE) |
| C_FLUSH | [C_FLUSH](#C_FLUSH) |
| C_EOF | [C_EOF](#C_EOF) |
| C_END_OF_FILE | [C_END_OF_FILE](#C_EOF) |
| C_CLOSE | [C_CLOSE](#C_CLOSE) |
| C_END | [C_END](#C_CLOSE) |
| C_FINISH | [C_FINISH](#C_CLOSE) |
## Functions
## Variables

### C_READ

This symbol can be reached by the following aliases: _`C_READ`_, _`C_INPUT`_

_**Key of the reader function if this continuous function closes over a read/input operation**_


#### Definition
```jaiva
maak C_READ <- (string)
```

> [!NOTE]
> _[Continuous Function](../Continuous-Functions.md)_


Since Version: _6.1.0_


#### Example: 

```jaiva
tsea "jaiva/continuous"!

@ Provided "func" returns a continuous function.

maak cont <- func()!
cont(C_READ)()! @ Calls the returned function.
```


---


### C_WRITE

This symbol can be reached by the following aliases: _`C_WRITE`_, _`C_OUTPUT`_

_**Key of the output function if this continuous function closes over a write/output operation**_


#### Definition
```jaiva
maak C_WRITE <- (string)
```

> [!NOTE]
> _[Continuous Function](../Continuous-Functions.md)_


Since Version: _6.1.0_


#### Example: 

```jaiva
tsea "jaiva/continuous"!

@ Provided "func" returns a continuous function.

maak cont <- func()!
cont(C_WRITE)()! @ Calls the returned function.
```


---


### C_FLUSH

This symbol can be reached by the following aliases: _`C_FLUSH`_

_**(Only associated with continuous output functions) Key of the function which flushes the output if the stream can be flushed (In Java terms, is Buffered.)**_


#### Definition
```jaiva
maak C_FLUSH <- (string)
```

> [!NOTE]
> _[Continuous Function](../Continuous-Functions.md)_


Since Version: _6.1.0_


#### Example: 

```jaiva
tsea "jaiva/continuous"!

@ Provided "func" returns a continuous function.

maak cont <- func()!
cont(C_FLUSH)()! @ Calls the returned function.
```


---


### C_EOF

This symbol can be reached by the following aliases: _`C_EOF`_, _`C_END_OF_FILE`_

_**(Only associated with continuous input functions) Key of the function which signals that an input continuous function has reached the end of it's data. Specific semantics depend on implementations as not all input continuous functions may make use of this.**_


#### Definition
```jaiva
maak C_EOF <- (string)
```

> [!NOTE]
> _[Continuous Function](../Continuous-Functions.md)_


Since Version: _6.1.0_


#### Example: 

```jaiva
tsea "jaiva/continuous"!

@ Provided "func" returns a continuous function.

maak cont <- func()!
cont(C_EOF)()! @ Calls the returned function.
```


---


### C_CLOSE

This symbol can be reached by the following aliases: _`C_CLOSE`_, _`C_END`_, _`C_FINISH`_

_**Key of the function which closes the continuous function, effectively ending it and freeing resources. This always needs to be called to avoid resource leaks.**_


#### Definition
```jaiva
maak C_CLOSE <- (string)
```

> [!NOTE]
> _[Continuous Function](../Continuous-Functions.md)_


Since Version: _6.1.0_


#### Example: 

```jaiva
tsea "jaiva/continuous"!

@ Provided "func" returns a continuous function.

maak cont <- func()!
cont(C_CLOSE)()! @ Calls the returned function.
```


---


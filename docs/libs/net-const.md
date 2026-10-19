# net/const (Library)

_constants to input into the reader and writer_
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| NET_RU_BYTES | [NET_RU_BYTES](#NET_RU_BYTES) |
| NET_RU_BYTEARR | [NET_RU_BYTEARR](#NET_RU_BYTES) |
| NET_RU_BYTEBUFF | [NET_RU_BYTEBUFF](#NET_RU_BYTES) |
| NET_R_INTEGER | [NET_R_INTEGER](#NET_R_INTEGER) |
| NET_R_INT | [NET_R_INT](#NET_R_INTEGER) |
| NET_W_INTEGER | [NET_W_INTEGER](#NET_W_INTEGER) |
| NET_W_INT | [NET_W_INT](#NET_W_INTEGER) |
| NET_R_JAVA_UTF8 | [NET_R_JAVA_UTF8](#NET_R_JAVA_UTF8) |
| NET_R_MOD_UTF8 | [NET_R_MOD_UTF8](#NET_R_JAVA_UTF8) |
| NET_W_JAVA_UTF8 | [NET_W_JAVA_UTF8](#NET_W_JAVA_UTF8) |
| NET_W_MOD_UTF8 | [NET_W_MOD_UTF8](#NET_W_JAVA_UTF8) |
| NET_W_UTF8 | [NET_W_UTF8](#NET_W_UTF8) |
| NET_W_STRING | [NET_W_STRING](#NET_W_UTF8) |
| NET_W_UTF_8 | [NET_W_UTF_8](#NET_W_UTF8) |
| NET_R_DOUBLE | [NET_R_DOUBLE](#NET_R_DOUBLE) |
| NET_W_DOUBLE | [NET_W_DOUBLE](#NET_W_DOUBLE) |
| NET_R_LONG | [NET_R_LONG](#NET_R_LONG) |
| NET_W_LONG | [NET_W_LONG](#NET_W_LONG) |
| NET_R_BOOLEAN | [NET_R_BOOLEAN](#NET_R_BOOLEAN) |
| NET_R_BOOL | [NET_R_BOOL](#NET_R_BOOLEAN) |
| NET_W_BOOLEAN | [NET_W_BOOLEAN](#NET_W_BOOLEAN) |
| NET_W_BOOL | [NET_W_BOOL](#NET_W_BOOLEAN) |
| NET_R_BYTE | [NET_R_BYTE](#NET_R_BYTE) |
| NET_W_BYTE | [NET_W_BYTE](#NET_W_BYTE) |
| NET_R_BYTES | [NET_R_BYTES](#NET_R_BYTES) |
| NET_R_BYTEARR | [NET_R_BYTEARR](#NET_R_BYTES) |
| NET_R_BYTEBUFF | [NET_R_BYTEBUFF](#NET_R_BYTES) |
| NET_W_BYTES | [NET_W_BYTES](#NET_W_BYTES) |
| NET_W_BYTEARR | [NET_W_BYTEARR](#NET_W_BYTES) |
| NET_W_BYTEBUFF | [NET_W_BYTEBUFF](#NET_W_BYTES) |
## Functions
## Variables

### NET_RU_BYTES

This symbol can be reached by the following aliases: _`NET_RU_BYTES`_, _`NET_RU_BYTEARR`_, _`NET_RU_BYTEBUFF`_

_**Continuous Function Input constant over tcp client/server streams. This one's special since it'll wait and keep collecting bytes until the buffer has reached N bytes.**_


#### Definition
```jaiva
maak NET_RU_BYTES <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_R_INTEGER

This symbol can be reached by the following aliases: _`NET_R_INTEGER`_, _`NET_R_INT`_

_**Continuous Function Input constant over tcp client/server streams.**_


#### Definition
```jaiva
maak NET_R_INTEGER <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_W_INTEGER

This symbol can be reached by the following aliases: _`NET_W_INTEGER`_, _`NET_W_INT`_

_**Continuous Function Input constant over tcp client/server streams.**_


#### Definition
```jaiva
maak NET_W_INTEGER <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_R_JAVA_UTF8

This symbol can be reached by the following aliases: _`NET_R_JAVA_UTF8`_, _`NET_R_MOD_UTF8`_

_**Continuous Function Input constant over tcp client/server streams.**_


#### Definition
```jaiva
maak NET_R_JAVA_UTF8 <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_W_JAVA_UTF8

This symbol can be reached by the following aliases: _`NET_W_JAVA_UTF8`_, _`NET_W_MOD_UTF8`_

_**Continuous Function Input constant over tcp client/server streams.**_


#### Definition
```jaiva
maak NET_W_JAVA_UTF8 <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_W_UTF8

This symbol can be reached by the following aliases: _`NET_W_UTF8`_, _`NET_W_STRING`_, _`NET_W_UTF_8`_

_**Continuous Function Input constant over tcp client/server streams. This has no read variant due to the nature of not knowing how many bytes the entire UTF8 chunk could be. Your problem.**_


#### Definition
```jaiva
maak NET_W_UTF8 <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_R_DOUBLE

This symbol can be reached by the following aliases: _`NET_R_DOUBLE`_

_**Continuous Function Input constant over tcp client/server streams.**_


#### Definition
```jaiva
maak NET_R_DOUBLE <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_W_DOUBLE

This symbol can be reached by the following aliases: _`NET_W_DOUBLE`_

_**Continuous Function Input constant over tcp client/server streams.**_


#### Definition
```jaiva
maak NET_W_DOUBLE <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_R_LONG

This symbol can be reached by the following aliases: _`NET_R_LONG`_

_**Continuous Function Input constant over tcp client/server streams.**_


#### Definition
```jaiva
maak NET_R_LONG <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_W_LONG

This symbol can be reached by the following aliases: _`NET_W_LONG`_

_**Continuous Function Input constant over tcp client/server streams.**_


#### Definition
```jaiva
maak NET_W_LONG <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_R_BOOLEAN

This symbol can be reached by the following aliases: _`NET_R_BOOLEAN`_, _`NET_R_BOOL`_

_**Continuous Function Input constant over tcp client/server streams.**_


#### Definition
```jaiva
maak NET_R_BOOLEAN <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_W_BOOLEAN

This symbol can be reached by the following aliases: _`NET_W_BOOLEAN`_, _`NET_W_BOOL`_

_**Continuous Function Input constant over tcp client/server streams.**_


#### Definition
```jaiva
maak NET_W_BOOLEAN <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_R_BYTE

This symbol can be reached by the following aliases: _`NET_R_BYTE`_

_**Continuous Function Input constant over tcp client/server streams. Jaiva doesn't actually have a byte type, it's converted to an integer of range 0 to 255, so keep that in mind.**_


#### Definition
```jaiva
maak NET_R_BYTE <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_W_BYTE

This symbol can be reached by the following aliases: _`NET_W_BYTE`_

_**Continuous Function Input constant over tcp client/server streams. Jaiva doesn't actually have a byte type, it's converted to an integer of range 0 to 255, so keep that in mind.**_


#### Definition
```jaiva
maak NET_W_BYTE <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_R_BYTES

This symbol can be reached by the following aliases: _`NET_R_BYTES`_, _`NET_R_BYTEARR`_, _`NET_R_BYTEBUFF`_

_**Continuous Function Input constant over tcp client/server streams. Attempts to read at most the amount of bytes currently available and returns that. this is tcp bro rather use NET_RU_BYTES**_


#### Definition
```jaiva
maak NET_R_BYTES <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


### NET_W_BYTES

This symbol can be reached by the following aliases: _`NET_W_BYTES`_, _`NET_W_BYTEARR`_, _`NET_W_BYTEBUFF`_

_**Continuous Function Input constant over tcp client/server streams. Attempts to read at most the amount of bytes currently available and returns that. this is tcp bro rather use NET_RU_BYTES**_


#### Definition
```jaiva
maak NET_W_BYTES <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.2.0_


---


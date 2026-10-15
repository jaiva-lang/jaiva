# file/bytes/const (Library)

_constants to input into the reader and writer_
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| R_INTEGER | [R_INTEGER](#R_INTEGER) |
| R_INT | [R_INT](#R_INTEGER) |
| W_INTEGER | [W_INTEGER](#W_INTEGER) |
| W_INT | [W_INT](#W_INTEGER) |
| R_JAVA_UTF8 | [R_JAVA_UTF8](#R_JAVA_UTF8) |
| R_MOD_UTF8 | [R_MOD_UTF8](#R_JAVA_UTF8) |
| W_JAVA_UTF8 | [W_JAVA_UTF8](#W_JAVA_UTF8) |
| W_MOD_UTF8 | [W_MOD_UTF8](#W_JAVA_UTF8) |
| W_UTF8 | [W_UTF8](#W_UTF8) |
| W_STRING | [W_STRING](#W_UTF8) |
| W_UTF_8 | [W_UTF_8](#W_UTF8) |
| R_DOUBLE | [R_DOUBLE](#R_DOUBLE) |
| W_DOUBLE | [W_DOUBLE](#W_DOUBLE) |
| R_LONG | [R_LONG](#R_LONG) |
| W_LONG | [W_LONG](#W_LONG) |
| R_BOOLEAN | [R_BOOLEAN](#R_BOOLEAN) |
| R_BOOL | [R_BOOL](#R_BOOLEAN) |
| W_BOOLEAN | [W_BOOLEAN](#W_BOOLEAN) |
| W_BOOL | [W_BOOL](#W_BOOLEAN) |
| R_BYTE | [R_BYTE](#R_BYTE) |
| W_BYTE | [W_BYTE](#W_BYTE) |
| R_BYTES | [R_BYTES](#R_BYTES) |
## Functions
## Variables

### R_INTEGER

This symbol can be reached by the following aliases: _`R_INTEGER`_, _`R_INT`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak R_INTEGER <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### W_INTEGER

This symbol can be reached by the following aliases: _`W_INTEGER`_, _`W_INT`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak W_INTEGER <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### R_JAVA_UTF8

This symbol can be reached by the following aliases: _`R_JAVA_UTF8`_, _`R_MOD_UTF8`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak R_JAVA_UTF8 <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### W_JAVA_UTF8

This symbol can be reached by the following aliases: _`W_JAVA_UTF8`_, _`W_MOD_UTF8`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak W_JAVA_UTF8 <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### W_UTF8

This symbol can be reached by the following aliases: _`W_UTF8`_, _`W_STRING`_, _`W_UTF_8`_

_**Continuous Function Input constant over file streams. This has no read variant due to the nature of not knowing how many bytes the entire UTF8 chunk could be. Your problem.**_


#### Definition
```jaiva
maak W_UTF8 <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### R_DOUBLE

This symbol can be reached by the following aliases: _`R_DOUBLE`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak R_DOUBLE <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### W_DOUBLE

This symbol can be reached by the following aliases: _`W_DOUBLE`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak W_DOUBLE <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### R_LONG

This symbol can be reached by the following aliases: _`R_LONG`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak R_LONG <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### W_LONG

This symbol can be reached by the following aliases: _`W_LONG`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak W_LONG <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### R_BOOLEAN

This symbol can be reached by the following aliases: _`R_BOOLEAN`_, _`R_BOOL`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak R_BOOLEAN <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### W_BOOLEAN

This symbol can be reached by the following aliases: _`W_BOOLEAN`_, _`W_BOOL`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak W_BOOLEAN <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### R_BYTE

This symbol can be reached by the following aliases: _`R_BYTE`_

_**Continuous Function Input constant over file streams. Jaiva doesn't actually have a byte type, it's converted to an integer of range 0 to 255, so keep that in mind.**_


#### Definition
```jaiva
maak R_BYTE <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### W_BYTE

This symbol can be reached by the following aliases: _`W_BYTE`_

_**Continuous Function Input constant over file streams. Jaiva doesn't actually have a byte type, it's converted to an integer of range 0 to 255, so keep that in mind.**_


#### Definition
```jaiva
maak W_BYTE <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### R_BYTES

This symbol can be reached by the following aliases: _`R_BYTES`_

_**Continuous Function Input constant over file streams. Attempts to read up to N remaining bytes. (You need to provide N)**_


#### Definition
```jaiva
maak R_BYTES <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


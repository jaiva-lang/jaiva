# file/bytes/const (Library)

_constants to input into the reader and writer_
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| F_R_INTEGER | [F_R_INTEGER](#F_R_INTEGER) |
| F_R_INT | [F_R_INT](#F_R_INTEGER) |
| F_W_INTEGER | [F_W_INTEGER](#F_W_INTEGER) |
| F_W_INT | [F_W_INT](#F_W_INTEGER) |
| F_R_JAVA_UTF8 | [F_R_JAVA_UTF8](#F_R_JAVA_UTF8) |
| F_R_MOD_UTF8 | [F_R_MOD_UTF8](#F_R_JAVA_UTF8) |
| F_W_JAVA_UTF8 | [F_W_JAVA_UTF8](#F_W_JAVA_UTF8) |
| F_W_MOD_UTF8 | [F_W_MOD_UTF8](#F_W_JAVA_UTF8) |
| F_W_UTF8 | [F_W_UTF8](#F_W_UTF8) |
| F_W_STRING | [F_W_STRING](#F_W_UTF8) |
| F_W_UTF_8 | [F_W_UTF_8](#F_W_UTF8) |
| F_R_DOUBLE | [F_R_DOUBLE](#F_R_DOUBLE) |
| F_W_DOUBLE | [F_W_DOUBLE](#F_W_DOUBLE) |
| F_R_LONG | [F_R_LONG](#F_R_LONG) |
| F_W_LONG | [F_W_LONG](#F_W_LONG) |
| F_R_BOOLEAN | [F_R_BOOLEAN](#F_R_BOOLEAN) |
| F_R_BOOL | [F_R_BOOL](#F_R_BOOLEAN) |
| F_W_BOOLEAN | [F_W_BOOLEAN](#F_W_BOOLEAN) |
| F_W_BOOL | [F_W_BOOL](#F_W_BOOLEAN) |
| F_R_BYTE | [F_R_BYTE](#F_R_BYTE) |
| F_W_BYTE | [F_W_BYTE](#F_W_BYTE) |
| F_R_BYTES | [F_R_BYTES](#F_R_BYTES) |
## Functions
## Variables

### F_R_INTEGER

This symbol can be reached by the following aliases: _`F_R_INTEGER`_, _`F_R_INT`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak F_R_INTEGER <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### F_W_INTEGER

This symbol can be reached by the following aliases: _`F_W_INTEGER`_, _`F_W_INT`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak F_W_INTEGER <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### F_R_JAVA_UTF8

This symbol can be reached by the following aliases: _`F_R_JAVA_UTF8`_, _`F_R_MOD_UTF8`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak F_R_JAVA_UTF8 <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### F_W_JAVA_UTF8

This symbol can be reached by the following aliases: _`F_W_JAVA_UTF8`_, _`F_W_MOD_UTF8`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak F_W_JAVA_UTF8 <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### F_W_UTF8

This symbol can be reached by the following aliases: _`F_W_UTF8`_, _`F_W_STRING`_, _`F_W_UTF_8`_

_**Continuous Function Input constant over file streams. This has no read variant due to the nature of not knowing how many bytes the entire UTF8 chunk could be. Your problem.**_


#### Definition
```jaiva
maak F_W_UTF8 <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### F_R_DOUBLE

This symbol can be reached by the following aliases: _`F_R_DOUBLE`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak F_R_DOUBLE <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### F_W_DOUBLE

This symbol can be reached by the following aliases: _`F_W_DOUBLE`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak F_W_DOUBLE <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### F_R_LONG

This symbol can be reached by the following aliases: _`F_R_LONG`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak F_R_LONG <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### F_W_LONG

This symbol can be reached by the following aliases: _`F_W_LONG`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak F_W_LONG <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### F_R_BOOLEAN

This symbol can be reached by the following aliases: _`F_R_BOOLEAN`_, _`F_R_BOOL`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak F_R_BOOLEAN <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### F_W_BOOLEAN

This symbol can be reached by the following aliases: _`F_W_BOOLEAN`_, _`F_W_BOOL`_

_**Continuous Function Input constant over file streams.**_


#### Definition
```jaiva
maak F_W_BOOLEAN <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### F_R_BYTE

This symbol can be reached by the following aliases: _`F_R_BYTE`_

_**Continuous Function Input constant over file streams. Jaiva doesn't actually have a byte type, it's converted to an integer of range 0 to 255, so keep that in mind.**_


#### Definition
```jaiva
maak F_R_BYTE <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


### F_W_BYTE

This symbol can be reached by the following aliases: _`F_W_BYTE`_

_**Continuous Function Input constant over file streams. Jaiva doesn't actually have a byte type, it's converted to an integer of range 0 to 255, so keep that in mind.**_


#### Definition
```jaiva
maak F_W_BYTE <- (string)
```

> [!NOTE]
> _This is for the write function._


Since Version: _6.1.0_


---


### F_R_BYTES

This symbol can be reached by the following aliases: _`F_R_BYTES`_

_**Continuous Function Input constant over file streams. Attempts to read up to N remaining bytes. (You need to provide N)**_


#### Definition
```jaiva
maak F_R_BYTES <- (string)
```

> [!NOTE]
> _This is for the read function._


Since Version: _6.1.0_


---


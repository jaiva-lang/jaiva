# file/bytes (Library)

_Allows byte level reading and writing of files onto disk. This makes use of continuous functions to do so._
## Exports
This library exports the following libraries: 
- file/bytes/const

Exported Symbols Include: `F_R_INTEGER`, `F_R_INT`, `F_W_INTEGER`, `F_W_INT`, `F_R_JAVA_UTF8`, `F_R_MOD_UTF8`, `F_W_JAVA_UTF8`, `F_W_MOD_UTF8`, `F_W_UTF8`, `F_W_STRING`, `F_W_UTF_8`, `F_R_DOUBLE`, `F_W_DOUBLE`, `F_R_LONG`, `F_W_LONG`, `F_R_BOOLEAN`, `F_R_BOOL`, `F_W_BOOLEAN`, `F_W_BOOL`, `F_R_BYTE`, `F_W_BYTE`, `F_R_BYTES`

## Table of Contents
| Alias | Link |
| --- | --- |
| f_creader | [f_creader](#f_creader) |
| f_cwriter | [f_cwriter](#f_cwriter) |
## Functions

### f_creader

This symbol can be reached by the following aliases: _`f_creader`_

_**Provides byte level reads into a file.**_


#### Definition

```jaiva
F~f_creader(filePath)
```
- **_filePath_**  **`<-`** _**string**_
	 - _Absolute path_

_**Returns:**_ **Returns a [Continuous Function](../Continuous-Functions.md) over the file reading resource.**

Since Version: _6.1.0_


#### Example: 

```jaiva
tsea "jaiva/file/bytes"!
tsea "jaiva/continuous!"
tsea "jaiva/types/numbers"!

@ If the file contains a UTF8 string then a 255 byte value

maak conf <- f_creader("C:\Users\Wow\myFile.jib")!
maak read <- conf(C_READ)!
maak collected <- aowa!

maak collect <-|!

nikhil (conf(C_EOF)()') ->
    maak byte <- read(F_R_BYTE)!

    sif (byte?) <? nevermind!

    sif (byte != 255) ?> collect <- arrLit(collect:::, byte)!
<~

conf(C_CLOSE)()!

khuluma(t_strFromByteArr(collect:::))!
```


---


### f_cwriter

This symbol can be reached by the following aliases: _`f_cwriter`_

_**Provides byte level writes into a file.**_


#### Definition

```jaiva
F~f_cwriter(filePath, append?)
```

- **_filePath_**  **`<-`** _**string**_
	 - _The (Absolute) file path to write to._
- **_append_** **`?`** **`<-`** _**boolean**_
	 - _Append to the file if it already exists, otherwise overwrite. Defaults to true, which appends._

_**Returns:**_ **Returns a [Continuous Function](../Continuous-Functions.md) over the file writing resource.**

Since Version: _6.1.0_


#### Example: 

```jaiva
tsea "jaiva/file/bytes"!
tsea "jaiva/continuous!"

maak cont <- f_cwriter("C:\Users\Wow\myFile.jib")!
maak write <- cont(C_OUTPUT)!

write(F_W_UTF8, "What's Up Dawg??")!
write(F_W_BYTE, 255)! @ Write 255 marker so we know where the UTF8 bytes end.

cont(C_FLUSH)()!
cont(C_CLOSE)()!
```


---

## Variables

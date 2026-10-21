# types/numbers (Library)

_More complicated converters_
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| t_dToIEEE754Long | [t_dToIEEE754Long](#t_dToIEEE754Long) |
| t_lToIEEE754Double | [t_lToIEEE754Double](#t_lToIEEE754Double) |
| t_strFromByteArr | [t_strFromByteArr](#t_strFromByteArr) |
| t_strToByteArr | [t_strToByteArr](#t_strToByteArr) |
## Functions

### t_dToIEEE754Long

This symbol can be reached by the following aliases: _`t_dToIEEE754Long`_

_**Returns the representation of the given double, as a long, where it's bits correspond to the IEEE 754 floating-point "double format" bit layout.**_


#### Definition

```jaiva
F~t_dToIEEE754Long(double)
```
- **_double_**  **`<-`** _**number**_
	 - _The double to get the bits off_

_**Returns:**_ **the bits that represent the double**

Since Version: _6.0.0-beta.5_


---


### t_lToIEEE754Double

This symbol can be reached by the following aliases: _`t_lToIEEE754Double`_

_**Returns the double value of which this long is assumed to be a representation of the IEEE 754 floating-point "double format" bit layout.**_


#### Definition

```jaiva
F~t_lToIEEE754Double(long)
```
- **_long_**  **`<-`** _**number**_
	 - _The representation of the double as a long_

_**Returns:**_ **the double this long's bits represent.**

Since Version: _6.0.0-beta.5_


---


### t_strFromByteArr

This symbol can be reached by the following aliases: _`t_strFromByteArr`_

_**Converts the given byte array into a UTF-8 (Standard) string**_


#### Definition

```jaiva
F~t_strFromByteArr(<-arr?)
```
- **_arr_** **`?`** **`<-`** _**[]**_
	 - _A var args byte array (integer values from 0 to 255)_

_**Returns:**_ **the string**

Since Version: _6.1.0_


#### Example: 

```jaiva
tsea "jaiva/types/numbers"!

maak byteArr <-| 72, 101, 108, 108, 111!

khuluma(t_strFromByteArr(byteArr:::))! @ Prints "Hello"
```


---


### t_strToByteArr

This symbol can be reached by the following aliases: _`t_strToByteArr`_

_**Converts the given string into UTF-8 byte values (0–255)**_


#### Definition

```jaiva
F~t_strToByteArr(string)
```
- **_string_**  **`<-`** _**string**_
	 - _The string to convert into UTF-8 bytes_

_**Returns:**_ **The UTF-8 bytes as integer values**

Since Version: _6.2.0_


---

## Variables

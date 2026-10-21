# types (Library)

_Converting between types and stuff_
## Exports
This library exports the following libraries: 
- types/numbers

Exported Symbols Include: `t_dToIEEE754Long`, `t_lToIEEE754Double`, `t_strFromByteArr`, `t_strToByteArr`

## Table of Contents
| Alias | Link |
| --- | --- |
| t_num | [t_num](#t_num) |
| t_str | [t_str](#t_str) |
## Functions

### t_num

This symbol can be reached by the following aliases: _`t_num`_

_**Converts a given **string** to a number with an optional **radix**.**_


#### Definition

```jaiva
F~t_num(string, radix?)
```

- **_string_**  **`<-`** _**string**_
	 - _The input to convert to a number_
- **_radix_** **`?`** **`<-`** _**number**_
	 - _An optional radix to convert to_

_**Returns:**_ **A number. (Either a double, integer or long)**

> [!NOTE]
> _Jaiva integer prefixes [such as _0x_ or _0b_], are checked for first before the radix. If the resulting input is too big, a long may be returned instead of an integer_


Since Version: _2.0.0-beta.3_


#### Example: 

```jaiva
khuluma(t_num("0b1010"))! @ prints 10
khuluma(t_num("FF", 16))! @ prints 255
khuluma(t_num("3.14"))! @ prints 3.14
khuluma(t_num("0x1A"))! @ prints 26
khuluma(t_num("30129382409L"))! @ prints 30129382409 (as a long value)
```


---


### t_str

This symbol can be reached by the following aliases: _`t_str`_

_**Converts any input of any given type to a string.**_


#### Definition

```jaiva
F~t_str(input?, radix?)
```

- **_input_** **`?`** **`<-`** _**idk**_
	 - _The input to convert_
- **_radix_** **`?`** **`<-`** _**number**_
	 - _A given radix for integers or longs_

_**Returns:**_ **A string representation of the given input**

> [!NOTE]
> _If given an input of `idk`, it will return `idk`. Not a string._


Since Version: _2.0.0-beta.3_


#### Example: 

```jaiva
khuluma(t_str(255))! @ prints "255"
khuluma(t_str(true))! @ prints "yebo"
khuluma(t_str(yebo))! @ prints "yebo"
khuluma(t_str(10, 2))! @ prints "0b1010"
```


---

## Variables

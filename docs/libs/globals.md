# globals (Library)

_The globals, These functions and variables are available in any scope without an explicit import._
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| getVarClass | [getVarClass](#getVarClass) |
| reservedKeywords | [reservedKeywords](#reservedKeywords) |
| version | [version](#version) |
| flat | [flat](#flat) |
| sleep | [sleep](#sleep) |
| typeOf | [typeOf](#typeOf) |
| typeOfNumber | [typeOfNumber](#typeOfNumber) |
| arrLit | [arrLit](#arrLit) |
| scope | [scope](#scope) |
| getCallerValue | [getCallerValue](#getCallerValue) |
| khuluma | [khuluma](#khuluma) |
| mamela | [mamela](#mamela) |
| clear | [clear](#clear) |
| args | [args](#args) |
| uArgs | [uArgs](#uArgs) |
| linker | [linker](#linker) |
| tseaf | [tseaf](#tseaf) |
## Functions

### getVarClass

This symbol can be reached by the following aliases: _`getVarClass`_

_**Attempts to return the symbol's corresponding Java class in string form. If you're using this then you def don't know what you're doing.**_


#### Definition

```jaiva
F~getVarClass(var)
```
- **_var_**  **`<-`** _**idk**_
	 - _The value to return it's token symbol for_

_**Returns:**_ **The .toString() class representation of the given variable's token**

Since Version: _1.0.0-beta.0_


#### Example: 

```jaiva
maak name <- "ayo!"!
khuluma(getVarClass(name))! @ Prints com.jaiva.tokenizer.tokens.Token$TStringVar@(hashcode)
khuluma(getVarClass(reservedKeywords))! @ Prints com.jaiva.tokenizer.tokens.Token$TArrayVar@(hashcode)
```


---


### flat

This symbol can be reached by the following aliases: _`flat`_

_**Attempts to flatten (at the top level) the given arrays array1 and array2 into 1 single array.**_


#### Definition

```jaiva
F~flat(<-arrays?)
```
- **_arrays_** **`?`** **`<-`** _**[]**_
	 - _Variable amount of arrays to input_

> [!NOTE]
> _If there are any type mismatches in array1, it will be ignored and the same check is done to array2 and so on. Therefore this function will **always** return an array, whether or not it was successful._


Since Version: _1.0.0-beta.2_


#### Example: 

```jaiva
maak array1 <-| 1, 2, 3!
maak array2 <-| 4, 5, 6!
maak array3 <- flat(array1, array2)! @ Flattens the two arrays into a new one.
khuluma(array3)! @ Prints [1, 2, 3, 4, 5, 6]
```


---


### sleep

This symbol can be reached by the following aliases: _`sleep`_

_**Pause execution of the interpreter for n amount of milliseconds.**_


#### Definition

```jaiva
F~sleep(milliseconds)
```
- **_milliseconds_**  **`<-`** _**number**_
	 - _The amount of milliseconds to sleep for_

> [!NOTE]
> _(Keep in mind this function still has to take your value and turn it into a Java primitive and other things, so the delay might not be exact. If you're looking for accuracy maybe remove x amount of ms till it's accurate.)_


Since Version: _1.0.0_


#### Example: 

```jaiva
khuluma("yo")!
sleep(1000)! @ pause for 1 second.
khuluma("yo.. again")!
```


---


### typeOf

This symbol can be reached by the following aliases: _`typeOf`_

_**Returns the type of any given input.**_


#### Definition

```jaiva
F~typeOf(input?)
```
- **_input_** **`?`** **`<-`** _**idk**_
	 - _The input to check the type against_

_**Returns:**_ **Returns the string form of the typ, which could be "array", "string", "boolean", "number", "function", or the primitive idk.  If you require a more precise answer than number use typeOfNumber**

Since Version: _3.0.0_


#### Example: 

```jaiva
maak b <- 100!

khuluma(typeOf(b))!                   @ "number"
khuluma(typeOf(typeOf))!                @ "function"
khuluma(typeOf())!                    @ idk
khuluma(typeOf(aowa))!                @ "boolean"
khuluma(typeOf("what the f"))!        @ "string"
khuluma(typeOf(reservedKeywords))!    @ "array"
khuluma(typeOf(idk))!                 @ idk
```


---


### typeOfNumber

This symbol can be reached by the following aliases: _`typeOfNumber`_

_**Returns the type of a given number input. which in Java terms is either an integer, double or long**_


#### Definition

```jaiva
F~typeOfNumber(input?)
```
- **_input_** **`?`** **`<-`** _**idk**_
	 - _The input to check the type against_

_**Returns:**_ **Returns the string form of the type, which could be "integer", "double", "long", or the primitive idk.**

Since Version: _5.0.4_


#### Example: 

```jaiva
maak b <- 100!

khuluma(typeOf(b))!                   @ "integer"
khuluma(typeOf(0.34))!                @ "double"
khuluma(typeOf(0.34d))!               @ "double"
khuluma(typeOf())!                    @ idk
khuluma(typeOf(82936741648236817L))!  @ "long"
```


---


### arrLit

This symbol can be reached by the following aliases: _`arrLit`_

_**Creates an array literal from the given elements. This is useful if you want to createFunction an array without declaring it to a variable. For example, `arrLit(1, 2, 3)` will return `[1, 2, 3]`. This is needed as Jaiva doesnt have square bracket syntax**_


#### Definition

```jaiva
F~arrLit(<-elements?)
```
- **_elements_** **`?`** **`<-`** _**[]**_
	 - _Variable amount of elements to take in and turn into a single array._

_**Returns:**_ **The input given, as an array**

Since Version: _3.0.0_


#### Example: 

```jaiva
maak array1 <- arrLit(1, 2, 3, "hello", aowa, idk)! @ Creates an array with mixed types.
maak array2 <-| 1, 2, 3, "hello", aowa, idk! @ Creates an array with mixed types. (Same as above but with maak syntax)
maak array3 <- arrLit()! @ Creates an empty array.
khuluma(array1)! @ Prints [1, 2, 3, "hello", aowa, idk]
khuluma(array3)! @ Prints []
khuluma(array1 = array2)! @ Prints aowa (false) (I am not implementing array equality via `=` operator anytime soon. It is the exact same array though.)
```


---


### scope

This symbol can be reached by the following aliases: _`scope`_


#### Definition

```jaiva
F~scope(<-string?)
```
- **_string_** **`?`** **`<-`** _**[]**_
	 - _variable amount of strings to input._

_**Returns:**_ **idk**

> [!NOTE]
> _The following are accepted strings: 
    "sw" to suppress all warnings. 
    "ew" to elevate all warnings. 
    "constant" to make all symbols given constant. and 
    "strict" which toggles "ew" and "constant"
_


Since Version: _4.1.1_


#### Example: 

```jaiva
scope("freezeAll")!
kwenza af(a) ->
    khutla a!
<~
af <- 10! @ Errors as the variable af cannot be written to.

@ Or

scope("ew")!
@* depr $> This fucntion is deprecated.
kwenza af(a) ->
    khutla a!
<~
af()! @ Errors as the usual deprecation warning is now a fatal error. (Crashes the interpreter)
```


---


### getCallerValue

This symbol can be reached by the following aliases: _`getCallerValue`_

_**Returns the caller value provided to this file if this file was run by another java program.**_


#### Definition

```jaiva
F~getCallerValue()
```

_**Returns:**_ **The caller value**

Since Version: _5.0.2_


#### Example: 

```jaiva
@ Say we are in J3Engine command
maak value <- getCallerValue()!
@ use it
```


---


### khuluma

This symbol can be reached by the following aliases: _`khuluma`_

_**Prints any given input to the console.**_


#### Definition

```jaiva
F~khuluma(msg?, removenewLn?)
```

- **_msg_** **`?`** **`<-`** _**idk**_
	 - _The message to print._
- **_removenewLn_** **`?`** **`<-`** _**boolean**_
	 - _If true, no new line is printed after the message. Defaults to false._

_**Returns:**_ **idk**

Since Version: _1.0.0-beta.2_


#### Example: 

```jaiva
khuluma("Hello, World!")! @ Prints "Hello, World!" to the console with a new line.
@ Then the following prints "Hello" then "World!" on the same line.
khuluma("Hello, ", true)!
khuluma("World!")!
khuluma()! @ Prints just a new line.
```


---


### mamela

This symbol can be reached by the following aliases: _`mamela`_

_**Listens for input from the console.**_


#### Definition

```jaiva
F~mamela()
```

_**Returns:**_ **The input given from the console as a string**

> [!NOTE]
> _GithubBlockQuote: This will pause all execution until input is given._


Since Version: _1.0.0-beta.3_


#### Example: 

```jaiva
khuluma("What is your name?")!
maak name <- mamela()! @ Reads input from the user and stores it in the variable name.
khuluma("Hello, " + name + "!")!
```


---


### clear

This symbol can be reached by the following aliases: _`clear`_

_**Clears the console.**_


#### Definition

```jaiva
F~clear()
```

_**Returns:**_ **idk**

> [!NOTE]
> _The effectiveness of this function depends on the terminal's support for ANSI escape codes._


Since Version: _1.0.0_


#### Example: 

```jaiva
clear()! @ Clears the console output.
```


---


### linker

This symbol can be reached by the following aliases: _`linker`_

_**Links the MapValue instance of 'a' into 'b' such that they hold the same value and if one is edited the other will also have that edit.**_


#### Definition

```jaiva
F~linker(a, b)
```

- **_a_**  **`<-`** _**idk**_
	 - _The symbol which holds the MapValue to be linked._
- **_b_**  **`<-`** _**idk**_
	 - _The symbol who's MapValue will either be created or overwritten_

_**Returns:**_ **idk**

> [!NOTE]
> _A usual (b <- a) syntax would suffice if you'd like to copy the value of a into b.
However when a is changed, b will stay the value you set earlier. This function fixes that where it will
link the exact MapValue from a into b, discarding b's old MapValue. such that editing any one of the symbols
via the reassignment syntax will update the linked variable.

In the case that the "b" parameter does not actually exist in the symbol table, d_link will try to make it, itself.
_


Since Version: _5.0.4_


#### Example: 

```jaiva
maak a <- f~() : 10! @ Lambda that returns 10
maak b <- true! @ boolean value true

@ With normal reassignment syntax, setting b to a then changing b does not update a
b <- a!
b <- 10!
khuluma(a)! @ prints the lambda signature and not 10.

@ With d_link, the exact MapValue held by that alias is copied.
d_link(a, b)!
b <- 10!
khuluma(a)! @ prints 10
a <- 100!
khuluma(b)! @ prints 100
```


---


### tseaf

This symbol can be reached by the following aliases: _`tseaf`_

_**Imports and immediately returns the imported symbol from the given file or library**_


#### Definition

```jaiva
F~tseaf(path, symbol)
```

- **_path_**  **`<-`** _**string**_
	 - _The path to import from._
- **_symbol_**  **`<-`** _**string**_
	 - _The symbol to import from. This can either be a string, or a non-existing variable reference._

_**Returns:**_ **The value, if the requested symbol is of a variable, the function if the requested symbol is a function, but otherwise idk if the library/file doesnt expose such a symbol.**

> [!NOTE]
> _This isn't special, the "jaiva/" prefix for libraries like normal tsea syntax is still requried to differentiate a file from a library import._


Since Version: _6.2.0_


#### Example: 

```jaiva
@ Normally, you have to go via import syntax just to import a single variable
tsea "jaiva/somewhere" <- func!
func()!

@ Using tseaf you can do the following
tseaf("jaiva/somewhere", "func")()!

@ Or otherwise alias it yourself
maak func_1 <- tseaf("jaiva/somewhere", "func")!
func_1()!

@ Works for files that expose symbols too
maak arr <- tseaf("../path", "array")!
khuluma(arr~)! @ Prints array length

@ And for the symbol name, it's not required to pass a string.
@ Provided the reference does not exist in the scope, then it will
@ use that non-existing reference's name. Otherwise if it exists it
@ will use that reference's value like normal.

maak funcp <- 10!
tseaf("jaiva/somewhere", funca)! @ Will retrieve funca from the list
tseaf("jaiva/somewhere", funcp)! @ Will fail, since funcp will pass the number 10 as the argument.
```


---

## Variables

### reservedKeywords

This symbol can be reached by the following aliases: _`reservedKeywords`_

_**An array containing jaiva's reserved keywords that you cannot use as symbol names.**_


#### Definition
```jaiva
maak reservedKeywords <-| (array)
```

Since Version: _1.0.0-beta.0_


#### Example: 

```jaiva
khuluma(reservedKeywords)! @ Prints all the reserved keywords.
```


---


### version

This symbol can be reached by the following aliases: _`version`_

_**What do you think this returns.**_


#### Definition
```jaiva
maak version <- (string)
```

Since Version: _1.0.0-beta.0_


#### Example: 

```jaiva
khuluma(version)! @ Prints 5.0.0 (at the time of writing)
```


---


### args

This symbol can be reached by the following aliases: _`args`_

_**The command-line arguments passed to the Jaiva command.**_


#### Definition
```jaiva
maak args <-| (array)
```

> [!NOTE]
> _This is the raw arguments given from Main.args[], including Jaiva-specific arguments._


Since Version: _1.0.2_


#### Example: 

```jaiva
khuluma(args[0])! @ Prints "jaiva" if the command was 'jaiva myscript.jv'
khuluma(args[1])! @ Prints "myscript.jv" if the command was 'jaiva myscript.jv'
```


---


### uArgs

This symbol can be reached by the following aliases: _`uArgs`_

_**The command-line arguments without Jaiva-specific arguments.**_


#### Definition
```jaiva
maak uArgs <-| (array)
```

Since Version: _1.0.2_


#### Example: 

```jaiva
khuluma(uargs[0])! @ Prints any other argument given after the Jaiva specific ones.
khuluma(uargs[1])! @ Prints the second user argument if provided.
khuluma(uargs[2])! @ And so on...
```


---


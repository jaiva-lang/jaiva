# reflect (Library)

_playin around with interpreter internals yknow._
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| r_fEvalParamsLater | [r_fEvalParamsLater](#r_fEvalParamsLater) |
| r_vPreferArr | [r_vPreferArr](#r_vPreferArr) |
| r_sUnfreeze | [r_sUnfreeze](#r_sUnfreeze) |
| r_sGetModifiers | [r_sGetModifiers](#r_sGetModifiers) |
## Functions

### r_fEvalParamsLater

This symbol can be reached by the following aliases: _`r_fEvalParamsLater`_

_**Sets the "evalParamsLater" property of a function to true/false.**_


#### Definition

```jaiva
F~r_fEvalParamsLater(funcRef, newValue?)
```

- **_funcRef_**  **`<-`** _**idk**_
	 - _The function reference._
- **_newValue_** **`?`** **`<-`** _**boolean**_
	 - _Whether the function should evaluate parameters later or not._

_**Returns:**_ **idk**

> [!NOTE]
> _Majority of Functions In Jaiva, rely on the interpreter to parse and resolve input arguments for it, before it runs. However a minority such as d_emit from jaiva/debug prefer to resolve parameters itself, in which case they have a property "evalParamsLater" set to true. What does this mean? Changing this property for any function is 99.98% going to brick it. Only library functions can decide how to handle special input otherwise Jaiva defined functions should never have this value set to true. And i mean you can test and find out._


Since Version: _7.0.0_


#### Example: 

```jaiva
tsea "jaiva/reflect" <- r_fEvalParamsLater!
tsea "jaiva/math" <- m_sin!

r_fEvalParamsLater(m_sin)!
@ In this case, it still works since the tokenizer passes the integer 1
khuluma(m_sin(1))!
@ However the minute you make it a variable,
@ m_sin gets a token and fails
maak a <- 1!
khuluma(m_sin(a))!
```


---


### r_vPreferArr

This symbol can be reached by the following aliases: _`r_vPreferArr`_

_**Set's the "preferArr" property of a variable to true, or otherwise another value**_


#### Definition

```jaiva
F~r_vPreferArr(varRef, newValue?)
```

- **_varRef_**  **`<-`** _**idk**_
	 - _The variable reference._
- **_newValue_** **`?`** **`<-`** _**boolean**_
	 - _Whether the variable should prefer arrays or not. Defaults to true_

_**Returns:**_ **idk**

> [!NOTE]
> _Variables in Jaiva are dual storage, in that they have 2 separate slots for a singular scalar value and an array value. Usually this distinction is made at variable creation time however it does not change, meaning if you define a variable to hold 1, then reassign it the value of an array, your array was stored successfully be any normal calls to the variable will only return 1. This function flips it to force it to instead prefer the stored array over the scalar value._


Since Version: _7.0.0_


#### Example: 

```jaiva
tsea "jaiva/reflect" <- r_vPreferArr!

maak var <- 10!
var <- arrLit(1, 2, 3)!

khuluma(var)! @ Prints 10
r_vPreferArr(var)!
khuluma(var)! @ Prints [1, 2, 3] Although now you have the inverse issue.
r_vPreferArr(var, false)!
khuluma(var)! @ Prints 10
```


---


### r_sUnfreeze

This symbol can be reached by the following aliases: _`r_sUnfreeze`_

_**Unfreezes a symbol, making it's "pointer" mutable in any scope.**_


#### Definition

```jaiva
F~r_sUnfreeze(symbol)
```
- **_symbol_**  **`<-`** _**idk**_
	 - _The symbol reference. Function or variable._

_**Returns:**_ **idk**

> [!NOTE]
> _Unfreezes a frozen symbol. This does not imply that the instance of the variable is now writable, but that the container it owns in the scope's VFS can be written and hence overwrite the symbol. This also does not remove any modifiers that the symbol may have been annotated with it simply changes the internal property._


Since Version: _7.0.0_


#### Example: 

```jaiva
tsea "jaiva/reflect" <- r_sUnfreeze!

r_sUnfreeze(khuluma)!

@ Now you can edit khuluma to your liking
khuluma <- 1!
@ khuluma function itself is just overwritten.
@ So you have lost the ability to print.
@ Congrats.
```


---


### r_sGetModifiers

This symbol can be reached by the following aliases: _`r_sGetModifiers`_

_**Returns the modifiers a symbol has as an array of strings.**_


#### Definition

```jaiva
F~r_sGetModifiers(symbol)
```
- **_symbol_**  **`<-`** _**idk**_
	 - _The symbol reference. Function or variable._

_**Returns:**_ **An array of strings**

> [!NOTE]
> _Literally just retries the modifiers this symbol has as a bare bones string array._


Since Version: _7.0.0_


#### Example: 

```jaiva
tsea "jaiva/reflect" <- r_sGetModifiers!

maak [test, r, f f] x <- 1!

khuluma(r_sGetModifiers(x))! @ prints ["test", "r", "f f"]
```


---

## Variables

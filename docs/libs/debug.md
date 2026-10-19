# debug (Library)

_Random stuff you should not be working with. These aren't related to the CLI debugger._
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| d_emit | [d_emit](#d_emit) |
| d_vfs | [d_vfs](#d_vfs) |
| d_link | [d_link](#d_link) |
| d_getScope | [d_getScope](#d_getScope) |
## Functions

### d_emit

This symbol can be reached by the following aliases: _`d_emit`_

_**Throws a DebugException to be caught by a Java test class and emits the given variables**_


#### Definition

```jaiva
F~d_emit(<-arr?)
```
- **_arr_** **`?`** **`<-`** _**[]**_
	 - _The array of values to pass to the exception_

_**Returns:**_ **Physically can't return. As it always throws an error**

> [!NOTE]
> _If you aren't familiar with Java, the language Jaiva is developed in. This will essentially forcefully stop the execution of the interpreter, with the intent for said error to be caught and dealt with by another Java class. This serves 0 purpose if  you're just running a Jaiva file._


Since Version: _1.0.2_


---


### d_vfs

This symbol can be reached by the following aliases: _`d_vfs`_

_**Returns the current context's vfs.**_


#### Definition

```jaiva
F~d_vfs()
```

_**Returns:**_ **A 2d array, first array containing the keys, second array the values.**

> [!NOTE]
> _This function does not allow you to edit the current vfs, only to get everything that is currently within the vfs as an array. Everytime this function is called a new array containing all the stuff is made._


Since Version: _4.1.0_


---


### d_link

This symbol can be reached by the following aliases: _`d_link`_

> [!WARNING]
> This symbol has been marked as deprecated! This function has moved to the global scope and is now named "linker". Use that instead as this may be removed


_**Links the MapValue instance of 'a' into 'b' such that they hold the same value and if one is edited the other will also have that edit.**_


#### Definition

```jaiva
F~d_link(a, b)
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


### d_getScope

This symbol can be reached by the following aliases: _`d_getScope`_

_**gets the scope. wahtd you expect**_


#### Definition

```jaiva
F~d_getScope()
```

_**Returns:**_ **the scope string**

> [!NOTE]
> _This is the exact same scope string that an error outputs_


Since Version: _5.0.4_


---

## Variables

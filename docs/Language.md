# Index

> [!WARNING]
> The docs are always up-to-date with the current
version whether it is stable or not.
> Refer to [the changelog](../changelog.md) to see the features
which the current stable versions supports.

-   **[Index](#index)** <- you are here.
-   **[Examples](#examples)**
-   **[Syntax](#syntax)**
    -   _[Comments](#comments)_
    -   _[Assignment Operators](#assignment-operators)_
    -   _[Types](#types)_
        -   [Number](#number)
        -   [Booleans](#booleans)
        -   [Strings](#strings)
            -   [String Operations](#string-operations)
            -   [Escaping Characters](#escaping-characters)
        -   [idk](#idk)
    -   _[Operators](#operators)_
    -   _[Blocks](#blocks)_
    -   _[Chaining](#chaining)_
-   **[Keywords](#keywords)**
-   **[Scopes](#scopes)**
-   **[Variables](#variables)**
    -   _[Definition](#definition)_
    -   _[Usage](#usage)_
    -   _[Reassignment](#reassignment)_
-   **[Arrays](#arrays)**
-   **[Functions](#functions)**
    -   _[Definition](#definition-1)_
    -   _[Calling](#calling)_
        -   [Spread Operator](#spread-operator)
    -   _[Referencing](#referencing)_
    -   _[Parameters](#parameters)_
        -   [Higher-Order Functions](#higher-order-functions)
        -   [Optional Arguments](#optional-arguments)
        - [Variadic Arguments](#variadic-arguments)
    - _[Lambdas](#lambdas)_
    - _[Argument Extension](#argument-extensions)_
-   **[If Statements](#if-statements)**
    -   _[Basic If](#basic-if)_
    -   _[mara (else)](#mara-else)_
    -   _[mara (else) with if](#mara-else-with-if)_
    -   _[Ternary Ifs](#ternary-ifs)_
    -   _[Shorthand Ifs](#shorthand-ifs)_
-   **[Loops](#loops)**
    -   _[Nikhil Loops (While Loops)](#nikhil-loops-while-loops)_
    -   _[Colonize Loops (For Loops)](#colonize-loops-for-loops)_
        -   [Colonize with (For Each)](#colonize-with-for-each)
    -   _[Control Flow](#control-flow)_
-   **[Error Handling](#error-handling)**
    -   _[Throw an Error](#throw-an-error)_
    -   _[zama zama (Try) chaai (Catch) Block](#zama-zama-try-chaai-catch-block)_
-   **[Tsea (Import) and Exporting Files](#tsea-import-and-exporting-files)**
-   **[Arrow Syntax Table](#arrow-syntax-table)**

# Examples

These examples tend to use multiple different features at once.
See the relevant sections for more in depth explanations of each feature used.



# Syntax

It's simple really, to start off with:

All lines (unless block open and close) must end in an exclamation mark. because why aren't you screaming your code.

```jaiva
maak a <- 10!
```

## Comments.

Single line comments start with an `@`

```jaiva
@ single line comment
```

And multi line comments open and close with the `{` `}`

```jaiva
{
    wus good shawty
    - stfu
}
```

### Documentation

If you're using something like vscode and would like to document your [variables](#variables) or [functions](#functions), you can use the following syntax

```jaiva
@* My lovely variable.
maak a <- 10!
```

For more information on JaivaDoc, see [JDoc.md](docs/JDoc.md)

## Assignment operators

`<-` Is the basic assignment operator in this language. Any assignment you do is with this.

Accept for arrays, thats specifically `<-|` (See [Arrays](#arrays))

And also for throwing errors, thats `<==` (See [Throw an error](#throw-an-error))

## Types

In terms of actual types, Jaiva doesnt have type syntax like Java or Typescript. This is a loosely typed language. However some functions do error when you do not provide the correct type, so keep that in mind.

### Number

This is just any integer or real number

They don't need any special format, you can just type the number out to use it

```jiv
maak b <- 10!
maak c <- 9.321!
```

(also scientific notation is supported)

```jiv
maak b <- 1e4! @ This gets parsed as 10000
```

even binary, hex and octal notation is supported! (it always gets parsed as an integer)

```jiv
maak c <- 0b1010    @ Integer 10 in binary
maak d <- 0xA       @ Integer 10 in hexadecimal
maak e <- 0c12      @ Integer 10 in Octal
```

### Bools

`true` and `false` are supported, but so are `aowa` (maps to false) and `yebo` (maps to true). because come on now this is just cool.

They don't need any special format, you can just type the bool out to use it

```jiv
maak b <- aowa!
```

### Strings

Strings, stolen striaght from Java use `"` (double quotes) to start and end a string.

These are the only characters for strings. No string literals or multi line strings because wtf dawg.

```jiv
maak b <- "String"!
```

To get the length of a string use the `~` operator

```jiv
maak b <- "let's gooo"!
khuluma(b~)! @ returns 10
```

#### String operations.

1.  Concatenation

    `"a" + "string"` _returns_ **`"astring"`**

    `1 + "string"` _returns_ **`"1string"`**

    `"string" + 1` _returns_ **`"string1"`**

2.  Substring (first occurance)

    `"string" - "tri"` _returns_ **`"sing"`** (removes the first occuramce of rhs from the lhs.

    `"string" - 2` _returns_ **`"stri"`**

    `2 - "string"` _returns_ **`"ring"`**

3.  Multiplication

    `"String" * 3` _returns_ **`"StringStringString"`**

4.  Substring (all occurances)

    `"remove all es please" / "e"` _returns_ **`"rmov all s plas"`** (removes ALL occurences of rhs from the lhs.)

    `"Hello ong World" / 2` _returns_ **`"Hello o"`** (returns the substring in the range _[0, (lhs' length)/rhs)_ )

    `4 / "Hello ong World"` _returns_ **`"lo ong World"`** (returns the substring in the range _[(rhs' length)/lhs, rhs.length-1)_ )

    And of course, you can compare strings to each other using `=` and `!=`

5.  Contains

    `"string" ? "tri"` _returns_ **`true`** (checks if the left-hand string contains the right-hand string)

    `"string" ? "xyz"` _returns_ **`false`**

Also you can go nuts and compound them together

```jaiva
maak output <- 2 - "hello" - 2!
@ Output holds "l"
```

#### Escaping characters

To escape characters in a string use the `$` symbol.

```
maak b <- "String$n"!
```

Table of escape characters

| character              | escape sequence |
|------------------------|-----------------|
| `=`                    | `$=`            |
| `,`                    | `$,`            |
| `!`                    | `$!`            |
| `@`                    | `$@`            |
| `\n` (new line)        | `$n`            |
| `\t` (tab)             | `$t`            |
| `\r` (carriage return) | `$r`            |
| `\b` (backspace)       | `$b`            |
| `\f` (form feed)       | `$f`            |
| `"` (double qoutes)    | `$"`            |
| `$`                    | `$$`            |

### idk

This is a constant and value which is used to represent nothingness.

```jaiva
maak a <- idk!  @ Using idk as a value
maak b!         @ Creating a variable with no value assigns idk to it
maak c <- 10!

khuluma(a = idk)! @ true
khuluma(b = idk)! @ true
khuluma(c != idk)! @ true

kwenza func(t?) ->
    khutla t!
<-

khuluma(func())! @ Prints idk, as the parameter t did not get a value.
```

> [!NOTE]
> In the case of passing `idk` into a function call, unless the parameter is marked as optional, you cannot pass `idk` into a required parameter.

## Operators

| operation                                          | operator |
|----------------------------------------------------|----------|
| modulu                                             | `%`      |
| exponentiate                                       | `^`      |
| division                                           | `/`      |
| multiplication                                     | `*`      |
| addition                                           | `+`      |
| subtraction                                        | `-`      |
| unary minus                                        | `-`      |
| is equal to (not double equals)                    | `=`      |
| is not equal to                                    | `!=`     |
| greater than (and equal to)                        | `>` `>=` |
| less than (and equal to)                           | `<` `<=` |
| logical AND                                        | `&&`     |
| logical OR                                         | `\|\|`   |
| Logical NOT (Negation)                             | `'`      |
| Logical Identity (Returns the same value as input) | `;`      |
| bitwise AND                                        | `&`      |
| bitwise OR                                         | `\|`     |
| bitwise XOR                                        | `#`      |
| braces for ordering                                | `(` `)`  |
| bitshift left                                      | `<<`     |
| bitshift right                                     | `>>`     |
| hexshift left                                      | `<x`     |
| hexshift right                                     | `>x`     |
| String containment check/[idk](#idk) check         | `?`      |

> [!NOTE]
> There is no "truthiness".

> [!NOTE]
> Where other languages use `!` as logical NOT as a prefix to the expression. Jaiva uses `'` as a logical NOT as a `post`fix expression. Where a normal language would right `!(var != 3)` in Jaiva the equivalent is `(var != 3)'`

> [!NOTE]
> Similarly. Jaiva uses `^` for exponentiation. and `#` for XOR

### Special Operators

This is just a list of operators which resolve to something specific

| Operation            | Input Value | Parsed As       |
|----------------------|-------------|-----------------|
| Unary Minus          | `-value`    | `-1 * value`    |
| idk check*           | `value?`    | `value = idk`   |
| idk check* (inverse) | `value?'`   | `value != idk`  |
| Logical NOT          | `value'`    | `value = false` |
| Logical Identity     | `value;`    | `value = true`  |

> [!NOTE]
> The idk check only works if there is no right hand side input. If there is, then it
> works as the `?` operator defined in [String operations](#string-operations). A containment
> check.

### Order of Operations

All Operations follow this exact order:

1. Exponentiation (Highest precedence)
2. Division, Multiplcation, Modulo
3. Addition, Subtraction
4. Bit/Hex Shifts
5. Bitwise Operators
6. Comparisons
7. Logical Operators (Lowest precedence)

Use `(`braces`)` to change the order however you wish. (Espeically since sometimes this shit doesnt wanna work)

## Blocks

Blocks are defined by the `->` and `<~` symbols. The `->` symbol opens a block, and the `<~` symbol closes it.

I think this is where jaiva deviates from normal programming languages, Especially since your usual `{` `}` is reserved for comments.

> [!NOTE]
> You can have multiple blocks in a single line, but this is not recommended. It makes the code hard to read. (And i dont think i implemented or tested that case, ur crazy if u think im finna allow that )

> [!NOTE]
> You can only have a block after like, an if, or a function and whatever, you cant just open an new arbitrary block in the middle of your code. This is a design choice, and i think it makes sense.

```jiv
if (a = 10) ->
   @ inner code
<~
```

## Chaining

In Jaiva you have array indexing with `[]` (See [Arrays](#arrays)) and function calling with `()` (See [Functions](#functions))

Naturally, you can make some code like this:

```jiv
kwenza returnArray() ->
    maak arr <-| 10, 39, returnArray, 4!    @ Creates an array with values and at index 2, contains a reference to itself.
    khutla arr!
<~
```

Instead of assigning each value returned to a variable, you can chain array indexing and function calls together, multiple times, in any order or any fashion

```jiv
returnArray()[1]!               @ holds 39
returnArray()[1 + 1]!           @ ([1+1] becomes [2]) holds the referencing to itself, allowing it to call itself.
returnArray()[-1 + 3]()!        @ ([-1+3] becomes [2]) returns the same array.
returnArray()[2]()[4/2]!        @ ([4/2] becomes [2]) holds the exact same reference to itself, allowing infinite calls to itself

@ holds the exact same reference, every time.
returnArray()[2]()[2]()[2]()[2]()[2]()[2]()[2]()[2]!
```

One of my favourite examples is the infinity function

```jiv
kwenza infinity() ->
    khutla infinity!
<~

infinity()!             @ Returns itself
infinity()()()()!       @ Returns itself
infinity()()()()()()()()()()()()()()()()()()()()()()! @ Still returns itself.
```

Jaiva's Interpreter however will take a while to resolve this mess of code when the chaining gets absurdly long. But if you give it time it will work.

# Keywords

Alot of the keywords refer to words from South African languages, so if you happen to know one, you've got the advantage
here's a cheat table though

| keyword   | meaning                      | use in jaiva                                                  | language origin       | reference                                   |
| --------- | ---------------------------- | ------------------------------------------------------------- | --------------------- | ------------------------------------------- |
| maak      | "make"                       | variable declaration keyword                                  | Afrikaans             | [Variables](#variables)                     |
| aowa      | "no"                         | `false`                                                       | Sepedi                | [Bools](#bools)                             |
| yebo      | "yes"                        | `true`                                                        | Zulu                  | [Bools](#bools)                             |
| if        | -                            | conditional execution keyword                                 | English               | [If Statements](#if-statements)             |
| mara      | "but"                        | keyword to start an `else` block                              | Sesotho               | [Mara (else)](#mara-else)                   |
| kwenza    | "does"                       | function definition keyword                                   | Zulu                  | [Functions](#functions)                     |
| khutla    | "return"                     | function return keyword                                       | Zulu                  | [Functions](#functions)                     |
| colonize  | -                            | loop constructs (for loop and for-each loop)                  | English               | [Colonize loops](#colonize-loops-for-loops) |
| nikhil    | -                            | while loop keyword                                            | it's a name, not sure | [Colonize loops](#colonize-loops-for-loops) |
| zama zama | "try"                        | try block                                                     | Zulu (informal?)      | [Errror Handling](#error-handling)          |
| cima      | "turn off"                   | keyword to throw an error                                     | Zulu                  | [Throw an error](#throw-an-error)           |
| chaai     | "oh no" (subjective meaning) | catch block                                                   | lowkey dont know      | [Error Handling](#error-handling)           |
| voetsek   | "fuck off"                   | `break` keyword                                               | Afrikaans             | [Control flow](#control-flow)               |
| nevermind | -                            | `continue` keyword                                            | English               | [Control flow](#control-flow)               |
| with      | -                            | keyword used to define for each loop along with colonize      | English               | [Colonize loops](#colonize-loops-for-loops) |
| tsea      | "take"                       | keyword to import symbols from another file                   | Sepedi                | [Tsea](#tsea-import-and-exporting-files)    |
| idk       | -                            | This is a special keyword, as it also acts as a value. (null) | English               | [idk](#idk)                                 |
| however   | -                            | (`else` keyword in a ternary)                                 | English               | [Ternary ifs](#ternary-ifs)                 |

# Scopes

Everytime you open a block, you create a new scope. This means that any variables or functions you define in that block are not accessible outside of that block.

```jiv
maak a <- 10!
maak b <- 20!
maak c <- 30!

if (a = 10) ->
    maak d <- 40!
    khuluma(d)! @ 40
    kwenza addition(param1, param2) ->
        khuluma(param1 + param2)!
    <~
<~ mara ->
    khuluma(d)! @ This will error, because d isn't defined in this scope.
    khuluma(a)! @ 10. This works because a is defined in the global scope.
<~

addition(10, 20)! @ Will also error, because addition() is not defined in this scope.
```

When a new Jaiva file is run, the global scope is created. This is the outermost scope, and any variables or functions defined here are accessible from any other scope.

When a block is opened, a new scope is created. This new scope has access to all variables and functions defined in its parent scope, but not vice versa.

When a block is closed, the scope is destroyed, and any variables or functions defined in that scope are no longer accessible.

This scoping mechanism allows for encapsulation and prevents naming conflicts between variables and functions defined in different scopes.

The only exception to the conventional scoping rules are [Lambas](#lambdas) which preserve the scope they were created in.

# Variables

Variables are [scoped](#scopes) constructs.

See [Globals](docs/Libraries.md) for a list of global variables that are available to you.

## Definition

```jiv
maak (variable name) <- (value)!
```

Simple asf

```jiv
maak a <- 20!           @ number
maak var1 <- "string"!  @ string
maak var5 <- aowa!      @ boolean
maak f!                 @ define without a value.
```

Also a neat feature, since only a specifc set of chars are reserved, this allows for some weird variable names that is allowed.

| Statement                                 | Variable Name |
|-------------------------------------------|---------------|
| `maak a b <- 10!`                         | `a b`         |
| `maak a  b <- 100!` (diferent from above) | `a  b`        |
| `maak b... <- 20!`                        | `b...`        |
| `maak \ <- 10!`                           | `\`           |

And more crazy combos you can come up with. if it doesnt result in a generic Java error, it's probably valid. Go wild.

## Use

```jiv
maak a <- 20!
maak b <- 10!
maak c <- a + b! @ 30
```

## Reassignment

```jiv
maak a <- 20!

a <- 10!
a <- "string"!
```

You can reassign any type really. This shit aint type safe.

#  Arrays

**Arrays are 0-indexed. This isn't Lua afterall**

**Array literals do not exist. Sorry not sorry.**

```jiv
maak a <-| 20, 23, 56, 324, 354!
maak b <-|! @ Empty array.
```

Yknow. Then you can access elements of the array using the `[]` operator.

```jiv
maak a <-| 20, 23, 56, 324, 354!
maak b <- a[0]! @ 20
```

To get the length of an array use the `~` operator.

```jiv
maak a <-| 10, 23, 984!
khuluma(a~)! @ returns 3
```

There is a function, which allows you to create an array literal on the fly:

```jiv
maak a <- arrLit(10, 23, 984)!
@ Same as
maak a <-| 10, 23, 984!
```

Then there's spreading arrays into functions, but thats in [Argument Spreading](#spread-operator)

# Functions

Functions are [scoped](#scopes) constructs.

See [Globals](docs/Libraries.md) for a list of global functions that are available to you.

## Definition

Functions are defined using the `kwenza` keyword, and return values using the `khutla` keyword.

```jiv
kwenza addition(param1, param2) ->
    khutla (param1 + param2)!
<~
```

## Calling

Just like any other language, you can call a function by just using the name of the function and passing in the parameters.

```jiv
maak a <- 10!
maak b <- 20!
maak c <- addition(a, b)! @ 30
@ or
maak c <- addition(10, 20)! @ 30
```

Function parameters can take any type of variable, including arrays and other functions.

### Spread Operator

See [Arrays](#arrays) for more information on arrays.

The spread operator (`:::`) allows you to spread the elements of an array into individual 
arguments when calling a function.

```jiv
maak arr <-| 10, 20, 30!

kwenza sum(param1, param2, param3) ->
    khutla (param1 + param2 + param3)!
<~

maak result <- sum(arr:::)! @ This is equivalent to sum(10, 20, 30)
```

It works best when paired with [Variadic Arguments](#variadic-arguments), otherwise other arguments will be
effectively discarded

```jiv
maak arr <-| 10, 20, 30!

kwenza sum(<-params) ->
    maak total!
    colonize param with params ->
        total <- total + param!
    <~
<~

kwenza sum2(a, b) ->
    khutla a + b!
<~

maak result <- sum(arr:::)! @ This works perfectly, as sum takes in a variadic parameter
maak result2 <- sum2(arr:::)! @ Will be 30, as only the first 2 elements of arr are consumed and the last elemnt is discarded.
```

And a more in depth example of this behaviour

```jiv
maak arr <-| 1, 4, 3!

kwenza sub(p, q) ->
    khutla p - q!
<~

maak result <- sub(arr:::, 2)!
```
>   In the above call, the spread operator expands to 1, 4 and 3 before the function is called.
>   Therefore the second parameter given which was 2, is giuven as the 4th parameter to the function.
>    Effectively making the function call sub(1, 4, 3, 2), However since the function only takes in 2 parameters,
>    the 3rd and 4th parameters are discarded.
    
>    Therefore, result will be -3 sub(1, 4)
    
>    Parameters are read left to right, and when a spread operator is used, it expands the array elements in place,
>    pushing any subsequent parameters to the right.

And a shorter example using [lambdas](#lambdas)

```jiv
maak arr <-| "hello", " ", "world"!

khuluma( (f~(arr):arr~)(arr:::) )!
```

>   In this example, we define a lambda function that takes an array and returns its length.
>   However, when we call this lambda with the spread operator, it expands the array elements into individual arguments.
>   The lambda instead receives each string as a separate argument rather than a single array
>   and since the lambda expects a single array parameter, the extra arguments are discarded.

>   Therefore, it will return the length of the first argument "hello", which is 5.

It is up to the function's definition to handle the parameters correctly, otherwise out of bounds parameters will be discarded.

The only exception to this is when the function takes in a [Variadic Argument](#variadic-arguments), in which case all parameters after the spread operator 
will be consumed into the variadic parameter as an array and any other parameters before and/or after will be assigned normally.

Also yes, you can spread multiple arrays multiple times in a single function call.

```jiv
maak arr1 <-| 1, 2, 3!
maak arr2 <-| 4, 5, 6!
kwenza combine(<-params) ->
    khutla params!
<~
maak result <- combine(-1, 0, arr1:::, arr2:::, 7, 8)! @ result is now [-1, 0, 1, 2, 3, 4, 5, 6, 7, 8]
```

## Referencing

You can reference a function by excluding the parameter braces.

```jiv
khuluma(additon)! @ This will print to the console, the function's defintion. So "addition(param1, param2)"
```

And you can pass this reference as a value anywhere you want really

(See [Higher-Order Functions](#higher-order-functions) for passing function references in other functions)

```jiv
maak new <- addition!   @ This will mkae the variable new hold the reference to the function addition
khuluma(new(10, 10))!   @ Now new can be used as a function, and will act exactly like addition
khuluma(new)!           @ Printing new will give you the original function's declaration so "addition(param1, param2)"
```

And some more funky examples

```jiv
kwenza anotherFunc() ->
    khutla 10!
<-

maak t <- anotherFunc!  @ This assigns t, the reference to anotherFunc
anotherFunc <- khuluma! @ This reassigns anotherFunc as a reference to khuluma

khuluma(t)!             @ Print "anotherFunc()"
khuluma(t())!           @ Prints 10
khuluma(anotherFunc)    @ Prints "khuluma(msg, removeNewLn?)"
anotherFunc("hello")!   @ Prints "hello" (Since this is just khuluma.)
```

## Parameters

### Higher-Order Functions

In the case wehere you want to pass a function into another function, you will have to input `F~` to the name of the parameter to explicity tell the interpreter that the shi u finna input should be treated as a function.

`V~` is available for variables, but its not needed as without this typing, its always assumed to be a variable

```jiv
kwenza function(F~ref) ->
    khutla ref()!
<~

kwenza returnNum() ->
    khutla 100!
<~

khuluma(function(returnNum))! @ prints 10
```

be careful when you start doing this though as when you start calling and chaining these, you loose variables in scopes since it doesnt know an shi.

### Optional Arguments

In the case where you need certain arguments to be optional, stolen straight from typescript, all you gotta do is suffix the parameter name with `?` then it will not be required when calling the function.

```jiv
kwenza function(param?) ->
    khuluma(param = idk)! @ When a parameter is optional and it is not given,
                           @ It will be set to the type idk
<~

function()! @ prints true
function(10)! @ prints false
```

> [!WARNING]
> When a parameter is required, it CANNOT be set to [idk](#idk) or be empty in the parameters list of the function call. That's the whole point of a required parameter, that it is a value.

### Variadic Arguments

Otherwise, known as rest parameters or varargs in other places, allows you to pass in any amount of arguments to a function, and have them all be stored in an array.

To define a variadic parameter, prefix the parameter name with `<-` (No space in between)

```jiv
kwenza function(<-param1) ->
    khuluma(param1)! @ prints the array of all arguments passed in.
<~
```

In Jaiva though, you can't have any other parameters before or after the variadic parameter. You either have optional and required parameters or a single variadic parameter.

Since a variadic parameter can take in any amount of arguments, it can also take in 0 arguments too.

```jiv
function()! @ prints an empty array
maak a <- 10!
function(10, 20, "string", aowa, a)! @ prints [10, 20, "string", false, 10]
```
## Lambdas

Using

```jaiva
f~(<param>) : (return value)
```

where `<param>` is your basic jaiva parameter. Can be required, optional, var args, another function?? anything.

and `(return value)` is an atomic value, so that's your number, string, variable, function call, another lambda. All good.

Lambdas, are strictly single line and have to return something within that single line (Without the `khutla` keyword)

Also. Yes. Creating a lambda, preserves the [Scope](#scope) it was created in, allowing for functional paradigms like currying and closures.

e.g.

```jaiva
maak add <- f~(a) : f~(b) : a + b!

khuluma(add(1)(4))! @ prints 5
```

And now functions which take in simple functions, can be reduced to a single line

e.g.

```jaiva
tsea "jaiva/arrays"!

a_reduce(arr, f~(a, b) : a << b)!
```

A lambda, is no different to a function at all. Parameters are defined the same way, and it's called the same way too.
Don't believe me? The token [TLambda](../src/main/java/com/jaiva/tokenizer/tokens/specific/TLambda.java)
extends the token [TFunction](../src/main/java/com/jaiva/tokenizer/tokens/specific/TFunction.java) and within the interpreter,
the class [Lambda](../src/main/java/com/jaiva/interpreter/symbol/Lambda.java) extends the class
[BaseFunction](../src/main/java/com/jaiva/interpreter/symbol/BaseFunction.java)

## Argument Extensions

Since `()` and `[]` in jaiva have to be on the same line, problems arise when you are passing multiple arguments into a function.

For instance, take `a_apply` from `jaiva/arrays` which applies the given functions to an array:

```jaiva
tsea "jaiva/arrays"!

@ A very messy array
maak myArr <-| 10, 49, f~() : 2, "whats up", yebo, idk, arrLit(10), 81! 

maak out <- a_apply(myArr, f~(arr) : a_forEach(arr, khuluma), f~(arr) : a_map(arr, f~(el) : typeOf(el) = "function" => el() however el), f~(arr) : a_filter(arr, f~(el) : (el?)' )!
```

I can barely even read that!

To fix this, we can use syntax which allows you to extend a function call's arguments
into multiple lines (uses `|>`)

```jaiva
tsea "jaiva/arrays"!

@ A very messy array
maak myArr <-| 10, 49, f~() : 2, "whats up", yebo, idk, arrLit(10), 81! 

maak out <- a_apply(myArr)!
    |> f~(arr) : a_forEach(arr, khuluma)!
    |> f~(arr) : a_map(arr, f~(el) : typeOf(el) = "function" => el() however el)!
    |> f~(arr) : a_filter(arr, f~(el) : (el?)')!
```

which is functionally equivalent to calling the called function with all the values in `|>` appended to the call as independent arguments.

So it is literally just a way to split the argument list into multiple lines.

> [!NOTE]
> The statement itself still has to close it's brace and have `!` at the end. As in:
```diff
- WRONG
func(
    |> arg
    |> arg, arg2)!

+ CORRECT
func()!
|> arg!
|> (arg, arg2)!
```

This does come in handy for functional style usage but this is purely for making arguments readable, for exmaple
take `f_new` function from `jaiva/file`, which takes in conceptually `f_new(filePath, [content], canRead?, canWrite?, canExecute?)`

You can write the entire call in a single line

```jaiva
tsea "jaiva/file/api"!
f_new("C:/Users/PrivateLol/data.csv", arrLit("some,random,text,here","1,2,6,3","10,5,1,6"), true, true, true)!
```

or you can use argument extension to either pass one or multiple arguments at once vertically
```jaiva
f_new()!
    |> "C:/Users/PrivateLol/data.csv"!
    |> arrLit("some,random,text,here","1,2,6,3","10,5,1,6")! @ Passes a single array as the second parameter
    |> (true, true, true)! @ Passes true into the third, fourth and fifth slot.
```

id document more but come on man go test urself

## Continuous Functions

These are special types of functions which provide a closure over resources.

They are so special, they get their own documentation in [Continuous Functions](Continuous-Functions.md)

However here is an example reading some input file.

```jaiva

tsea "jaiva/file/bytes"!
tsea "jaiva/continuous!"
tsea "jaiva/types/numbers"!

@ If the file contains a UTF8 string then a 255 byte value

maak conf <- f_creader("C:\Users\Wow\myFile.jib")!
@ Get the read function
maak read <- conf(C_READ)!
maak collect <-|!

@ While the EOF function returns false, meaning we haven't reached the end of the file
nikhil (conf(C_EOF)()') ->
    @ Read 1 byte
    maak byte <- read(R_BYTE)!

    @ Shorthand ifs that execute the statement if the condition is true
    sif (byte?) ?> nevermind!
    sif (byte != 255) ?> collect <- arrLit(collect:::, byte)!
<~
@ Close the stream and free resources
conf(C_CLOSE)()!
@ Print the resulting UTF-8 String by expanding the byte array into individual aprameters into
@ t_strFromByteArr function provided by "jaiva/types/numbers"
khuluma(t_strFromByteArr(collect:::))!
```

# If Statements

For if statements, `if` is the keyword, and `mara` is the else statement.

## Basic If

```jiv
if (condition) ->
    @ block to execute.
<~
```

```jiv
maak variable <- 10!
if (variable != 100) ->
    khuluma("Variable isn't 100")!
<~
```

## mara (else)

```jiv
if (condition) ->
    @ block to execute
<~ mara ->
    @ block to execute if the condition is false
<~
```

```jiv
if (variable != 100) ->
    khuluma("Variable is not 100")!
<~ mara ->
    khuluma("It's 10")!
<~
```

> [!NOTE]
> Other languages allow you to put a variable as a the condition to check for truthiness. We don't allow that here, check it against [true](#bools) or [idk](#idk)

> [!WARNING]
> You cannot put the mara keyword underneath the end of an if block, it HAS to be on the same line as the closing `<~` symbol. This makes it easier for me lol.

## mara (else) with if

```jiv
if (condition) ->
    @ block to execute
<~ mara if (condition) ->
    @ another block.
<~
```

```jiv
maak variable <- 10!
if (variable != 100) ->
    khuluma("Variable is not 100")!
<~ mara if (variable = 10) ->
    khuluma("Variable is 10")!
<~
```

You can chain as many mara if statements as you want, but this is not recommended. (It makes the code hard to read, and this language is already hard enough to read)

You can also nest if statements. because why not.

> [!WARNING]
> You can use a normal `mara` block along with other `mara if` blocks. However the `mara` block must be the LAST block of the entire if chain.

## Ternary Ifs

This is your shorthand one line if statement that returns a value too.

This is the syntax

```jiv
(condition) => (expression1) however (expression2)
```

Where if `(condition)` returns true, it returns `(expression1)` **_however_** if not, return `(expression2)`.

**(With or without braces)**

(Yes including the `however` keyword)

So you can do things like

```jiv
maak control!
@* braces are added to make it more readable, you don't need them
maak a <- ((control = idk) => idk however 0xFFFF)!
```

Where if the variable `control` is [idk](#idk) , thne variable `a` will be set to idk too. Otherwise, it will be set to the integer `0xFFFF`

And you can chain them

```jiv
maak input <- mamela("enter a number")! @ Waits for number input fom the console.

kwenza clamp(number, min, max) ->
    khutla (number > max => max however (number < min => min however number))!
<~

khuluma(clamp(input, 0, 50))!
```

Here we define a function `clamp` which will... clamp, what'd you think, the given number to the given minimum value and maximum value.

> [!NOTE]
> While it's not required to have braces around the expressions or condition. Just have them you maniac.

> [!WARNING]
> You cant have ternaries on new lines, they hve to be in the sam eline if you chain them unfortunately

## Shorthand Ifs

Believe me when i tell you it gets tiring to write a single conditional everytime.
e.g.
```jaiva
if (value != z && (s << 2 == r~)) ->
    value <- z!
<~
```

That's 3 whole lines. `s`horthand `if`s however, or `sif`s, collpase this into a single line!

```jaiva
sif (value != z && (s << 2 == r~)) ?> value <- z!
```

Where:

```jaiva
sif (CONDITION) ?> (STATEMENT)!
```

is exactly the same as writing
```jaiva
if (CONDITION) ->
    (STATEMENT)!
<~
```

> [!NOTE]
> This only works for single statement blocks, and only works for a singular `if`, it doesn't
> have a `mara if` or `mara` counterpart.

> [!NOTE]
> Where this differs from [Ternaries](#ternary-ifs) is that, a ternary is used to output a
> **_value_** with both a `true` case and a `false` case. A Shorthand-If however, is purely
> used to execute a **_statement_** only if, the condition is `true`

# Loops

## Nikhil loops (while loops)

Shoutout to Nikhil for this one.

[Twitter](https://x.com/NikhilLala11)
[Instagram](https://www.instagram.com/_nikhil1707_/) <br>
YouTube @nikhil17 (probably idk) <br>
Discord @Nikx17 <br>

```jiv
nikhil (condition) ->
    @ block to execute
<~
```

Where

> `condition` is the condition to check for the loop to continue. This is a boolean expression.

## Colonize loops (for loops)

This ones a bit weird.

```jiv
colonize ((variable init) <| (condition) <| (increment)) ->
    @ block to execute
<~
```

Where

> `variable init` is the variable to use in the for loop, and the initial value.

> `condition` is the condition to check for the loop to continue. This is a boolean expression.

> `increment` is the increment to use for the loop. This can be a `+` or a `-` sign or an expression or function call or even just a value

> and the `<|` is the separator between the three parts of the for loop.

```jiv
colonize (i <- 0 <| i <= 10 <| +) ->
    khuluma(i)! @ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
<~
```

```jiv
colonize (i <- 1 <| i <= 10 <| (f~(z) : i % 2 = 1 => 2 * z however z + 1)(i) ) ->
    khuluma(i)! @ 1, 2, 3, 6,  7
<~
```

### colonize with (for each)

```jiv
colonize ((variable name) with (array)) ->
    @ block
<~
```

Where

> `variable name` is the variable to use in the for loop, and the initial value.

> `array` is the array to loop through.

```jiv
colonize (word with reservedKeywords) ->
    khuluma(word)! @ prints all the reserved keywords
<~
```

## Control flow

For all loops, to forcefully exit a loop, use the `voetsek` keyword.

```jiv
nikhil (a = 10) ->
    khuluma(a)! @ 10
    voetsek! @ this will break out of the loop, stopping it.
<~
```

To skip to the next iteration of a loop, use the `nevermind` keyword.

```jiv
colonize (i <- 0 <| i <= 10 <| +) ->
    if (i = 5) ->
        nevermind! @ this will skip the rest of the loop and go to the next iteration.
    <~
    khuluma(i)! @ 0, 1, 2, 3, 4, 6, 7, 8, 9, 10
<~
```

# Error handling

Jaiva has error handling and throwing! This is done using 2 constructs.

## Throw an error

Use the `cima` keyword follwed by the `<==` operator, then followed by the error message.

`<==` is the assignment operator for errors. Literally not used anywhere else but for this.

```jiv
cima <== (error message)!
```

```jiv
maak a <- 10!
if (a = 10) ->
    khuluma("a is 10")!
<~ mara ->
    cima <== "a is not 10"! @ this will throw an error.
<~
```

## zama zama (try) chaai (catch) block

The `zama zama` keyword (both, one `zama` won't work.) is used to define a try block, and the `chaai` keyword is used to define a catch block.

The `chaai` block is executed if an error is thrown in the `zama zama` block.

> [!NOTE]
> When in a chaai block, you have access to a special variable called

> [!WARNING]
> You cannot have a `zama zama` block without a `chaai` block. This is a design choice, and it makes sense.

> [!WARNING]
> You cannot put the `chaai` keyword underneath the end of a `zama zama` block, it HAS to be on the same line as the closing `<~` symbol. This makes it easier for me lol.

```jiv
zama zama ->
    @ block of code to try
<~ chaai ->
    @ block of code to execute if an error is thrown
<~
```

```jiv
zama zama ->
    khuluma(true + 1)!
<~ chaai ->
    khuluma(error)!
<~
```

### Error variable

When in a `chaai` block, Jaiva gives you access to a variable named `"error"` which holds the error that ocurred in string form however if there already is a variable named error, it will increment the new error variable by 1.

```jiv
zama zama ->
    khuluma(idk - 1)! @ throws error
<~ chaai ->
    zama zama ->
        khuluma("an error bruh" - idk)! @ another error
    <~ chaai ->
        khuluma(error)!     @ This is the (idk - 1) error
        khuluma(error1)!    @ This is this context's error (So the ("an error bruh" - idk) error)
    <~
<~
```

> [!NOTE]
> The way the code works, is it looks in it's current context for any variable which contains the exact word "error", so if you have like 4 variables named error, and you decide to open a chaai block, the name of the error variable will be "error4" (it counts the amount of current variables named error, and adds it to the variable name)
> Just keep this in mind
> This is defined in [Interpreter.java on line 499](/src/main/java/com/jaiva/interpreter/Interpreter.java)


# Tsea (import) and exporting files

Importing isn't that bad. Here's a simple import.

Say you have "`file.jiv`" with the following code.

```jiv
maak *a <- 10! @ Pre-pend * to the variable name to mark it as a exported variable.
kwenza *addition(param1, param2) -> @ Pre-pend * to the function name to mark it as a exported function.
    khuluma(param1 + param2)!
<~

@ Note: Using the variables/functions are still normal. When using them you don't have to prepend the * to them. It's just simply a marker for the tokenizer to know that this variable/function is exported.
```

Then in your main file, you can import it like this.

```jiv
tsea "file.jiv"! @ or you can have an absolute import.
khuluma(addition(a, 1))! @ 11
```

Or if you want to import a specific variable or function, you can do this.

```jiv
tsea "file.jiv" <- addition!
```

> [!NOTE]
> File paths are relative to the current working directory. So if you have a file in a different directory, you will have to use the full path to the file.

> [!NOTE]
> You can also import files from the Jaiva library. Example to import the arrays files
>
> ```jiv
> tsea "jaiva/arrays"!
> ```
>
> See [Globals](docs/Libraries.md) for a list of global variables and functions that are available to you, and others that you can import.

> [!NOTE]
> Omitting the file extension is okay, however it will default to `.jiv`


# Arrow Syntax Table

Just a nice list since if you havent noticed, i looove arrows

| Syntax | Description                        | Documentation                                 |
|--------|------------------------------------|-----------------------------------------------|
| `>`    | Greater Than                       | [Operators](#operators)                       |
| `<`    | Less Than                          | [Operators](#operators)                       |
| `>=`   | Greater Than or Equal              | [Operators](#operators)                       |
| `<=`   | Less Than or Equal                 | [Operators](#operators)                       |
| `>>`   | right shift                        | [Operators](#operators)                       |
| `<<`   | left shift                         | [Operators](#operators)                       |
| `>x`   | right (hex) shift                  | [Operators](#operators)                       |
| `<x`   | left (hex) shit                    | [Operators](#operators)                       |
| `<-`   | Assignment                         | [Assignment Operators](#assignment-operators) |
| `<-\|` | Array Variable Assignment          | [Assignment Operators](#assignment-operators) |
| `<==`  | Error Output Assignment            | [Assignment Operators](#assignment-operators) |
| `->`   | Block Open                         | [Blocks](#blocks)                             |
| `<~`   | Block Close                        | [Blocks](#blocks)                             |
| `$>`   | JDoc Param Separator               | [Jaiva Documentation](JDoc.md)                |
| `=>`   | Ternary Statement                  | [Ternary Ifs](#ternary-ifs)                   |
| `<\|`  | Colonize Loop Separator            | [Colonize Loops](#colonize-loops-for-loops)   |
| `\|>`  | Function Call Parameters Extension | [Argument Extensions](#argument-extensions)   |
| `?>`   | Shorthand If Statement             | [Shorthand ifs](#shorthand-ifs)               |

damn you made it to the end?
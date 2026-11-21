# v7.0.0 Modifiers, Function Links and Networking!

Like FUCK theres ALOTTT

## BREAKING!!!

- Changed from Java `21` to `25`, so now in order to run or use Jaiva from jitpack
you lowkey need Java 25. Sorry but `sealed` `permits` was calling my name.
- Changed from ytils `1.2.*` to `2.0.0`, Any stuff improting Jaiva and relying on it's
dependency on ytils has changed.
- Symbol names can no longer contain the `\` character as now it serves a purpose.
- Remove `-jg/--json-with-globals`, and make `-j/--json` identical to streamer json output.
- Add error for when a block has no code (Completely whitespace). This really isn't breaking because idk 
why you would rely on this...
- `file/query` moved to `file/text/query`
- Renamed `file/api` and all sub libraries to `file/text` to better represent that they are text based file-related api
> | Old `file/api` import       | New Import                   |
> |-----------------------------|------------------------------|
> | `tsea "jaiva/file/api"`       | `tsea "jaiva/file/text"`       |
> | `tsea "jaiva/file/api/query"` | `tsea "jaiva/file/text/query"` |

## New Featurez

### Symbol Modifiers

Copied from [Language.md Section of Modifiers](docs/Language.md#modifiers)


Assuming you've read and understood Variable and Function definitions, modifiers are
special definition-time "properties" of a symbol. They are usually in a `[]` list before
the name of a variable or function.

```jiv
maak [frozen] x <- 10! @ Now x cannot be edited.

kwenza [frozen] function() -> @ And now function cannot be edited.
    khuluma("hello world.")! 
<~

kwenza func([const] param) -> @ Function parameter with modifier
    idk!
<~
```

> For a table of function parameter typings and where to put the modifer,
> it's always first.
>
> | Function Parameter Example     | Example with modifier |
> |--------------------------------|-----------------------|
> | `param` (normal)               | `[mod] param`         |
> | `<-param` (var args)           | `[mod] <-param`       |
> | `param?` (optional)            | `[mod] param?`        |
> | `F~param` (`F~` or `V~` sigil) | `[mod] F~param`       |
> | `V~param?` (combination)       | `[mod] V~param?`      |

It can either be a comma separated list, or space separated (braces are compulsory)
however if a comma is present it will use that. Allowing input with spaces


```jiv
maak [example mod mod2] y <- 2! @ "example" "mod" and "mod2"
maak [example mod, mod2] x <- 10! @ "example mod" and "mod2"
```

Jaiva provides a small set of modifiers, currently some are a no-op but will have
the necessary documentation in due time.

Some modifiers cannot be paired with others (mutually exclusive)

| Modifier   | Aliases                  | Modifier Semantics                                                                          | Mutually Exclusive To |
|------------|--------------------------|---------------------------------------------------------------------------------------------|-----------------------|
| `"frozen"` | `"const" "constant" "f"` | Freezes the symbol at creation time meaning it cannot be reassigned.                        |                       |
| `"meta"`   | `"metadata"`             | Marks the symbol as metadata, implying it serves tooling purposes only and shouldnt be used |                       |


> [!NOTE]
> A host app might provide more modifier definitions unique to it's runs of Jaiva.

You need only put the modifier name or alias, putting both will issue a warning from da
interpreter.


#### Adding Modifiers To Existing Symbols

it is allowed with the following syntax:

```jiv
maak a <- 10! @ Say we define a to be 10!
[const] a <- a! @ This adds the const modifier to a, and sets it to itself.
```

_however_ it does change the semantics of reassignment a bit.

It is encouraged to read through [Reassignment](#reassignment) first

Because the thing is, adding modifiers does not add to the original reference
but instead supplies you with a **_clone/copy_** of the original value with the new modifiers
applied.

An example is as follows:

**Normal**
> ```jiv
> maak x <- 10!
> if (true;) ->
>   x <- 12!
>   khuluma(x)! @ Prints 12.
> <~
> khuluma(x)! @ Prints 12.
> ```


**With Modifier Copying Semantics**
> ```jiv
> maak x <- 10!
> if (true;) ->
>   [const] x <- 12!
>   khuluma(x)! @ Prints 12.
> <~
> khuluma(x)! @ Prints 10.
> ```

This is such that a child scope doesn't add a modifier that persists outside of it's own
scope.

> _But then how can we access the earlier symbol if it has the same name?_

Fret not, there is special syntax to do just that! Because applying modifiers to symbols
are the only thing that causes this, this syntax only applies to it.

Append `\` at the start of the reference (can apply multiple) to get it's parent.

> ```jiv
> maak x <- 10!
> if (true;) ->
>   [const] x <- 12!
>   khuluma(x)! @ Prints 12.
>   khuluma(\x)! @ Prints 10.
> <~
> khuluma(x)! @ Prints 10.
> ```

And a more complex example

```jiv
kwenza funcz([p] a) ->
    a <- 10!
    [cor] a <- f~() : 6!
    [cow] a <- aowa!
    khuluma(a)!      @ false
    khuluma(\a())!   @ 6
    khuluma(\\a)!    @ 10
<~
funcz(17)!
```

### Function Links

Copied from [Language.md Section of Function links](docs/Language.md#function-links)

Function links are pretty odd. But essentially think of it as a poor man's overload.

Say we have these 2 functions

```jaiva
kwenza handleJson(jsonInput) ->
    
    @ if the input is weird throw
    cima <== "Not JSON"!
    @ ...
<~
kwenza handleYaml(yamlInput) ->
    @ if the input is weird throw
    cima <== "Not YAML"!
    @ ...
<~
```

And you want to unify them you can use function links, which uses either the `||` or `&&` operator
to create a function which will:
- Attempt either one of the functions (Will attempt the one that matches the given input args)
- (Depdending on the operator) May fallback to the other if one fails

```jiv
maak link <- handleJson || handleYaml!
maak link2 <- handleJson && handleYaml!

link("uses: actions/setup-java@v6")!
link2("uses: actions/setup-java@v6")!
```

The difference being
- `link` will try to run the first pick, and if **any** error occurs, it will try the other one.
- `link2` will try to run the first pick, and only if too little or too many parameters were given will try the other.

hence in this case, `link` works by consuming the error produced by `handleJson` and `link2` will fail
and show the error that `handleJson` would've shown.

In the case of multiple links, The operators are **_left-associate_** and a function link
itself is defined as var args so it doesn't have as much precedence as another function
with more arguments.

e.g.

```jiv
kwenza ref1(a, b) ->
    khutla a + b!
<~

kwenza ref2(a, b, c) ->
    khutla a * b * c!
<~

kwenza ref3(a, b, c, d?) ->
    sif (typeOf(d) = "function") ?> cima <== "I dont want a function!"!
    khutla a + b / c + d? => 0 however d!
<~

kwenza ref4(a, b, c, F~d) ->
    khutla d(a, b) + c^2!
<~

maak ref <- ref1 || ref2 || ref3 || ref4!
@ This is identical to the commented out definition
@ maak ref <- ((ref1 || ref2) || ref3 ) || ref4!

khuluma(ref(1, 2))!                         @ Call 1 - 3
khuluma(ref(1, 2, 3))!                      @ Call 2 - 6
khuluma(ref(1, 2, 3, 4))!                   @ Call 3 - 4
khuluma(ref(1, 2, 3, f~(a, b) : a * b))!    @ Call 4 - 11

khuluma(ref)!

@ outputs the linked function which shows as either an intersection or union between 2 other functions.
@ F~f-linked(<-args?) -> (F~f-linked(<-args?) -> (F~f-linked(<-args?) -> (F~ref1(a, b) ∪ F~ref2(a, b, c)) ∪ F~ref3(a, b, c, d?)) ∪ F~ref4(a, b, c, F~d))
```

If we single out `Call 4`
- The function link sees `a function with var args (the inner function link)` `||` `ref4`
- `ref4` matches the exact input amount given, and hence will be preferred
- `ref4` runs to succession and hence returns.

You can change the ordering via grouping yourself. But man thats up to you
at that point.

### Parameter Extension Repeated Groups

[You already know where this is copied from.](docs/Language.md#repeated-groups)

One thing argument extensions have over normal function calls is you can reduce
multiple of the same parameter into a simple repeated addition.

E.g. I have
```jaiva
maak a <- hello("hi", "lol", "hi", "lol", "hi", "lol")!
```

you could go
```jaiva
maak a <- hello()!
    |> ("hi", "lol", "hi", "lol", "hi", "lol")!
```

or otherwise
```jaiva
maak a <- hello()!
    |> ("hi", "lol")!
    |> ("hi", "lol")!
    |> ( "hi", "lol")!
```

or expand it to single args which works its the exact same. but this pattern can be reduced to:

```jaiva
maak a <- hello()!
    |> [3] ("hi", "lol")!
```

where the number in brackets (a non negative, integer number that doesnt have to be parsed to be recognized)
represents the amoutn of times the group will repeat in the function call.

### New Array Operators

[Yup. copied form here](docs/Language.md#array-related)


provided `arr` is defined as:

```jaiva
maak arr <-| 1, 3, 10, "fwe", aowa, arrLit()!
@ Or using arrLit only
maak arr <- arrLit(1, 3, 10, "fwe", aowa, arrLit())!

maak arr2 <-| 12, "fwe", 3, true!

khuluma(a)! @ Prints [1, 3, 10, "fwe", aowa, []]
```

1. Array sublist

   `1 - arr` _returns_ **`[3, 10, "fwe", aowa, []]`**

   `arr - 1` _returns_ **`[1, 3, 10, "fwe", aowa]`**

2. Repetition

   `arr * 2` _returns_ **`[1, 3, 10, "fwe", aowa, [], 1, 3, 10, "fwe", aowa, []]`**

   `2 * arr` _returns_ **`[1, 1, 3, 3, 10, 10, "fwe", "fwe", aowa, aowa, [], []]`**

4. Contains

   `arr ? "tri"` _returns_ **`false`** (checks if the left-hand array contaisn the right hand value inside it.)

   `arr ? false` _returns_ **`true`** (recall `false` and `aowa` are identical.)

5. Intersection

   `arr && arr2` _returns_ **`["fwe", 3]`** (The intersection between the 2 arrays.
   not sure about the order however it seems more dependant on RHS)

Also you can go nuts and compound them together

```jaiva
maak output <- 2 - arr - 2!
@ Output holds [3, 10, "fwe", aowa]
```

### `tseaf` function

Copied from [tseaf documentation](docs/libs/globals.md#tseaf)

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


Since Version: _7.0.0_


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

### New Libraries

#### `net` library

A new networking library, which currently has
- `net` - Aggregator
  - `net/interf` - Network Interfaces
    - `net/interf/query` - Query functions to get data from the arrays 
  - `net/tcp` - TCP connections
  - `net/const` - Networking constants when working with bytes

These libraries documentation can be found in [libs](docs/libs)
- [net](docs/libs/net.md)
- [net/tcp](docs/libs/net-tcp.md)
- [net/const](docs/libs/net-const.md)
- [net/interf](docs/libs/net-interf.md)
- [net/interf/query](docs/libs/net-interf-query.md)

There is also a (should be fully) worked example in [examples/net](examples/net)

Heres a snippet from [client.jiv](examples/net/client.jiv):

```jiv
tsea "jaiva/continuous"!
tsea "jaiva/file"!
tsea "jaiva/net"!
tsea "./shared"! @ Shared file

@* Client function. Its the client itself.
@* p        $> con <- F~any The connection function
@* p        $> conAddress <- string The connection address
@* p        $> conPort <- string The connection port
@* p        $> close <- F~any close function which closes
@*          $> this side of the socket when called.
kwenza client(F~con, conAddress, conPort, F~close) ->
    maak conn_eof <- con(C_EOF)! @ EOF function to set calback and check
    maak conn_closed <- con(C_CLOSED)! @ Same thing as EOF
    @ Handle errs
    kwenza erHandler(_, _2, type) ->
        @ we have conId and conAddress already.
        maak identifier <- "Server on " + conAddress!

        @ If its SELF, then this side was the one that closed via invoking the close() function
        if (type != "SELF") ->
            @ if i ever introduce a switch, itd naturally replace this.
            sif (type = "CONNECTION") ?> khuluma(identifier + " was closed.")!
            sif (type = "ERR") ?> khuluma(identifier + " crashe d suddenly!")!
            sif (type = "INPUT") ?> khuluma(identifier + " closed it's input stream!")!
            sif (type = "OUTPUT") ?> khuluma(identifier + " closed it's output stream!")!
        <~
	    close()! @ might as well close if this was invoked.
    <~

    conn_closed(erHandler)!
    conn_eof()! @ Rather use argument extensions here since its a long lambda.
        |> f~(_, _2) : khuluma("EOF was reached while reading.... oh no.")!

    zama zama ->
        @ read first packet
        maak greet <- readPacket(con)!
        khuluma("Received from server " + greet)!
        khuluma()!
        @ and ig do other stuff. Maybe listen to the console's systme in so we can
        @ send to server
        nikhil ((conn_closed() || conn_eof())') ->
            khuluma("Send to server: ", true)!
            maak out <- mamela()! @ listen to da input blocking.
            sif (out = "exit" || out~ = 0) ?> voetsek!
            sendPacket(con, out)!
            khuluma()!
        <~
	<~ chaai ->

        @ if the connection closed, then this is a false alarm.
        @ EOF is supposed to
        sif (conn_closed() || conn_eof()) ?> khutla idk!
	    khuluma("Connection to " + conAddress + " fucking died...: " + error)!

	<~


	khuluma("Closing communication with " + conAddress)!

	@ tcp_handshake provides a convenience for this
    @ con(C_CLOSE)()!
	@ with the close parameter.
	close()!
<~

zama zama ->
    @ could use "localhost" but im testing whether this does return 10.0.0.17
	tcp_handshake(net_localIpv4(), 5050, client)!
<~ chaai ->
	khuluma("Couldn't connect to server or some other error. " + error)!
<~
```

#### `reflect` library

A rather unsafe library for either changing properties of variables/functions on
the fly or querying data. e.g.

```jiv
tsea "jaiva/reflect" <- r_sUnfreeze!

r_sUnfreeze(khuluma)!

khuluma <- 1!

@ Congratulations. You do not have a print function anymore.
```

[Reflect docs](docs/libs/reflect.md)

### Mojo Features

Literally not that deep, but added a new `<skip>` field to both mojos to skip
execution, so you dont have to rebuild those every single time you package.

## Updates

- `a_push`, `a_pushAll`, `a_unshift` have been deprecated, they are still available but
their documentation in `arrays` library explains why (array spreading + arrLit is fundamentally better)
> | `jaiva/arrays` function                   | `arrLit` + spreading alternative          |
> |-------------------------------------------|-------------------------------------------|
>  | `a_push(arr, element)`                    | `arrLit(arr:::, element)`                 |
> | `a_pushAll(arr, element1, element2, ...)` | `arrLit(arr:::, element1, element2, ...)` |
> | `a_unshift(arr, element)`                 | `arrLit(element, arr:::)`                 |

- Completely refactor scope traces such that they show the file of origin (Some scopes yes are made as a result of
the file you are executing but may be defined in another file or in a library. This changes makes it explicit
to know whether a scope issue was your fault or an import's fault.)
- `d_link` has been deprecated and is now replaced by the global version named `linker` (No longer requires importing `jaiva/debug`)
- All Jaiva repositories have been moved to the [Jaiva-Lang](https://github.com/jaiva-lang) org.
  - [jaiva-lang/jaiva](https://github.com/jaiva-lang/jaiva)
  - [jaiva-lang/vscode](https://github.com/jaiva-lang/vscode)
  - [jaiva-lang/syntax](https://github.com/jaiva-lang/syntax)
  - [jaiva-lang/highlightjs](https://github.com/jaiva-lang/highlightjs)
- JDoc comments which are given to a variable that holds a lambda, is now inherited by that same lambda.
  (Consistency)
- `sif` now supports parameter extending and `TIfStatement` JSON output has a new `isShorthand` field.
> ```jiv
> sif (x;) ?> func()!
> |> (x, y, z)!
> ```

## Fixes

- Using and compounded post fix operators do not require a brace anymore.
- Fixed JSON output of libraries which make use of aliasing, not working lol.
- Fix file md output formatting (didn't call .trim())
- Fix some runtime exceptions and make them actual proper stuff (some)
- Fix stupid vscode related problems that jaiva introduced into it (JSON output related)
- Fix bug with ternaries
- Fix bug where variable reassignment collided with lambdas.
- Fix `TIfStatement` json not having `elseBody`

## Nerd Corner

- Made `ContinuousFunction` support input & output simultaneously for TCP. (File libraries
still use the constructors where it's only input or output)
- Added `x?'` to become `x != idk` as a direct fast path in tokenisation.
- Deprecate internal APIs. Like Moving away from long `BaseFunction.call` calls to using the `CallProperties`
enum as a single parameter.
- Started making extensive use of IntelliJ annotations
- Encapsulate some fields and make a lot of classes final. (This project started when i was still pretty
fresh to java and hence had no care in the world for immutable stuff.)
- Sealed a fuck ton of classes.
- Revamp all JSON output to be map based (Via `ytils` SimpleJSON)
- introduce a new exception under `JaivaException` named `LoadException` being anything related to loading
a library. This can be via a host app or when using `tsea`/`tseaf` which may wrap or throw these exceptions.
- Function parameters no longer store 2 parallel arrays `String[]` and `ArrayList<Boolean>` and instead store a single `ArrayList<FunctionArg>`
parameter list, allowing for parameters to hold their own contents.
- `TCodeblock` has been moved out of the `TokenDefault` hierarchy and renamed to `CodeRegion`
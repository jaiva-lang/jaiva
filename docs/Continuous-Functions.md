# Continuous Functions

This won't make sense if you don't have the basic understanding of [Jaiva Functions](Language.md#functions)

Unlike normal functions, a _**continuous**_ function provides a persistent closure over a resource (A resource being
streamed file input, a network connection (soon), etc)

```jaiva
@ Given "function" has some internal resources such as an open file we can read via a continuous function
maak cont <- function()! @ cont is the reference to the continuous function

@ Get the read function
maak read <- cont("read")!
read()! @ Read

cont("close")()! @ Close and free resources
```

> [!NOTE]
> Instead of passing magic strings, you can se the constants defined in [jaiva/continuous](libs/continuous.md) library

They specifically, return smaller closures over specific "tasks" relevant to the resource, but are ultimately still
responsible for managing said resource.

## Closures

This is the list of smaller closures which a Continuous Function _may_ provide.

| `id`     | Description                                                                                                                 | Closure Return Type          |
|----------|-----------------------------------------------------------------------------------------------------------------------------|------------------------------|
| `read`   | Returns the closure associated with reading input                                                                           | Any. Depends on the provider |
| `write`  | Returns the closure associated with writing output                                                                          | `idk`                        |
| `flush`* | Returns the closure which flushes buffered output resources                                                                 | `idk`                        |
| `eof`*   | Returns the closure which signifies the end of data or a stream. In some cases given input will allow you to set a callback | `boolean` or `idk`           |
| `close`  | Returns the closure which ends the data stream and relinquishes the resources to avoid resource leaks                       | `idk`                        |
| `closed` | Returns the closure which signifies the end of some thing* In some cases given input will allow you to set a callback       | `boolean` or `idk`           |

> [!NOTE]
> `flush` is only relevant if the continuous function is of an output related resource, and similar to `eof` for an input
> however that's more provider's implementation based.

If a hypothetical function `foo` from some internal package say

```jaiva
maak conf <- foo()!
```

listens to incoming network requests via a continuous function
then it is implied calling `foo()` will return a continuous function relating to `read` related resources. Hence:

```jaiva
maak read <- conf("read")!
```

will return `foo`'s incoming network request reader function. Unless `foo` specifies, `write` has no semantic meaning and will
do nothing.

---

## Provider Implementation

The provider of the Continuous Function is responsible for what certain functions actually mean, in that the `write` function
made by one continuous provider may not have the exact same semantics as another `write` continuous provider.


e.g. Take the [f_cwriter](libs/file-bytes.md#f_cwriter) from [jaiva/file/bytes](libs/file-bytes.md):

```jaiva
tsea "jaiva/file/bytes"!
tsea "jaiva/continuous"!

maak continuous <- f_cwriter("... filePath")!

maak write <- continuous(C_OUTPUT)!        @ Get the (output) writer

write(W_INT, 10)!                           @ Write the integer 10
write(W_MOD_UTF8, "Hello")!                 @ Write the string "Hello" as (Java Modified) UTF-8

continuous(C_FLUSH)()!                      @ Get and immediately invoke the flush closure
continuous(C_CLOSE)()!                      @ Get and immediately invoke the end closure to free resources as we are done
                                            @ writing to the file.
```
In this case `f_cwriter` returns a continuous function which:
- Is of **_write_** semantics (writing to a file), hence calling `read` will be useless.
- Enforces that the returned `write` closure, takes at least 2 arguments. The type to write, and the actual input.
- Provides an implementation of `flush` as this is an output resource (that is buffered+)

Whereas it's opposite in the same library [f_creader](libs/file-bytes.md#f_creader) uses the reader version:

```jaiva
tsea "jaiva/file/bytes"!
tsea "jaiva/continuous"!

maak continuous <- f_creader("... filePath")!

maak read <- continuous(C_INPUT)!           @ Get the (input) reader
maak eof <- continuousI(C_EOF)!             @ Get the EOF function (Returns true, if EOF was reached)

nikhil (eof()') ->
    khuluma(read(R_BYTE))!                  @ Keep reading and printing bytes until EOF is reached
<~

continuous(C_CLOSE)()!                      @ Close and free resources again
```

here, `f_creader` returns a continuous function which:
- Is of **_read_** semantics (reading a file), hence calling `write` will be useless.
- Enforces that the `read` closure must take at a minimum, 1 argument. The type to read the next byte/byte(s) as
- Provides an implementation of `eof` which when the end of the file is reached, the function will
return true.

## Limitation

A Jaiva script can't itself make these types of functions, but a Jaiva scirpt may be provided these
types of functions for input which may or mat not.

---

## Future?

So far, they've been integrated with reading and writing file data. Maybe one day they'll also coe for
- networking
- some other io stream based shi
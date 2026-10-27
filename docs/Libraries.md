# Libraries

These a symbols (variables and functions) that are either avaiable in the global scope, or need to be imported from their respected libraries.

Because why not, in this file, the symbols will contain a link to where exactly it's defined and/or imported from.

## Built-in

These are any symbols that are available in any Jaiva program. (Typically called globals)

These can be found in [globals](libs/globals.md) documentation.

## Libraries

These symbols, on the other hand, need to be imported from their respective libraries.

Some either exist only within Java, while others are implemented in Jaiva itself.

See [Importing and Exporting](../README.md#tsea-import-and-exporting-files) for more information on how to import libraries.

But it's generally:

```jiv
tsea "jaiva/(module name)"!
```

> [!NOTE]
> Even though all jaiva files have the extensions `.jiv`, `.jaiva` and `.jva`, All of them (The library files) have the `.jiv` extension. This is just the convention i decided to use.

> [!NOTE]
> Unlike built-in symbols where I can link you to implementation, I cannot do that for libraries because they are not part of the Jaiva source code, but rather part of the downloadable zip package. (The bin)

The following are the libraries that are available: 

- [types](libs/types.md)
  - [types/numbers](libs/types-numbers.md)
- [arrays](libs/arrays.md)
- [file](libs/file.md)
  - [file/text](libs/file-text.md)
    - [file/text/query](libs/file-text-query.md)
  - [file/bytes](libs/file-bytes.md)
    - [file/bytes/const](libs/file-bytes-const.md)
- [math](libs/math.md)
  - [math/const](libs/math-const.md)
  - [math/trig](libs/math-trig.md)
  - [math/base](libs/math-base.md)
- [threads](libs/threads.md)
- [continuous](libs/continuous.md)
- [time](libs/time.md)
  - [time/api](libs/time-api.md)
  - [time/zone](libs/time-zone.md)
- [debug](libs/debug.md)
- [net](libs/net.md)
  - [net/const](libs/net-const.md)
  - [net/tcp](libs/net-tcp.md)
  - [net/interf](libs/net-interf.md)
    - [net/interf/query](libs/net-interf-query.md)

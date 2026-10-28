# v7.0.0

breaking change : Switch from Java 21 to Java 25

Literlaly just plumbing

- All Jaiva repositories have been moved to the [Jaiva-Lang](https://github.com/jaiva-lang) org.
  - [jaiva-lang/jaiva](https://github.com/jaiva-lang/jaiva)
  - [jaiva-lang/vscode](https://github.com/jaiva-lang/vscode)
  - [jaiva-lang/syntax](https://github.com/jaiva-lang/syntax)
  - [jaiva-lang/highlightjs](https://github.com/jaiva-lang/highlightjs)
- Fix stupid vscode related problems that jaiva introduced into it (JSOn output)
- Use ytils 1.3.1 SimpleJSON and refactor all tokens to use it
- Start adding IntelliJ Annotation
- Deprecate internal APIs
- Remove `-jg/--json-with-globals`, and make `-j/--json` identical to streamer json output.
- Revamp all JSON output to be map based (Via ytils SimpleJSON)
- Encapsulate some fields and make a lot of classes final
- Ponder switching from Java 21 to Java 25 for sealed permits syntax (Jaiva 7...)
- Fix aliased library symbols not being aliased in JSON output
  - For LibrarySymbol, even if a symbol's canonical name is within the alias list already,
  it's removed and readded as the first entry to ensure it's always encountered first no amtter
  what.
- Fix file md output formatting (didn't call .trim())
- Fix some runtime exceptions (some)
- Added `linker` to global scope and in turn deprecated its old version `d_link` in `jaiva/debug`
- Added `tseaf` to import stuff via function
- New `net` library
  - New `net/tcp` (tcp connections)
  - New `net/const` (Constants for tcp and future net libs)
  - New `net/interf` (Network interfaces)
  - New `net/interf/query` (Querying properties of network interface array)
- Refactor scope
- Added parameter extending group repetition
- Made a JDoc comment who was given to a variable set to a lambda, also apply to that same lambda. (Tokenizer side)
- `sif` now supports parameter extending and `TIfStatement` JSON output has a new `isShorthand` field.
- New `reflect` library.
- `file/query` has moved to `file/api/query`
- Renamed `file/api` and sub libraries to `file/text` to better represent that they are text based file-related api
- Add error for when a block has no code.
- Fix `TIfStatement` json not having `elseBody`
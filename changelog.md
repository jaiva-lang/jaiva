# v6.2.0

Literlaly just plumbing

- All Jaiva repositories have been moved to the [Jaiva-Lang](https://github.com/jaiva-lang) org.
  - [jaiva-lang/jaiva](https://github.com/jaiva-lang/jaiva)
  - [jaiva-lang/vscode](https://github.com/jaiva-lang/vscode)
  - [jaiva-lang/syntax](https://github.com/jaiva-lang/syntax)
  - [jaiva-lang/highlightjs](https://github.com/jaiva-lang/highlightjs)
- Fix stupid vscode related problems that jaiva introduced into it (JSOn output)
- Use ytils 1.3.0 SimpleJSON and refactor all tokens to use it
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
- Added `linker` to global scope and in turn deprecated its old version `d_link`
- Added `tseaf` to import stuff via function
- New `net` library
  - New `net/tcp`
  - New `net/const`
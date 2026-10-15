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
- Deprecate intenral APIs
- Remove `-jg/--json-with-globals`, and make `-j/--json` identical to streamer json output.
- Revamp all JSON output to be map based (Via ytils SimpleJSON)
- Encapsulate some fields and make a lot of classes final
- Ponder switching from Java 21 to Java 25 for sealed permits syntax (Jaiva 7...)
- Fix aliased library symbols not being aliased in JSON output
- Fix file md output formatting (didn't call .trim())
- Fix some runtime exceptions (some)
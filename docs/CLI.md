# CLI

## `jaiva.cmd` (batch) / `jaiva` (bash)

This simply runs the actual `jaiva.jar`

See [Install](./Install.md) for install instructions

Simple CLI Ngl.

```sh
> jaiva [-p | -h | -v | -t | -u | -js]
> jaiva <filePath> [-j | -s | -jog | -md]
```

1. (No flags) launches the REPL.

    ```sh
    > jaiva
    ```

2. Get help instructions.

    ```sh
    > jaiva -h
    > jaiva --help
    ```

3. Launches REPL but also prints tokens only (more for debugging purposes)

    ```sh
    > jaiva -p
    > jaiva --print-tokens
    ```

4. Print the version of Jaiva.

    ```sh
    > jaiva -v
    > jaiva --version
    ```

5. Run's a test command i have set. Because why not

    ```sh
    > jaiva -t
    > jaiva --test
    ```

6. Get update instructions. (will direct you to [jaiva-install](#jaiva-installcmd-batch--jaiva-install-bash))

    ```sh
    > jaiva -u
    > jaiva --update
    ```

7. Enter JSON Streamer Mode. See [Streamer Mode](#streamer-mode)

   > ```shell
   > jaiva -js
   > jaiva --json-stream
   > ```

### `jaiva path`

`<filePath>` isn't necessarily a file path for every single output. it depends which

1. Run Jaiva file, (No flags.)
    ```sh
    > jaiva <filePath>
    ```
2. Returns tokens as strings.
    ```sh
    > jaiva <filePath> -s
    > jaiva <filePath> --string
    ```
3. Return tokens of the given file in JSON format.

    ```sh
    > jaiva <filePath> -j
    > jaiva <filePath> --json
    ```
   
   > [!NOTE]
   > This is the file's JSON representation without any
   > interpretation.

4. Return tokens of the given library file in JSON format

    ```sh
    > jaiva <filePath> -jog
    > jaiva <filePath> --json-of-globals
    ```
   
   where `<filePath>` is the full qualified jaiva import path like `jaiva jaiva/arrays -jog`

   > [!NOTE]
   > unlike the other JSON outputs which are a top level array, this one returns an object
   > with 2 properties:
   > ```json5
   > {
   >    "version": "string", // The version of jaiva this was exported with
   >    "tokens" : [] // The actual tokens list.
   > }
   > ```

5. Run Jaiva file (No flags.), however if there are any errors, throw the entire Java stack trace

    ```sh
    > jaiva <filePath> -is
    > jaiva <filePath> --include-stacks
    ```

6. Enable debugging mode. See [Debugger Commands](#debugger-commands)

    ```sh
    > jaiva <filePath> -d
    > jaiva <filePath> --debug
    ```

7. Export library as Markdown documentation

   > ```sh
   > jaiva <filePath> --markdown <outputFolder> -p:[opts]
   > jaiva <filePath> -md <outputFolder> -p:[opts]
   > ```

   > [!NOTE]
   > Where `<filePath>` can either be a file or a `jaiva/*` library
   > reference.

   > [!NOTE]
   > Where `[opts]` is a 5 character list of `+` or `-` which represents
   > the following positional paraemeters. (Where `+` is true, and `-` is false)
   >  - `includeTableOfContents` : Whether the markdown should include a table of contents
   >  - `splitSymbols` : Whether the single Symbols section should instead be split into Functions and Variables
   >  - `preferFunctions`: If `splitSymbols` is true, prefer the functions section before variables
   >  - `declareExports` : If is a library which is attached to a class, declare it's exports
   >  - `listExports` : If `declareExports`, further list all the symbols which aren't apart of this library.

## Debugger Commands

Good luck getting the debugger to work reliably. It's pretty buggy.

The debugger commands are available when the `-d` or `--debug` flag is used. The following commands are available:

1. Toggle breakpoints

```sh
> breakpoint <subcommand> [arg]
> bp <subcommand> [arg]
```

_Subcommands:_

```sh
- add <line_number> : Add a breakpoint at the specified line number.
- remove <line_number> : Remove a breakpoint at the specified line number.
- list : List all breakpoints.
- clear : Clear all breakpoints.
```

2. Start the debugger

```sh
> start
```

3. Step through code (Skips the current line and moves to the next one)

```sh
> step
```

4. Continue execution until the next breakpoint or end of file

```sh
> continue
> cont
```

5. Pause the debugger

```sh
> pause
```

6. Exit the debugger

```sh
> exit
> quit
```

7. Manage the variable functions hashmap (vfs):

```sh
> vfs <subcommand> [arg]
```

_Subcommands:_

```sh
- get <variable_name> : Get the value of a variable from the vfs.
- dump [all] : Dump the current variable functions hashmap (vfs).
    - If 'all' is specified, it will show all symbols, otherwise only show user defined symbols.
```

8. Print help text

```sh
> help
> h
```

9. Print stack trace
```sh
- stack trace
- st
- scope
```

## Streamer Mode

This mode of the CLI is mostly used in the backend by the [Jaiva VSCode Extension](https://github.com/yetnt/jaiva-vscode)
to enable a continuous talk with Jaiva.

Once enabled, the CLI will always wait for input on what to do. Even if it itself fails it 
will continue to wait until a new line is printed or `EXIT` is printed.

The whole purpose is tooling so it's responses are in JSON.

There is not specific syntax or arguments, rather it's just input the CLI takes so the following
example are after you've run the command to enable the mode.

### Continuous

This simply means, the streamer was run with no args and hence will be continuous

1. File Input

   ```shell
   C:/Users/Acer/...
   ```
   
   This simply provides the tokens of the file, unlike the other token providers, this is the
   actual tokens produced before interpreting

2. File Input & Interpretation

   ```shell
   C:/Users/Acer/...#INTERP
   ```

   This will further interpret the given file. The JSON output depends on the succession
   of the interpreter.

3. Exit

   ```shell
   EXIT
   ```
   
   or
   
   ```shell
   (empty line input)
   ```
   
   Exits the streamer.

### One-shot

One-shot simply means the streamer will immediately exit.

There is only a single one-shot input

```shell
jaiva --json-stream --exit <filePath>
```

Which returns the tokens of a file, without running the interpreter. The actual
tokens discovered.

This is just convenience for things such as importing in the VSCode Extension which cant go through
the same async/sync messy path.

### Streamer Output

1. Tokens Output
   ```
   {...}
   ```
   
2. Successful interpreter output

   ```json5
   {
      streamer: true,
      message: "", // doesnt matter in this case
      lineNumber: -1,
      warnings: [
        {
          "message": "", // The message of the warning encountered
          "lineNumber": 0 // The line numebr where this warning was encountered
        }
      ],
      scope: "scope string",
      type: "INTERP_SUCCESS"
   }
   ```

3. The Interpreter ran into an error (User-facing)

   ```json5
   {
      streamer: true,
      message: "Error Message",
      lineNumber: 0, // the error line number
      warnings: [
         {
            "message": "", // The message of the warning encountered
            "lineNumber": 0 // The line numebr where this warning was encountered
         }
      ],
      scope: "scope string",
      type: "ERR_INTERP"
   }
   ```

4. The interpreter fucking died 


   ```json5
   {
      streamer: true,
      message: "Error Message",
      lineNumber: 0, // the error line number
      warnings: [
         {
            "message": "", // The message of the warning encountered
            "lineNumber": 0 // The line numebr where this warning was encountered
         }
      ],
      type: "ERR_INTERP_DIED"
   }
   ```

5. The streamer itself somehow died

   ```json5
   {
      streamer: true,
      lineNumber: 0, // the error line number
      message: "",
      type: "ERR_STREAMER"
   }
   ```

6. The Tokeniser encountered an error

The streamer itself somehow died

   ```json5
   {
      streamer: true,
      lineNumer: 0, // the error line number
      message: "",
      type: "ERR_TOKENS"
   }
   ```

## `jaiva-install.cmd` (batch) / `jaiva-install` (bash)

Simple command on both windows and unix based systems

1. No Args

   ```shell
   jaiva-install
   ```
   
   This will query the github for the latest release, and if your current version
   is not the same, will update for you.


2. Explicit version

   ```shell
   jaiva-instal 4.0.0
   ```
   
   Will update/downgrade to that specific version of jaiva if the version exists and there is
   a valid `jaiva.zip` artefact available.

I've tested, you can install as far back as 1.0.0-beta.0 and back to v6. My script is insane wtf
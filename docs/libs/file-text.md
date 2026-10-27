# file/text (Library)

_The file text api for actually creating files and stuff yknow_
## Exports
This library exports the following libraries: 
- file/text/query

Exported Symbols Include: `f_nameOf`, `f_dirOf`, `f_contentOf`, `f_permsOf`

## Table of Contents
| Alias | Link |
| --- | --- |
| f_name | [f_name](#f_name) |
| f_dir | [f_dir](#f_dir) |
| f_this | [f_this](#f_this) |
| f_file | [f_file](#f_file) |
| f_new | [f_new](#f_new) |
## Functions

### f_file

This symbol can be reached by the following aliases: _`f_file`_

_**Finds the specified file and retrieves it's contents.**_


#### Definition

```jaiva
F~f_file(path)
```
- **_path_**  **`<-`** _**string**_
	 - _The path to the file you want to fetch._

_**Returns:**_ **Returns an array containing the properties of the file at the given `path` \n [fileName, fileDir, [contents], [canRead?, canWrite?, canExecute?]]**

Since Version: _1.0.1_


#### Example: 

```jaiva
maak file <- f_file("data/myFile.txt")!
khuluma("file name: " + file[0])!
khuluma("file directory: " + file[1])!
```


---


### f_new

This symbol can be reached by the following aliases: _`f_new`_

_**Creates a new file with the given properties at the given file.**_


#### Definition

```jaiva
F~f_new(path, content, canRead?, canWrite?, canExecute?)
```

- **_path_**  **`<-`** _**string**_
	 - _The path to the new file to create. Along with the file name and extension_
- **_content_**  **`<-`** _**idk**_
	 - _The content the file should hold. Either a string or an array of strings._
- **_canRead_** **`?`** **`<-`** _**boolean**_
	 - _Whether or not the file can be read. Defaults to true_
- **_canWrite_** **`?`** **`<-`** _**boolean**_
	 - _Whether the file can be written to or not. Defaults to true_
- **_canExecute_** **`?`** **`<-`** _**boolean**_
	 - _Whether the file can be executed or not. Defaults to true._

_**Returns:**_ **A boolean `true` if the file could be created. `false` otherwise.**

Since Version: _2.0.1_


#### Example: 

```jaiva
maak success <- f_new()!
<| "data/newFile.txt"!
<| arrLit("Hello, World!", "This is a new file.")!
<| (true, true, false)!
if (success) ->
    khuluma("file created successfully!")!
<~ else ->
    khuluma("Failed to createFunction file.")!
<~
```


---

## Variables

### f_name

This symbol can be reached by the following aliases: _`f_name`_

_**Variable that holds the current file's name**_


#### Definition
```jaiva
maak f_name <- (string)
```

> [!NOTE]
> _If you call this within the REPL, or somehow the filePath is null, it holds "file"_


Since Version: _1.0.0_


#### Example: 

```jaiva
if (f_name != "myFile.jiv") ->
    khuluma("This is not myFile.jiv!")!
<~
```


---


### f_dir

This symbol can be reached by the following aliases: _`f_dir`_

_**Variable that holds the current file's directory.**_


#### Definition
```jaiva
maak f_dir <- (string)
```

> [!NOTE]
> _If you call this within the REPL, or somehow the filePath is null, it holds "file"_


Since Version: _1.0.0_


#### Example: 

```jaiva
khuluma("Current file directory is: " + f_dir)!
```


---


### f_this

This symbol can be reached by the following aliases: _`f_this`_

_**Returns the current file's properties and contents.**_


#### Definition
```jaiva
maak f_this <-| (array)
```

> [!NOTE]
> _Returns an array containing the current file's properties \n [fileName, fileDir, [contents], [canRead?, canWrite?, canExecute?]]_


Since Version: _1.0.1_


#### Example: 

```jaiva
f_this[0]! @ holds the file name
f_this[1]! @ holds the file directory
khuluma("File contents: " + f_this[2])! @ holds the file contents as an array
khuluma("Can we write to this file? " + f_this[3][1])! @ holds the file permissions
```


---


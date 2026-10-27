# file/text/query (Library)

_Allows querying of file stuff from the structured array instead of manual indexing_
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| f_nameOf | [f_nameOf](#f_nameOf) |
| f_dirOf | [f_dirOf](#f_dirOf) |
| f_contentOf | [f_contentOf](#f_contentOf) |
| f_permsOf | [f_permsOf](#f_permsOf) |
## Functions

### f_nameOf

This symbol can be reached by the following aliases: _`f_nameOf`_

_**Gets the name of the file from a file.**_


#### Definition

```jaiva
F~f_nameOf(file)
```
- **_file_**  **`<-`** _**[]**_
	 - _The file array to get the name from._

_**Returns:**_ **The name of the file as a string.**

Since Version: _5.0.0_


#### Example: 

```jaiva
khuluma(f_nameOf(f_this))! @ Prints the name of the current file.
```


---


### f_dirOf

This symbol can be reached by the following aliases: _`f_dirOf`_

_**Gets the directory path of the file from a file.**_


#### Definition

```jaiva
F~f_dirOf(file)
```
- **_file_**  **`<-`** _**[]**_
	 - _The file array to get the directory path from._

_**Returns:**_ **The directory path of the file as a string.**

Since Version: _5.0.0_


#### Example: 

```jaiva
khuluma(f_dirOf(f_this))! @ Prints the directory path of the current file.
```


---


### f_contentOf

This symbol can be reached by the following aliases: _`f_contentOf`_

_**Gets the content of the file from a file.**_


#### Definition

```jaiva
F~f_contentOf(file)
```
- **_file_**  **`<-`** _**[]**_
	 - _The file array to get the content from._

_**Returns:**_ **The content of the file as an array of strings.**

Since Version: _5.0.0_


#### Example: 

```jaiva
khuluma(f_contentOf(f_this))! @ Prints the content of the current file.
```


---


### f_permsOf

This symbol can be reached by the following aliases: _`f_permsOf`_

_**Gets the permissions of the file from a file.**_


#### Definition

```jaiva
F~f_permsOf(file)
```
- **_file_**  **`<-`** _**[]**_
	 - _The file array to get the permissions from._

_**Returns:**_ **A closure over the permissions array allowing multiple ways to access the permissions without directly indexing into the array.**

> [!NOTE]
> _Unlike other functions, this one may require a bit of functional thinking. It returns a function that you can call to get specific permissions or all permissions at once. The function can take, a full string such as "read" or "r" and return whether that permission exists, An integer position [read, write, execute], or a Unix-like, 3 length permission string to return a boolean indicating that, that permission stirng matches the permission "rwx" or "r-x"_


Since Version: _5.0.0_


#### Example: 

```jaiva
maak permissions <- f_permsOf(f_this)!
khuluma(permissions())! @ Prints all permissions as an array [canRead, canWrite, canExecute]
khuluma(permissions("read"))! @ Prints whether the file can be read (true/false)
khuluma(permissions("r"))! @ Prints whether the file can be read (true/false)
khuluma(permissions(0))! @ Prints whether the file can be read (true/false)
@ Similarly for "write"/"w"/1 and "execute"/"x"/2
khuluma(permissions("r-x"))! @ Prints whether the file only has read and execute permission. (Unix like)
```


---

## Variables

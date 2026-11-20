
# Building Jaiva!

idk why you'd subject yourself to this

Requirements:

- [Java 25](https://www.oracle.com/java/technologies/javase/jdk25-archive-downloads.html)
- Maven 3.8+ (or an IDE with Maven support)
- An IDE, although you can build this purely in a terminal. Just more pain

## Steps

Clone the repo

```shell
git clone https://github.com/yetnt/jaiva
cd jaiva
```

Build the project using Maven:

```shell
mvn clean package
```

This will compile the project, run the tests
and create the packaged JAR in the `target/` directory.

## `-Prelease-cli`

If you wish to package with the `release-cli` profile, you need to create the following
folder/files first:

(since v6 the folder and files come pre-commited within `jaiva/core`, so you need not create them and can just run
the profile without worry.)

(inside `../jaiva/` directory)

```shell
mkdir jaiva-cli
cd jaiva-cli
```

Then create the following 2 files with the content

**`jaiva.cmd`**:
```cmd
@echo off
REM %~dp0 returns the drive letter and path of this script.
java -jar "%~dp0jaiva.jar" %*
```

**`jaiva`**
```shell
#!/bin/sh
# Determine the directory where this script is located.
SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"
# Execute the JAR with any passed arguments.
java -jar "$SCRIPT_DIR/jaiva.jar" "$@"
```

then 

```shell
cd ..
mvn package -Prelease-cli
```

to run the normal package goal but also
copy the `jaiva.jar` and `jaiva-sources.jar`
into the newly created folder and zip them together with the other files
for release.

Then you can change your `PATH` environment variable to instead
point to this for testing purposes if you'd like.
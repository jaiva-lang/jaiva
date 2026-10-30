# Hosting Jaiva in a Java app

Don't understand why you'd wanna do this. but alas Jaiva! allows this.

## Jaiva as a dependency

First off you obviously need to import Jaiva, and it's only
available as a Maven project.

The easiest way is to do it via `jitpack`

example pom

```xml
<project>

    ...
    
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.yetnt.jaiva</groupId>
            <artifactId>core</artifactId>
            <version>5.0.2</version> @ Or another version.
        </dependency>
    </dependencies>
    
    ...

</project>
```

then all of Jaiva is available via

```java
import com.jaiva.*;
```

## JBundler

JBundler, available at the top level, is the class which allows
you to tokenise and interpret a file without calling the jaiva
cli.

A general use case would be as follows:

```java

import com.jaiva.JBundler;
import com.jaiva.tokenizer.tokens.Token;

import java.util.ArrayList;

public class JBundlerExample {
    static void main(String[] args) {

        JBundler file1 = new JBundler("C:/Users/file.jiv");

        // Tokenise and interpreting the file in separate steps
        ArrayList<Token<?>> tokens = file1.tokenize();  // the tokens are also
                                                        // saved within its own instance field
                                                        // Calling file1.getTokens() will return
                                                        // a copy of the saved tokens
        file1.interpret(null);                          // Interpret the file and pass in a value
                                                        // that the entire Interpreter has access to.
        
        // Tokenise and interpret in one call.
        file1.run(null);
        
        // Tokenise and interpret any given file at any time
        JBundler other = new JBundler();
        
        other.execute(
                "C:/filePath.jiv",
                null,
                new ArrayList<>()   // Value to pass on to Jaiva that is itself a Jaiva Passable Type
                                    // (A jaiva file will be able to fetch this value via getCallerValue())
                                    // ArrayList, Integer, Double, Boolean, Long, String, BaseFunction and TVoidValue
        );
    }
}
```

and that's basically it for the simple process

### Passing Custom Libraries Classes

JBundler, in both constructors, allows you to pass a variable amount of args
of `Class` instances of custom `BaseLibrary` implementations which are unique to the host project.

This allows Jaiva to have domain specific libraries which can only be accessed if it was ran
via JBundler and not via the CLI.

I might make a markdown file for how you can make a library since its a lot of plumbing
but here's an example and how it goes into JBundler and how a jaiva script can import it

```java

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.libBuilders.func.*;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibraryType;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.jdoc.JDocBuilder;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;

import java.util.ArrayList;

@PublicLibrary(path = "namespace/reflective", description="Lol.")
public class ReflectiveLib extends BaseLibrary {

    public ReflectiveLib() {
        add(new FFunctionName()); // register the function within the library's vfs.
    }

    /**
     * Function example
     * This will be a function whose goal is to return the name of the
     * function object supplied to it.
     * <pre>{@code
     * maak j <- khuluma!
     * r_fName(j)! @ Returns khuluma
     * }</pre>
     */
    public static class FFunctionName extends BaseFunction {
        public FFunctionName() {
            super(
                    FunctionBuilder.start()
                            .name("r_fName") // full name of the function
                            .arguments(Arguments.getInstance().add(
                                    new AArgument(
                                            "func", "The function to reflect upon.", false, Argument.Type.FUNCTION
                                    )
                            ))
                            .docs(
                                    // Add JaivaDoc metadata
                                    JDoc.builder()
                                            .addDesc("Retrieves the name of a given function irrespective of it's alias in the current scope")
                                            .addReturns("The name of the function")
                                            .sinceVersion("1.0.0-alpha") // any version string you want.
                            )
            );
        }

        // The method to override to enable functionality

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            // This method checks that all required parameters and possible functional type hints were satisfied
            // Although if you defined it to be variadic, this isn't needed.
            checkParams(callProperties.toCheckParams());

            // Get the first value.
            
            Object value = params.getFirst();

            // Check whether its a function or not

            if (!(value instanceof BaseFunction baseFunction))
                throw new InterpreterException.WtfAreYouDoingException(
                        scope, "This function accepts only function references....", tFuncCall.lineNumber
                );
            
            return baseFunction.name;
        }
    }
}
```

then back in our lovely test

```java

import com.jaiva.JBundler;
import com.jaiva.tokenizer.tokens.Token;

import java.util.ArrayList;

public class JBundlerExample {
    static void main(String[] args) {

        JBundler file1 = new JBundler("C:/Users/file.jiv", FFunctionName.class);

        // Tokenise and interpreting the file in separate steps
        file1.run();
        
    }
}
```

then in `file.jiv`

```jiv
tsea "jaiva/namespace/reflective"! @ Still has jaiva/ appended before it.

maak print <- khuluma!

khuluma(r_fName(khuluma))!  @ Prints "khuluma"
print(r_fName(print))!      @ Prints "khuluma"
print(r_fName(r_fName))!    @ Prints "r_fName"
```

I'll make a tutorial on understanding Jaiva embedding better but this is it so far.
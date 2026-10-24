package com.jaiva.interpreter.libs.file;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.libs.file.api.FileType;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.jdoc.JDoc;

import java.util.ArrayList;

/**
 * Holds helper functions (that are part of the "file" library) that operate on FileType instances or likewise.
 * <p>
 * It is used to group related functions together under the "file" library namespace.
 * It isn't directly related to FileType instances but provides functionality for them (within a jaiva script).
 * </p>
 * <p>
 * Although the functions that live here operate on FileType instances, this should not be known
 * to the jaiva script user. They only see files as arrays with a specific structure.
 * This class simply provides a way to organize these functions within the library system.
 * </p>
 */
@JaivaLibrary(path = "file/query", description = "Allows querying of file stuff from" +
        " the structured array instead of manual indexing")
public class FileQuery extends BaseLibrary {
    public FileQuery() {
        add(new FNameOf(), new FDirOf(), new FContentOf(), new FPermsOf());
    }

    public static class FNameOf extends BaseFunction {
        public FNameOf() {
            super(
                    FunctionBuilder.start()
                            .name("f_nameOf")
                            .setLib("file/api")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument(
                                            "file", "The file array to get the name from.",
                                            false, Argument.Type.ARRAY
                                    )
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Gets the name of the file from a file.")
                                            .addReturns("The name of the file as a string.")
                                            .sinceVersion("5.0.0")
                                            .addExample("""
                                            khuluma(f_nameOf(f_this))! @ Prints the name of the current file.
                                            """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams()); // Ensure correct number of parameters
            Object fileObj = callProperties.params().getFirst();
            if (!(fileObj instanceof ArrayList<?> arr))
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(),
                        "The parameter 'file' needs to be an array like come on!", callProperties.getLineNumber());
            FileType file = FileType.of((ArrayList<Object>) arr, callProperties.scope(), callProperties.getLineNumber());
            return file.getFileName();
        }
    }

    public static class FDirOf extends BaseFunction {
        public FDirOf() {
            super(
                    FunctionBuilder.start()
                            .name("f_dirOf")
                            .setLib("file/api")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument(
                                            "file", "The file array to get the directory path from.",
                                            false, Argument.Type.ARRAY
                                    )
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Gets the directory path of the file from a file.")
                                            .addReturns("The directory path of the file as a string.")
                                            .sinceVersion("5.0.0")
                                            .addExample("""
                                            khuluma(f_dirOf(f_this))! @ Prints the directory path of the current file.
                                            """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams()); // Ensure correct number of parameters
            Object fileObj = callProperties.params().getFirst();
            if (!(fileObj instanceof ArrayList<?> arr))
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(),
                        "The parameter 'file' needs to be an array like come on!", callProperties.getLineNumber());
            FileType file = FileType.of((ArrayList<Object>) arr, callProperties.scope(), callProperties.getLineNumber());
            return file.getFilePath();
        }
    }

    public static class FContentOf extends BaseFunction {
        public FContentOf() {
            super(
                    FunctionBuilder.start()
                            .name("f_contentOf")
                            .setLib("file/api")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument(
                                            "file", "The file array to get the content from.",
                                            false, Argument.Type.ARRAY
                                    )
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Gets the content of the file from a file.")
                                            .addReturns("The content of the file as an array of strings.")
                                            .sinceVersion("5.0.0")
                                            .addExample("""
                                            khuluma(f_contentOf(f_this))! @ Prints the content of the current file.
                                            """)
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams()); // Ensure correct number of parameters
            Object fileObj = callProperties.params().getFirst();
            if (!(fileObj instanceof ArrayList<?> arr))
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(),
                        "The parameter 'file' needs to be an array like come on!", callProperties.getLineNumber());
            FileType file = FileType.of((ArrayList<Object>) arr, callProperties.scope(), callProperties.getLineNumber());
            return file.getFileContent();
        }
    }

    public static class FPermsOf extends BaseFunction {
        public FPermsOf() {
            super(
                    FunctionBuilder.start()
                            .name("f_permsOf")
                            .setLib("file/api")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument(
                                            "file", "The file array to get the permissions from.",
                                            false, Argument.Type.ARRAY
                                    )
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Gets the permissions of the file from a file.")
                                            .addReturns("A closure over the permissions array allowing multiple ways to access the permissions without directly indexing into the array.")
                                            .addNote("Unlike other functions, this one may require a bit of functional thinking. " +
                                                    "It returns a function that you can call to get specific permissions or all permissions at once. " +
                                                    "The function can take, a full string such as \"read\" or \"r\" and return whether that permission exists, " +
                                                    "An integer position [read, write, execute], or a Unix-like, 3 length permission string to return a boolean " +
                                                    "indicating that, that permission stirng matches the permission \"rwx\" or \"r-x\""
                                            )
                                            .sinceVersion("5.0.0")
                                            .addExample("""
                                            maak permissions <- f_permsOf(f_this)!
                                            khuluma(permissions())! @ Prints all permissions as an array [canRead, canWrite, canExecute]
                                            khuluma(permissions("read"))! @ Prints whether the file can be read (true/false)
                                            khuluma(permissions("r"))! @ Prints whether the file can be read (true/false)
                                            khuluma(permissions(0))! @ Prints whether the file can be read (true/false)
                                            @ Similarly for "write"/"w"/1 and "execute"/"x"/2
                                            khuluma(permissions("r-x"))! @ Prints whether the file only has read and execute permission. (Unix like)
                                            """)
                            )
            );
            freeze();
        }

        /**
         * Function returned by f_permsOf to get specific permissions.
         * This function doesn't actually require to have a name as it's created and immediately returned by f_permsOf.
         */
        public static class FPermissions extends BaseFunction {
            private final FileType file;

            public FPermissions(FileType file) {
                super(
                        FunctionBuilder.start()
                                .name("perms")
                                .setLib("file/api")
                                .arguments(
                                        Arguments.getInstance()
                                                .add(
                                                        new AArgument(
                                                                "perms", "The permission to get ('read'/'r'/0, 'write'/'w'/1, " +
                                                                "'execute'/'x'/2). If omitted, returns all permissions as an array.",
                                                                false, Argument.Type.ANY
                                                        )
                                                )
                                )
                                .docs(
                                        // docs really dont matter here its not discoverable as a closure.
                                        JDoc.builder()
                                                .addDesc("Gets specific permissions or all permission of the specified file.")
                                                .addReturns("The requested permission as a boolean, or all permissions as an array " +
                                                        "if no parameter is given.")
                                                .sinceVersion("5.0.0")
                                )
                );
                this.file = file;
                freeze();
            }

            @Override
            public Object call(CallProperties callProperties) throws Exception {
                checkParams(callProperties.toCheckParams());
                if (callProperties.params().isEmpty()) {
                    // Return all permissions as an array
                    return file.get(3);
                } else {
                    Object permObj = callProperties.params().getFirst();
                    if (permObj instanceof String permStr) {
                        if (permStr.length() == 3) {
                            boolean read = (permStr.charAt(0) == 'r') == file.canRead();
                            boolean write = (permStr.charAt(1) == 'w') == file.canWrite();
                            boolean execute = (permStr.charAt(2) == 'x') == file.canExecute();

                            return read && write && execute;
                        }
                        switch (permStr.toLowerCase()) {
                            case "read", "r" -> {
                                return file.canRead();
                            }
                            case "write", "w" -> {
                                return file.canWrite();
                            }
                            case "execute", "x" -> {
                                return file.canExecute();
                            }
                            default -> throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(),
                                    "Invalid permission string. Use 'read'/'r', 'write'/'w', or 'execute'/'x'.", callProperties.getLineNumber());
                        }
                    } else if (permObj instanceof Number permNum) {
                        int index = permNum.intValue();
                        switch (index) {
                            case 0 -> {
                                return file.canRead();
                            }
                            case 1 -> {
                                return file.canWrite();
                            }
                            case 2 -> {
                                return file.canExecute();
                            }
                            default -> throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(),
                                    "Invalid permission index. Use 0 for read, 1 for write, or 2 for execute.", callProperties.getLineNumber());
                        }
                    } else {
                        throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(),
                                "Permission parameter must be a string or number.", callProperties.getLineNumber());
                    }
                }
            }
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams()); // Ensure correct number of parameters
            Object fileObj = callProperties.params().getFirst();
            if (!(fileObj instanceof ArrayList<?> arr))
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(),
                        "The parameter 'file' needs to be an array like come on!", callProperties.getLineNumber());
            FileType file = FileType.of((ArrayList<Object>) arr, callProperties.scope(), callProperties.getLineNumber());
            return new FPermissions(file);
        }
    }
}

package com.jaiva.interpreter.libs.file.txt;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Scope;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Represents a file used by the Jaiva library "file" that's passed to the user.
 * <p>
 *     This class extends ArrayList to hold file data and metadata.
 *     [
 *     "filename.txt",          // file name
 *     "/p/t/o/",          // file path
 *     ["line1", "line2"], // file content as list of lines
 *     [canRead, canWrite, canExecute] // Permissions as booleans
 *     [sizeInBytes, lastModifiedTimestamp] // Additional metadata (Not implemented yet)
 *     ]
 * </p>
 * <p>
 *     To a user, a file is nothing but an array with specific structure and meaning.
 *     To the actual implementation, it's just an ArrayList with helper methods to access
 *     the data in a more readable way. The user sees an array (as ArrayList is what backs arrays in Jaiva)
 *     but the implementation can use this class to make working with files easier.
 * </p>
 */
public class FileType extends ArrayList<Object> {

    public static FileType of(FileCreator creator) {
        FileType f = new FileType();
        f.addAll(creator.toArrayList());
        return f;
    }
    /**
     * Factory method to create a FileType instance from an ArrayList.
     *
     * @param arr        The ArrayList containing file data and metadata.
     * @param ct         The current scope for error reporting.
     * @param lineNumber The line number for error reporting.
     * @return A FileType instance.
     * @throws InterpreterException If the structure of the array is incorrect.
     */
    public static FileType of(ArrayList<Object> arr, Scope ct, int lineNumber) throws InterpreterException {
        if (arr.size() < 4)
            throw new InterpreterException.WtfAreYouDoingException(ct,
                    "You lowkey gave an incorrect structure. (There needs to be at least 4 elements in the array).", lineNumber);

        if (!(arr.getFirst() instanceof String))
            throw new InterpreterException.WtfAreYouDoingException(ct,
                    "The first element (file name) needs to be a string.", lineNumber);

        if (!(arr.get(1) instanceof String) && !(arr.get(1) instanceof Path))
            throw new InterpreterException.WtfAreYouDoingException(ct,
                    "The second element (file path) needs to be a string.", lineNumber);

        if (arr.get(1) instanceof Path) {
            arr.set(1, arr.get(1).toString());
        }

        if (!(arr.get(2) instanceof ArrayList<?> linesArr))
            throw new InterpreterException.WtfAreYouDoingException(ct,
                    "The third element (file content) needs to be an array.", lineNumber);

        for (Object line : linesArr) {
            if (!(line instanceof String))
                throw new InterpreterException.WtfAreYouDoingException(ct,
                        "Each line in the file content array needs to be a string.", lineNumber);
        }

        if (!(arr.get(3) instanceof ArrayList<?> permsArr))
            throw new InterpreterException.WtfAreYouDoingException(ct,
                    "The fourth element (file permissions) needs to be an array.", lineNumber);

        if (permsArr.size() != 3)
            throw new InterpreterException.WtfAreYouDoingException(ct,
                    "The file permissions array needs to have exactly 3 elements (read, write, execute).", lineNumber);

        for (Object perm : permsArr) {
            if (!(perm instanceof Boolean))
                throw new InterpreterException.WtfAreYouDoingException(ct,
                        "Each permission in the file permissions array needs to be a boolean.", lineNumber);
        }

        FileType file = new FileType();
        file.addAll(arr);
        return file;
    }

    public String getFileName() {
        return (String) this.getFirst();
    }

    public String getFilePath() {
        return (String) this.get(1);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> getFileContent() {
        return (ArrayList<String>) this.get(2);
    }

    @SuppressWarnings("unchecked")
    public boolean canRead() {
        ArrayList<Boolean> perms = (ArrayList<Boolean>) this.get(3);
        return perms.getFirst();
    }

    @SuppressWarnings("unchecked")
    public boolean canWrite() {
        ArrayList<Boolean> perms = (ArrayList<Boolean>) this.get(3);
        return perms.get(1);
    }

    @SuppressWarnings("unchecked")
    public boolean canExecute() {
        ArrayList<Boolean> perms = (ArrayList<Boolean>) this.get(3);
        return perms.getLast();
    }

}

package com.jaiva.interpreter.libs.file.txt;

import java.util.ArrayList;

public class FileCreator {
    private String name;
    private String path;
    private ArrayList<String> content;
    private boolean canRead;
    private boolean canWrite;
    private boolean canExecute;

    /**
     * Creates a new FileCreator instance with default values.
     * This is mainly used when `f_this` is called within the REPL.
     */
    public FileCreator() {
        this.name = "REPL";
        this.path = "REPL";
        this.content = new ArrayList<>();
        this.canRead = false;
        this.canWrite = false;
        this.canExecute = false;
    }

    public ArrayList<?> toArrayList() {
        ArrayList<Object> arr = new ArrayList<>();
        arr.add(this.name);
        arr.add(this.path);
        arr.add(this.content);
        ArrayList<Boolean> perms = new ArrayList<>();
        perms.add(this.canRead);
        perms.add(this.canWrite);
        perms.add(this.canExecute);
        arr.add(perms);
        return arr;
    }

    public FileCreator setName(String name) {
        this.name = name;
        return this;
    }

    public FileCreator setPath(String path) {
        this.path = path;
        return this;
    }

    public FileCreator setContent(ArrayList<String> content) {
        this.content = content;
        return this;
    }

    public FileCreator setCanRead(boolean canRead) {
        this.canRead = canRead;
        return this;
    }

    public FileCreator setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
        return this;
    }

    public FileCreator setCanExecute(boolean canExecute) {
        this.canExecute = canExecute;
        return this;
    }
}

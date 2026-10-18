package com.jaiva.interpreter.libs;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Context;
import com.jaiva.interpreter.Interpreter;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.runtime.ImportVfs;
import com.jaiva.interpreter.runtime.ResourceLoader;
import com.jaiva.tokenizer.TConfig;
import com.jaiva.tokenizer.Tokenizer;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TImport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public final class ExternalLibraryLoader {
    private static final String LIB_DIR = "lib/";
    private static final ArrayList<String> libFiles = new ArrayList<>(Arrays.asList(
            "arrays.jiv"
            // and more, probably not but whatever.
    ));

    public HashMap<String, Vfs> loadAllLibraries(IConfig<Object> config) throws InterpreterException.LibImportException {

        HashMap<String, Vfs> imported = new HashMap<>();

        for (String libFile : libFiles) {
            try {
                String fullPath = LIB_DIR + libFile;
                String content = ResourceLoader.getResourceAsString(fullPath);
                Vfs vfs = loadLibrary(content, libFile, fullPath, config);
                imported.put(libFile.substring(0, libFile.indexOf('.')), vfs);
//                System.out.println("✓ Loaded library: " + libFile);
            } catch (Exception e) {
                throw new InterpreterException.LibImportException(new Scope(config), libFile);
            }
        }

        return imported;

    }

    public Vfs loadLibrary(String path, IConfig<Object> config) throws InterpreterException.LibImportException {
        if (!libFiles.contains(path))
            throw new InterpreterException.LibImportException(new Scope(config), path);

        try {
            String content = ResourceLoader.getResourceAsString(LIB_DIR + path);
            String name = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : LIB_DIR + path;
            return loadLibrary(content, name, LIB_DIR + path, config);
        } catch (Exception e) {
            throw new InterpreterException.LibImportException(new Scope(config), path);
        }
    }

    private Vfs loadLibrary(String content, String name, String path, IConfig<Object> config) throws Exception {
//        System.out.println("Loading " + name + " (" + content.length() + " chars)");

        ArrayList<Token<?>> tks = (ArrayList<Token<?>>) Tokenizer.readLine(content, "", null, null, -1, new TConfig());

        IConfig<Object> newConfig = new IConfig<Object>(config.getSanitisedArgs(), path
        );

        newConfig.setImportVfs(new ImportVfs(true)); // This tells the interpreter to only parse exported symbols. (Functions
        // and variables)

        Vfs vfsFromFile = ((Vfs) Interpreter.interpret(tks, new Scope(Context.IMPORT,
                new TImport(path, name, true, -1), new Scope(config)), newConfig));

        Globals g = new Globals(config);
        ArrayList<String> keys = new ArrayList<>(g.getVfs(config, g).keySet());

        for (String key : keys)
            vfsFromFile.remove(key);

        return vfsFromFile;

    }
}

package com.jaiva.interpreter.libs;

import com.jaiva.Main;
import com.jaiva.errors.JaivaException;
import com.jaiva.errors.LoadException;
import com.jaiva.interpreter.MapValue;
import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.libs.annotation.*;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.interpreter.symbol.SymbolType;
import com.jaiva.tokenizer.NewJson;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.specific.TokenDefault;
import com.yetnt.utils.builders.SimpleJSON;
import com.yetnt.utils.collection.HashMultiMap;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * A library. im not documenting bro this has so many shits
 */
public class BaseLibrary {

    // this base library was imported from something else
    private ImportChain importChain;

    // Promise that this vbase library will import something else
    private final ArrayList<ImportPromise> importPromises = new ArrayList<>();

    private final ArrayList<LibrarySymbol> symbols = new ArrayList<>();

    /**
     * Default Constructor.
     */
    public BaseLibrary() {
    }

    public BaseLibrary(IConfig<Object> config) {
        // This is just for reflection purposes.
    }

    private void addToImportChain(ImportChain existing, Class<? extends BaseLibrary> incident) {
        importChain = new ImportChain(incident, importChain == null ? Optional.empty() : Optional.of(existing));
    }

    private void addImportPromise(ImportPromise importPromise) {
        importPromises.add(importPromise);
    }

    protected void add(Symbol ...syms) {
        Arrays.stream(syms).forEach(symbol -> symbols.add(new LibrarySymbol(symbol)));
    }

    protected void add(@NotNull BaseLibrary bis) {
        symbols.addAll(bis.getSymbols());
    }

    protected void addWithAliases(Symbol symbol, String... aliases) {
        symbols.add(new LibrarySymbol(symbol, aliases));
    }

    public Vfs getUniqueVfs() {
        Vfs vfs = new Vfs();
        symbols.forEach(vfs::putAsSymbolName);
        return vfs;
    }

    @NotNull
    public Vfs getVfs(IConfig<Object> config, Globals globals) throws JaivaException {
        Vfs vfs = getUniqueVfs();
        if (importPromises.isEmpty()) {
            return vfs;
        }

        HashMultiMap<Class<? extends BaseLibrary>, ImportPromise> exportClaims = new HashMultiMap<>();

        exportClaims.put(this.getClass(), importPromises);

        for (ImportPromise importPromise : importPromises) {
            // Get the promised import class to import
            Class<? extends BaseLibrary> toImport = importPromise.classToImportFrom();
            // check this current if the lib is in there
            if (importChain != null && importChain.getClassesInChain().contains(toImport))
                throw new LoadException.ExportException(
                        getClass(), "Circular dependency caught within " + toImport.getSimpleName() + " and " + this.getClass().getSimpleName()
                );

            BaseLibrary lib = instantiate(toImport, config, globals);
            ArrayList<ImportPromise> libImportPromises = lib.getImportPromises();
            AtomicReference<HashSet<Class<? extends BaseLibrary>>> intersected = new AtomicReference<>();

            Class<? extends BaseLibrary> alreadyExported = exportClaims.findFirstKeyWhere(
                    (ArrayList<ImportPromise> values) -> {
                        HashSet<Class<? extends BaseLibrary>> i
                                = ImportPromise.intersect(importPromises, libImportPromises);
                        if (i.isEmpty()) return false;
                        intersected.set(i);
                        return true;
                    }
            );

            if (alreadyExported != null) {
                HashSet<Class<? extends BaseLibrary>> interseciton = intersected.get();
                throw new LoadException.ExportException(getClass(),
                        toImport.getSimpleName() + " attempted to export " +  interseciton.toString()
                        + " however it has already been exported by " + alreadyExported.getSimpleName()
                );
            }

            exportClaims.put(toImport, libImportPromises);

            lib.addToImportChain(importChain, getClass());
            // otherwise, get the vfs
            vfs.putAll(lib.getVfs(config, globals));
        }

        return vfs;
    }

    @SuppressWarnings("unchecked")
    protected static BaseLibrary instantiate(
            @NotNull Class<?> clazz,
            IConfig<Object> config,
            Globals globals
//            ,
//            ClassLoader cl
    ) throws JaivaException {
        BaseLibrary libraryInstance;
            try {
                Constructor<? extends BaseLibrary> constructor = (Constructor<? extends BaseLibrary>) clazz.getConstructor(IConfig.class);
                constructor.setAccessible(true); // In case the constructor is not public
                libraryInstance = constructor.newInstance(config);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new LoadException.InvalidLibraryClassException(
                        clazz, e
                );
            } catch (NoSuchMethodException e) {
                try {
                    Constructor<? extends BaseLibrary> constructor = (Constructor<? extends BaseLibrary>) clazz.getConstructor();
                    constructor.setAccessible(true); // In case the constructor is not public
                    libraryInstance = constructor.newInstance();
                } catch (NoSuchMethodException ex) {
                    throw new LoadException.InvalidLibraryClassException(clazz, "No constructor with or without IConfig exists to instantiate.");
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e2) {
                    throw new LoadException.InvalidLibraryClassException(
                            clazz, e2
                    );
                }
            }

        // check for Exports annotation

        Exports exportsAnnot = clazz.getAnnotation(Exports.class);
        if (exportsAnnot != null) {
            // get all the classes which matches the given export list
            ArrayList<Class<? extends BaseLibrary>> exportList = new ArrayList<>(List.of(exportsAnnot.value()));

            if (exportList.contains(clazz)) {
                throw new LoadException.ExportException(clazz, "Library attempts to export itself");
            }

            ArrayList<Class<? extends BaseLibrary>> fromExportList = globals
                    .getAllClassLibraries()
                    .stream()
                    .filter(LibraryLike::hasClass)
                    .map(LibraryLike::getLibClass)
                    .filter(exportList::contains)
                    .collect(Collectors.toCollection(ArrayList::new));

            for (Class<? extends BaseLibrary> importFrom : fromExportList) {
                libraryInstance.addImportPromise(new ImportPromise(importFrom));
            }
        }

        return libraryInstance;
    }

    public static String vfsToJson(@NotNull Vfs VFS) throws JaivaException {

        SimpleJSON json = new SimpleJSON();
        ArrayList<Object> tokens = new ArrayList<>();
        json.set("version", Main.version);
        json.set("tokens", tokens);

        HashMap<String, NewJson> jsonMap = new HashMap<>();
        for (Map.Entry<String, MapValue> entry : VFS.entrySet()) {
            String name = entry.getKey();
            MapValue vf = entry.getValue();
            Symbol symbol = vf.getValue();
            if (jsonMap.containsKey(symbol.name)) {
                SimpleJSON symJson = jsonMap.get(symbol.name).copy();
                if (symbol.symbolType == SymbolType.FUNCTION) {
                    // TFunction token adds F~ syntax to name
                    symJson.set("name", "F~" + name);
                } else {
                    symJson.set("name",  name);
                }
                tokens.add(symJson);
            } else {
                NewJson json2 = symbol.token.toJson();
                tokens.add(json2);
                jsonMap.put(symbol.name, json2);
            }
        }

        return json.toString();

    }

    public String toToolingJSON(IConfig<Object> config, Globals globals) throws JaivaException {
        Vfs VFS = getVfs(config, globals);
        return vfsToJson(VFS);
    }

    public enum Metadata {
        PATH, DESCRIPTION
    }

    public static Optional<String> getFromAnnotation(@NotNull Class<? extends BaseLibrary> clazz, Metadata metadata) {
        JaivaLibrary library = clazz.getAnnotation(JaivaLibrary.class);
        if (library != null) return Optional.of(metadata == Metadata.DESCRIPTION ? library.description() : library.path());

        PublicLibrary library2 = clazz.getAnnotation(PublicLibrary.class);
        if (library2 != null) return Optional.of(metadata == Metadata.DESCRIPTION ? library2.description() : library2.path());

        GlobalsLib library3 = clazz.getAnnotation(GlobalsLib.class);
        if (library3 != null) return Optional.of(metadata == Metadata.DESCRIPTION ? library3.description() : library3.path());

        return Optional.empty();
    }

    public static Optional<String> getFromSymbol(@NotNull Vfs vfs, Metadata metadata) throws LoadException.LibraryMetadataException {
        String str = metadata == Metadata.DESCRIPTION ? "description" : "path";
        Optional<Map.Entry<String, MapValue>> symEntry = vfs.entrySet()
                .stream()
                .filter(s -> s.getKey().equals(str))
                .findFirst();
        if (symEntry.isPresent()) {
            TokenDefault<?> token = symEntry.get().getValue().getValue().token;
            if (!(token.tooltip instanceof JDoc doc)) {
                throw new LoadException.LibraryMetadataException(
                        "\"" + str + "\" symbol does not contain usable documentation",
                        token.lineNumber
                );
            }
            if (doc.getDescription().isEmpty()) {
                throw new LoadException.LibraryMetadataException(
                        "\"" + str + "\" symbol's description is empty.",
                        token.lineNumber
                );
            }
            return Optional.of(doc.getDescription().trim());
        }
        return Optional.empty();
    }

    public ArrayList<LibrarySymbol> getSymbols() {
        return new ArrayList<>(symbols);
    }

    public ArrayList<ImportPromise> getImportPromises() {
        return importPromises;
    }
    /**
     * Uses reflection to get the public static String field named "path" from the given class.
     *
     * @param clazz the class to inspect
     * @return the value of the "path" field
     * @throws IllegalStateException if the field is not found, not public static String, or null/blank
     */
    public static String externalLibraryRequirements(@NotNull Class<?> clazz) throws LoadException.LibraryAnnotationException {
        boolean publicLibAnnotFound = false;
        boolean isPublic = false;
        for (Annotation annotation : clazz.getAnnotations()) {
            if (publicLibAnnotFound) break;
            Library j = annotation.annotationType().getAnnotation(Library.class);
            if (j != null) {
                publicLibAnnotFound = true;
                isPublic = j.libType() == LibraryType.LIB;
            }
        }

        if (!publicLibAnnotFound)
            throw new LoadException.LibraryAnnotationException(clazz);
        if (!isPublic)
            throw new LoadException.LibraryAnnotationException("External classes cannot have library annotations which aren't @PublicLibrary!");

        PublicLibrary library = clazz.getAnnotation(PublicLibrary.class);
        if (library != null) {
            return library.path();
        }

        throw new LoadException.LibraryAnnotationException(clazz.getName()
                + " must have the @PublicLibrary annotation!");
    }
}

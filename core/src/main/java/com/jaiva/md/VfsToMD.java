package com.jaiva.md;

import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.MapValue;
import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibrarySymbol;
import com.jaiva.interpreter.libs.Globals;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.Symbol;
import com.yetnt.utils.builders.MarkDownLiteral;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class VfsToMD {
    MDInputProps props;
    /**
     * the actual lines of the entire Markdown. once its in here its essentially solidified.
     */
    MDMap lines;
    MDOutputProps fileProps;

    String path;

    private final ArrayList<SymbolLink> symbolToc = new ArrayList<>();

    public record SymbolLink(String displayName, String link) {
        @NotNull
        @Override
        public String toString() {
            return new MarkDownLiteral(displayName).linkTo("#" + link).toString();
        }
    }

    public record MDFile(String content, String fileName) {}

    public VfsToMD(MDInputProps props, MDOutputProps fileProps, IConfig<Object> i, Globals g) throws JaivaException {
        this.props = props;
        lines = new MDMap();
        this.fileProps = fileProps;

        // create methods will be called here the TOC create last if the property is active
        createHeader();
        createExportsDeclaration(i, g);
        createSymbolList();
        createToc();
    }

    public MDFile complete() {
        // heading
        StringBuilder sb = new StringBuilder();

        ArrayList<String> ls = lines.get(MDMap.Key.HEADER);
        sb.append(String.join("\n", ls));

        if (fileProps.listExports()) {
            ArrayList<String> ex = lines.get(MDMap.Key.EXPORTS);
            sb.append(String.join("\n", ex));
        }

        if (fileProps.includeTableOfContents()) {
            ArrayList<String> toc = lines.get(MDMap.Key.TOC);
            if (toc != null)
                sb.append(String.join("\n", toc));
        }

        if (fileProps.splitSymbols()) {
            ArrayList<String> funcs = lines.get(MDMap.Key.FUNCTIONS);
            ArrayList<String> vars = lines.get(MDMap.Key.VARIABLES);
            if (fileProps.preferFunctions()) {
                sb.append(String.join("\n", funcs));
                sb.append(String.join("\n", vars));
            } else {
                sb.append(String.join("\n", vars));
                sb.append(String.join("\n", funcs));
            }
        } else {
            ArrayList<String> syms = lines.get(MDMap.Key.SYMBOLS);
            sb.append(String.join("\n", syms));
        }

        String filePath = path != null ? path
                .replaceAll(Matcher.quoteReplacement("\""), "")
                .toLowerCase()
                .replace("\\", "-")
                .replace("/", "-")
                .trim() : "unspecified";


        return new MDFile(sb.toString(), filePath);
    }

    /**
     * Creates the header
     * @throws JaivaException If the lib is a file and {@link BaseLibrary#getFromSymbol(Vfs, BaseLibrary.Metadata)}
     * fails.
     */
    private void createHeader() throws JaivaException {
        ArrayList<String> header = new ArrayList<>();
        lines.put(MDMap.Key.HEADER, header);

        String path, description;

        // attempt to get the path and description
        Optional<String> pathOpt, descriptionOpt;
        if (props.type() == MDInputProps.Type.CLASS) {
           pathOpt = BaseLibrary.getFromAnnotation(props.libClass().getClass(), BaseLibrary.Metadata.PATH);
           descriptionOpt = BaseLibrary.getFromAnnotation(props.libClass().getClass(), BaseLibrary.Metadata.DESCRIPTION);
        } else {
            pathOpt = BaseLibrary.getFromSymbol(props.vfs(), BaseLibrary.Metadata.PATH);
            descriptionOpt = BaseLibrary.getFromSymbol(props.vfs(), BaseLibrary.Metadata.DESCRIPTION);
            // further remove them from the vfs.
            props.vfs().remove("description");
            props.vfs().remove("path");
        }

        path = pathOpt.map(string -> string + (props.isLib() ? " (Library)" : " (File)")).orElse("(Unknown Library, no path specified.)");
        description = descriptionOpt.orElse("");

        this.path = pathOpt.orElse(null);

        header.add(new MarkDownLiteral(path).title(MarkDownLiteral.Title.TITLE).toString());
        header.add("");
        header.add(new MarkDownLiteral(description).italics().toString());
        header.add("");
    }


    /**
     * Does the export header
     */
    private void createExportsDeclaration(IConfig<Object> i, Globals g) throws JaivaException {
        ArrayList<String> exports = new ArrayList<>();
        lines.put(MDMap.Key.EXPORTS, exports);
        if (props.type() != MDInputProps.Type.CLASS) return; // only lasses can have export annotation
        if (!fileProps.declareExportedLibraries()) return; // early exit.

        exports.add(new MarkDownLiteral("Exports").title(MarkDownLiteral.Title.SUBTITLE).toString());
        exports.add(
                "This library exports the following libraries: "
        );

        props.libClass().getImportPromises().forEach(
                ip ->
                        exports.add(
                                "- " +
                                        BaseLibrary.getFromAnnotation(ip.classToImportFrom(), BaseLibrary.Metadata.PATH)
                                                .orElse("(Unknown Library, no path specified.)")
                        )
        );

        if (!fileProps.listExports()) return;

        if (props.libClass().getImportPromises().isEmpty()) return;

        exports.add(""); // new ln

        Vfs unique = props.vfs();
        Vfs all = props.libClass().getVfs(i, g);

        Vfs imported = new Vfs(all.entrySet().stream().filter(
                set -> !unique.containsValue(set.getValue())
        ).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a,
                LinkedHashMap::new
        )));

        String s = imported.keySet().stream().reduce(
                "",
                (s1, s2) -> s1 + ", " + new MarkDownLiteral(s2).inlineCode()
        );

        exports.add("Exported Symbols Include: " + s.substring(2));

        exports.add("");
    }

    private void createSymbolList() {
        ArrayList<String> symbols = new ArrayList<>();
        ArrayList<String> functions = new ArrayList<>();
        ArrayList<String> variables = new ArrayList<>();

        lines.put(MDMap.Key.FUNCTIONS, functions);
        lines.put(MDMap.Key.VARIABLES, variables);
        lines.put(MDMap.Key.SYMBOLS, symbols);

        functions.add(new MarkDownLiteral("Functions").title(MarkDownLiteral.Title.SUBTITLE).toString());
        functions.add("");
        variables.add(new MarkDownLiteral("Variables").title(MarkDownLiteral.Title.SUBTITLE).toString());
        variables.add("");
        symbols.add(new MarkDownLiteral("Symbols").title(MarkDownLiteral.Title.SUBTITLE).toString());
        symbols.add("");

        // if its a file, look through the VFS directly
        // otherwise, prefer the symbol list from BaseLibrary
        if (props.type() == MDInputProps.Type.FILE) {
            for (MapValue symMapValue : props.vfs().values()) {
                Symbol s = symMapValue.getValue();
                consumeSymbol(s, functions::addAll, variables::addAll, symbols::addAll);
            }
        } else {
            for (LibrarySymbol lsym : props.libClass().getSymbols()) {
                consumeSymbol(lsym, functions::addAll, variables::addAll, symbols::addAll);
            }
        }
    }

    private void consumeSymbol(
            Object s,
            Consumer<Collection<? extends String>> functionConsumer,
            Consumer<Collection<? extends String>> variableConsumer,
            Consumer<Collection<? extends String>> symbolConsumer
    ) {
        String displayName;
        ArrayList<String> aliasList;
        MDSymbol.GetMarkdown gm = MDSymbol.get(s);
        if (s instanceof BaseFunction || (s instanceof LibrarySymbol ls && ls.symbol() instanceof BaseFunction)) {
            functionConsumer.accept(gm.getLines());
        } else {
            variableConsumer.accept(gm.getLines());
        }
        symbolConsumer.accept(gm.getLines());
        aliasList = gm.getAliasList();
        displayName = gm.getDisplayName();

        aliasList.forEach(alias ->
            symbolToc.add(new SymbolLink(
                    alias,
                    displayName
            ))
        );
    }

    private void createToc() {
        if (symbolToc.isEmpty()) return;

        ArrayList<String> toc = new ArrayList<>();
        lines.put(MDMap.Key.TOC, toc);

        toc.add("");
        toc.add(new MarkDownLiteral("Table of Contents").title(MarkDownLiteral.Title.SUBTITLE).toString());

        toc.addAll(
                MarkDownLiteral.asTable(
                        new ArrayList<>(List.of("Alias", "Link")),
                        symbolToc
                                .stream()
                                .map(s -> new ArrayList<>(List.of(s.displayName, s.toString())))
                                .collect(Collectors.toCollection(ArrayList::new))
                )
        );
        toc.add("");
    }
}

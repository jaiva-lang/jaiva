package com.jaiva.md;

import com.jaiva.interpreter.libs.LibrarySymbol;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.jdoc.tags.DParameter;
import com.jaiva.tokenizer.tokens.specific.TBooleanVar;
import com.jaiva.tokenizer.tokens.specific.TFunction;
import com.jaiva.tokenizer.tokens.specific.TNumberVar;
import com.jaiva.tokenizer.tokens.specific.TStringVar;
import com.yetnt.utils.builders.MarkDownLiteral;
import com.yetnt.utils.tuple.SamePair;

import java.util.ArrayList;
import java.util.List;

public class MDSymbol {

    public static class GetMarkdown extends SamePair<ArrayList<String>> {
        private String name;
        public GetMarkdown(String symbolName, ArrayList<String> lines, ArrayList<String> aliasList) {
            super(lines, aliasList);
            this.name = symbolName;
        }

        public String getDisplayName() {
            return name;
        }

        public ArrayList<String> getLines() {
            return getFirst();
        }

        public ArrayList<String> getAliasList() {
            return getSecond();
        }
    }

    public static GetMarkdown get(Object sym) {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> aliasList = new ArrayList<>();
        String title;
        Symbol symbol;

        if (sym instanceof Symbol s) {
            title = s.name;
            lines.add(new MarkDownLiteral(title).title(MarkDownLiteral.Title.SECTION).toString());
            aliasList.add(s.name);
            symbol = s;
        } else {
            LibrarySymbol ls = (LibrarySymbol) sym;
            title = ls.aliases().getFirst();
            lines.add(new MarkDownLiteral(title).title(MarkDownLiteral.Title.SECTION).toString());
            aliasList.addAll(ls.aliases());
            lines.add("");
            lines.add(
                    "This symbol can be reached by the following aliases: "
                    + ls.aliases().stream().reduce(
                            "",
                            (a, b) -> a + ", " + new MarkDownLiteral(b).inlineCode().italics()
                    ).substring(2)
            );
            symbol = ls.symbol();
        }

        ArrayList<ArrayList<String>> commonProps = commonSymbolProperties(symbol);
        // [description, version, deprecation, note, example]

        lines.add("");

        // If exists, write deprecation
        if (!commonProps.get(2).isEmpty()) {
            lines.addAll(commonProps.get(2));
            lines.add("");
        }

        if (!commonProps.getFirst().isEmpty())
            lines.addAll(commonProps.getFirst()); // description

        lines.add("");

        // the function/variable definition string
        lines.addAll(writeSymbol(symbol));

        lines.add("");

        // write note
        if (!commonProps.get(3).isEmpty()) {
            lines.addAll(commonProps.get(3));
            lines.add("");
        }

        // write version
        if (!commonProps.get(1).isEmpty()) {
            lines.addAll(commonProps.get(1));
            lines.add("");
        }

        // write example
        if (!commonProps.get(4).isEmpty()) {
            lines.addAll(commonProps.get(4));
            lines.add("");
        }

        lines.add("---");

        lines.add("");
        lines.add("");

        return new GetMarkdown(title, lines, aliasList);
    }

    private static ArrayList<ArrayList<String>> commonSymbolProperties(Symbol s) {
        ArrayList<ArrayList<String>> lines = new ArrayList<>();
        ArrayList<String> description = new ArrayList<>();
        ArrayList<String> deprecation = new ArrayList<>();
        ArrayList<String> version = new ArrayList<>();
        ArrayList<String> note = new ArrayList<>();
        ArrayList<String> example = new ArrayList<>();

        lines.add(description);
        lines.add(version);
        lines.add(deprecation);
        lines.add(note);
        lines.add(example);

        if (s.token.tooltip instanceof String d) {
            if (d.isEmpty()) {
                description.add(new MarkDownLiteral(d).bold().italics().toString());
                deprecation.add("");
            }
        } else if (s.token.tooltip instanceof JDoc doc) {
            if (!doc.getDescription().isEmpty()) {
                description.add(new MarkDownLiteral(doc.getDescription().trim()).bold().italics().toString());
                description.add("");
            }
            if (!doc.getVersion().isEmpty()) {
                version.add("Since Version: " + new MarkDownLiteral(doc.getVersion()).italics());
                version.add("");
            }
            if (!doc.getNote().isEmpty()) {
                note.addAll(MarkDownLiteral.GithubBlockQuote(
                        MarkDownLiteral.GithubBlockQuote.NOTE,
                        new MarkDownLiteral(doc.getNote()).italics().toString()
                ));
                note.add("");
            }
            if (!doc.getDeprecatedString().isEmpty()) {
                deprecation.addAll(
                        MarkDownLiteral.GithubBlockQuote(
                                MarkDownLiteral.GithubBlockQuote.WARNING,
                                ("This symbol has been marked as deprecated! " + doc.getDeprecatedString())
                        )
                );
                deprecation.add("");
            }
            if (!doc.getExample().isEmpty()) {
                example.add(new MarkDownLiteral("Example: ").title(MarkDownLiteral.Title.SUBSECTION).toString());
                example.add("");
                example.addAll(MarkDownLiteral.asCodeBlock(
                        doc.getExample(),
                        "jaiva"
                ));
                example.add("");
            }
        }

        return lines;
    }

    private static ArrayList<String> writeSymbol(Symbol s) {
        if (s instanceof BaseFunction f) {
            return writeFunction(f);
        } else {
            return writeVariable((BaseVariable)s);
        }
    }

    private static ArrayList<String> writeFunction(BaseFunction f) {

        TFunction token = (TFunction)f.token;

        // write function definition.

        ArrayList<String> lines = new ArrayList<>(List.of(new MarkDownLiteral("Definition").title(MarkDownLiteral.Title.SUBSECTION).toString()));
        lines.add("");
        lines.addAll(MarkDownLiteral.asCodeBlock(
                new ArrayList<>(List.of(token.toDefinitionString())),
                "jaiva"
        ));

        if (!(token.tooltip instanceof JDoc doc)) return lines;

        // write parameters
        if (doc.getParameters().size() > 1) lines.add("");
        for (DParameter docParameter : doc.getParameters()) {
            lines.add(
                    "- " +
                            new MarkDownLiteral(docParameter.varName)
                                    .italics().bold() + " " +
                            (docParameter.optional ? new MarkDownLiteral("?").inlineCode().bold() : "") +
                            " " +
                            new MarkDownLiteral("<-").inlineCode().bold() +
                            " " +
                            new MarkDownLiteral(docParameter.type)
                                    .bold().italics()
            );
            lines.add(
                    "\t - " +
                            new MarkDownLiteral(docParameter.desc).italics()
            );
        }

        // write returns
        if (!doc.getReturns().isEmpty()) {
            lines.add("");
            lines.add(
                    new MarkDownLiteral("Returns:").bold().italics() +" "+ new MarkDownLiteral(doc.getReturns()).bold().toString()
            );
        }

        // write depends
        if (!doc.getDependencies().isEmpty()) {
            lines.add("");
            lines.addAll(
                    MarkDownLiteral.GithubBlockQuote(
                            MarkDownLiteral.GithubBlockQuote.WARNING,
                            "This symbol depends on the following symbols: " +
                                    new MarkDownLiteral(String.join(", ", doc.getDependencies())).bold()
                            + ". If not imported this call may fail!!"
                    )
            );
        }
        return lines;
    }


    private static ArrayList<String> writeVariable(BaseVariable v) {
        ArrayList<String> lines = new ArrayList<>(
                List.of(new MarkDownLiteral("Definition").title(MarkDownLiteral.Title.SUBSECTION).toString())
        );
        String def = "maak "+v.name+" <-";

        if (v.variableType == BaseVariable.VariableType.ARRAY) {
            def = def + "| (array)";
        } else {
            // in this case it could be multiple shiz
            def = switch (v.token) {
                case TStringVar ignored
                    -> def + " (string)";
                case TBooleanVar ignored
                    -> def + " (boolean)";
                case TNumberVar ignored
                    -> def +" (number)";
                default -> def + " (???)";
            };
        }

        lines.addAll(MarkDownLiteral.asCodeBlock(
                new ArrayList<>(List.of(def)),
                "jaiva"
        ));

        return lines;
    }

}

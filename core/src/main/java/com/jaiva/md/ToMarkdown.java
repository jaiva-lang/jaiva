package com.jaiva.md;

import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.libs.Globals;
import com.jaiva.interpreter.runtime.IConfig;
import com.yetnt.utils.builders.AnsiColour;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ToMarkdown {

    public ToMarkdown(MDInputProps props, Path path, IConfig<Object> i, Globals g, String properties) throws JaivaException, IOException {
        MDOutputProps outputProps;
        if (properties == null) {
            outputProps = new MDOutputProps(
                    true,
                    true, true,
                    true, true
            );
        } else {
            String[] parts = properties.split(":");
            if (parts.length != 2) parts = properties.split("=");
            if (parts.length != 2)
                throw new JaivaException.InvalidArgsException(
                        "Markdown output properties flag has to be in the format of \"-p:+-++-+\". Where a + represents true for that positional" +
                                " property."
                );
            String pr = parts[1];
            if (pr.length() != 5)
                throw new JaivaException.InvalidArgsException(
                        "Markdown output properties flag has to be in the format of \"-p:+-++-+\". Where a + represents true for that positional" +
                                " property."
                );
            boolean toc = pr.charAt(0) == '+';
            boolean split = pr.charAt(1) == '+';
            boolean preferFuncs = pr.charAt(2) == '+';
            boolean declareExportedSyms = pr.charAt(3) == '+';
            boolean listExports = pr.charAt(4) == '+';
            outputProps = new MDOutputProps(toc, split, preferFuncs, declareExportedSyms, listExports);
        }

        VfsToMD vfsToMD = new VfsToMD(props, outputProps, i, g);

        VfsToMD.MDFile out = vfsToMD.complete();

        File outFile = new File(path.toString(), out.fileName() + ".md");

        Files.writeString(outFile.toPath(), out.content());

        System.out.println(AnsiColour.print(
                "Wrote markdown docs to " + outFile.getAbsolutePath() + " succesfully",
                AnsiColour.FORE.GREEN
        ));
    }

}

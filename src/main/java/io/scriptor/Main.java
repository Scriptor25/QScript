package io.scriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.scriptor.backend.Builder;
import io.scriptor.frontend.StackFrame;
import io.scriptor.frontend.Parser;
import io.scriptor.frontend.ParserConfig;

public class Main {

    public static void compileFiles(
            final String[] inputFilenames,
            final String[] includeDirs,
            final String outputFilename)
            throws IOException {
        Builder.createContext();

        Builder main = null;
        for (int i = 0; i < inputFilenames.length; ++i) {
            final var frame = new StackFrame();
            final var filename = inputFilenames[i];
            final var builder = new Builder(frame, filename);

            final var file = new File(filename);
            final var config = new ParserConfig(frame, builder::genIR, file, includeDirs, new FileInputStream(file));
            Parser.parse(config);

            if (i == 0) {
                main = builder;
            } else {
                main.link(builder);
                builder.dispose();
            }
        }

        main.emitToFile(outputFilename);
        main.dispose();

        Builder.disposeContext();
    }

    public static void main(final String[] args) throws IOException {
        String outputFilename = null;
        List<String> includeDirs = new ArrayList<>();
        List<String> inputFilenames = new ArrayList<>();

        for (int i = 0; i < args.length; ++i) {
            if ("-o".equals(args[i])) {
                outputFilename = args[++i];
                continue;
            }
            if ("-i".equals(args[i])) {
                includeDirs.add(args[++i]);
                continue;
            }

            inputFilenames.add(args[i]);
        }

        if (inputFilenames.isEmpty())
            throw new IllegalStateException("no input filename specified");

        if (outputFilename == null)
            outputFilename = "a.out";

        compileFiles(inputFilenames.toArray(String[]::new), includeDirs.toArray(String[]::new), outputFilename);
    }
}

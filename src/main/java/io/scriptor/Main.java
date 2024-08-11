package io.scriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.scriptor.backend.Builder;
import io.scriptor.frontend.Context;
import io.scriptor.frontend.Parser;
import io.scriptor.frontend.ParserConfig;

public class Main {

    public static void compileFiles(
            final String[] inputFilenames,
            final String[] includeDirs,
            final String outputFilename)
            throws IOException {

        final var builders = new Builder[inputFilenames.length];

        final var ctx = new Context();
        for (int i = 0; i < inputFilenames.length; ++i) {
            ctx.clear();

            final var inputFilename = inputFilenames[i];
            final var builder = new Builder(ctx, inputFilename);
            final var file = new File(inputFilename);
            final var config = new ParserConfig(ctx, builder::genIR, file, includeDirs, new FileInputStream(file));
            Parser.parse(config);

            builders[i] = builder;
        }

        Builder.mergeAndEmitToFile(builders, outputFilename);
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

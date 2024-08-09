package io.scriptor.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.scriptor.frontend.Parser;
import io.scriptor.frontend.ParserConfig;
import io.scriptor.backend.Builder;
import io.scriptor.frontend.Context;
import io.scriptor.frontend.expression.Expression;

public class FileSession {

    public static void create(final String[] infilenames, final String outfilename) throws IOException {
        final var builders = new Builder[infilenames.length];
        for (int i = 0; i < infilenames.length; ++i) {
            final var infilename = infilenames[i];
            final var session = new FileSession(infilename);
            builders[i] = session.builder;
        }
        Builder.mergeAndEmitToFile(builders, outfilename);
    }

    private final Context ctx = new Context();
    private final Builder builder;
    private final File file;

    private FileSession(final String infilename) throws IOException {
        this.builder = new Builder(ctx, infilename);
        this.file = new File(infilename);

        Parser.parse(new ParserConfig(
                ctx,
                this::callback,
                file,
                new FileInputStream(file)));
    }

    private void callback(final Expression expr) {
        builder.genIR(expr);
    }
}

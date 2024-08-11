package io.scriptor.frontend;

import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;

import io.scriptor.frontend.statement.Statement;

public record ParserConfig(
        Context ctx,
        Consumer<Statement> callback,
        File file,
        String[] includeDirs,
        InputStream stream) {

    public ParserConfig(final ParserConfig config, final File file, final InputStream stream) {
        this(config.ctx, config.callback, file, config.includeDirs, stream);
    }
}

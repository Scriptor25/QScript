package io.scriptor.frontend;

import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;

import io.scriptor.frontend.stmt.Stmt;

public record ParserConfig(
        StackFrame frame,
        Consumer<Stmt> callback,
        File file,
        String[] includeDirs,
        InputStream stream) {

    public ParserConfig(final ParserConfig config, final File file, final InputStream stream) {
        this(config.frame, config.callback, file, config.includeDirs, stream);
    }
}

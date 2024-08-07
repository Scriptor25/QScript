package io.scriptor.frontend;

import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;

import io.scriptor.backend.IRContext;
import io.scriptor.expression.Expression;

public record ParserConfig(
        IRContext context,

        File file,
        Consumer<Expression> callback,

        State global,
        InputStream stream) {

    public ParserConfig(final ParserConfig config, final File file, final InputStream stream) {
        this(config.context, file, config.callback, config.global, stream);
    }
}

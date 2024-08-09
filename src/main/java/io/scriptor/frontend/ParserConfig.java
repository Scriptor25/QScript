package io.scriptor.frontend;

import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;

import io.scriptor.frontend.expression.Expression;

public record ParserConfig(
        Context ctx,
        Consumer<Expression> callback,
        File file,
        InputStream stream) {

    public ParserConfig(final ParserConfig config, final File file, final InputStream stream) {
        this(config.ctx, config.callback, file, stream);
    }
}

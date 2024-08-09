package io.scriptor.frontend;

import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;

import io.scriptor.frontend.expression.Expression;

public record ParserConfig(
        File file,
        Consumer<Expression> callback,

        State global,
        InputStream stream) {

    public ParserConfig(final ParserConfig config, final File file, final InputStream stream) {
        this(file, config.callback, config.global, stream);
    }
}

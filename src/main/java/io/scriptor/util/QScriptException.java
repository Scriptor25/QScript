package io.scriptor.util;

import io.scriptor.frontend.SourceLocation;

public class QScriptException extends RuntimeException {

    public QScriptException() {
        super();
    }

    public QScriptException(final String format, final Object... args) {
        super(format.formatted(args));
    }

    public QScriptException(final SourceLocation location, final String format, final Object... args) {
        super("at %s: %s".formatted(location, format.formatted(args)));
    }
}

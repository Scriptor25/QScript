package io.scriptor;

import io.scriptor.parser.SourceLocation;

public class QScriptException extends RuntimeException {

    public QScriptException() {
        super();
    }

    public QScriptException(final Throwable cause) {
        super(cause);
    }

    public QScriptException(final String format, final Object... args) {
        super(format.formatted(args));
    }

    public QScriptException(final SourceLocation location, final String format, final Object... args) {
        super("at %s: %s".formatted(location, format.formatted(args)));
    }

    public QScriptException(final Throwable cause, final String format, final Object... args) {
        super(format.formatted(args), cause);
    }

    public static void rtassert(final boolean check) {
        if (!check)
            throw new QScriptException("assertion failed");
    }
}

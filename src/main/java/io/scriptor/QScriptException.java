package io.scriptor;

import java.util.function.Supplier;

import io.scriptor.parser.SourceLocation;

public class QScriptException extends RuntimeException {

    public static void rtassert(final boolean check, final Supplier<QScriptException> supplier) {
        if (!check)
            throw supplier.get();
    }

    public QScriptException(final SourceLocation location, final String format, final Object... args) {
        super("at %s: %s".formatted(location, format.formatted(args)));
    }
}

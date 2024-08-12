package io.scriptor.util;

import io.scriptor.frontend.SourceLocation;

public class QScriptError {

    public static void print(final SourceLocation sl, final String fmt, final Object... args) {
        if (sl != null)
            System.err.printf("at %s: %s\n", sl, fmt.formatted(args));
    }

    private QScriptError() {
    }
}

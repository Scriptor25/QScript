package io.scriptor.frontend;

import java.io.File;

public record SourceLocation(File f, int r, int c) {

    public static final SourceLocation UNKNOWN = new SourceLocation(null, 0, 0);

    @Override
    public String toString() {
        if (f == null)
            return "(%d,%d)".formatted(r, c);
        return "%s(%d,%d)".formatted(f, r, c);
    }
}

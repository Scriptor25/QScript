package io.scriptor.frontend;

import java.io.File;

public record SourceLocation(File f, int r, int c) {

    @Override
    public String toString() {
        if (f == null)
            return "(%d,%d)".formatted(r, c);
        return "%s(%d,%d)".formatted(f, r, c);
    }
}

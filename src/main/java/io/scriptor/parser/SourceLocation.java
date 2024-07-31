package io.scriptor.parser;

import java.io.File;

public record SourceLocation(File file, int row, int column) {

    @Override
    public String toString() {
        if (file == null)
            return "(%d,%d)".formatted(row, column);
        return "%s(%d,%d)".formatted(file, row, column);
    }
}

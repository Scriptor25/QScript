package io.scriptor.frontend.statement;

import io.scriptor.frontend.SourceLocation;

public abstract class Statement {

    private final SourceLocation sl;

    protected Statement(final SourceLocation sl) {
        this.sl = sl;
    }

    public SourceLocation getSl() {
        return sl;
    }

    public abstract String toString();
}

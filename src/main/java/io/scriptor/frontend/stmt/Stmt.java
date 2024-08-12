package io.scriptor.frontend.stmt;

import io.scriptor.frontend.SourceLocation;

public abstract class Stmt {

    private final SourceLocation sl;

    protected Stmt(final SourceLocation sl) {
        this.sl = sl;
    }

    public SourceLocation getSl() {
        return sl;
    }

    public abstract String toString();
}

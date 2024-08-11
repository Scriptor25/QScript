package io.scriptor.frontend.statement;

import io.scriptor.frontend.SourceLocation;

public abstract class Statement {

    private final SourceLocation location;

    protected Statement(final SourceLocation location) {
        this.location = location;
    }

    public SourceLocation getLocation() {
        return location;
    }

    public abstract String toString();
}

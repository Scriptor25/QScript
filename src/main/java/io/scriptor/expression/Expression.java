package io.scriptor.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public abstract class Expression {

    private final SourceLocation location;
    private final Type type;

    protected Expression(final SourceLocation location, final Type type) {
        this.location = location;
        this.type = type;
    }

    public SourceLocation getLocation() {
        return location;
    }

    public Type getType() {
        return type;
    }

    public abstract String toString();

    public abstract Value gen(final IRBuilder builder, final IRModule module);
}

package io.scriptor.expression;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
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

    public abstract Value eval(final Environment env);

    public abstract String toString();
}

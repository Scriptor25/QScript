package io.scriptor.expression;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public abstract class Expression {

    private final SourceLocation location;
    private final Type promise;

    protected Expression(final SourceLocation location, final Type promise) {
        this.location = location;
        this.promise = promise;
    }

    public SourceLocation getLocation() {
        return location;
    }

    public Type getPromise() {
        return promise;
    }

    public abstract Value eval(final Environment env);

    public abstract String toString();
}

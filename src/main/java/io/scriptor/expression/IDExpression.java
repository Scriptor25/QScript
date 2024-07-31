package io.scriptor.expression;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class IDExpression extends Expression {

    private final String id;

    public IDExpression(final SourceLocation location, final Type promise, final String id) {
        super(location, promise);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public Value eval(final Environment env) {
        final var symbol = env.getSymbol(id);
        return symbol.getValue();
    }

    @Override
    public String toString() {
        return id;
    }
}

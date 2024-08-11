package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class SymbolExpression extends Expression {

    public static SymbolExpression create(final SourceLocation location, final Type type, final String name) {
        return new SymbolExpression(location, type, name);
    }

    private final String id;

    private SymbolExpression(final SourceLocation location, final Type type, final String name) {
        super(location, type);
        this.id = name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}

package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class SymbolExpression extends Expression {

    public static SymbolExpression create(final SourceLocation sl, final Type ty, final String name) {
        return new SymbolExpression(sl, ty, name);
    }

    private final String name;

    private SymbolExpression(final SourceLocation sl, final Type ty, final String name) {
        super(sl, ty);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

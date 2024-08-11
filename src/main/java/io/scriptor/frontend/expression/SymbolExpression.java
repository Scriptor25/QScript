package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.Context;
import io.scriptor.type.Type;

public class SymbolExpression extends Expression {

    public static SymbolExpression create(final SourceLocation location, final Context ctx, final String id) {
        final var symbol = ctx.getSymbol(location, id);
        return new SymbolExpression(location, symbol.type(), symbol.id());
    }

    private final String id;

    private SymbolExpression(final SourceLocation location, final Type type, final String id) {
        super(location, type);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}

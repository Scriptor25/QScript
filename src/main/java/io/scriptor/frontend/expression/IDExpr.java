package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.State;
import io.scriptor.type.Type;

public class IDExpr extends Expression {

    public static IDExpr create(final SourceLocation location, final State state, final String id) {
        final var symbol = state.getSymbol(location, id);
        return new IDExpr(location, symbol.type(), symbol.id());
    }

    private final String id;

    private IDExpr(final SourceLocation location, final Type type, final String id) {
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

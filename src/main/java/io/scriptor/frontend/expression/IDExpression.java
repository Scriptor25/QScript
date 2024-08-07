package io.scriptor.frontend.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.State;
import io.scriptor.type.Type;

public class IDExpression extends Expression {

    public static IDExpression create(final SourceLocation location, final State state, final String id) {
        final var symbol = state.getSymbol(location, id);
        return new IDExpression(location, symbol.type(), symbol.id());
    }

    private final String id;

    private IDExpression(final SourceLocation location, final Type type, final String id) {
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

    @Override
    public Value genIR(final IRBuilder builder, final IRModule module) {
        var value = builder.getValue(id);
        if (value != null)
            return value;

        value = module.getGlobal(id);
        if (value != null)
            return value;

        throw new UnsupportedOperationException();
    }
}

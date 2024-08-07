package io.scriptor.frontend.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class DefineExpression extends Expression {

    public static DefineExpression create(
            final SourceLocation location,
            final Type symbolType,
            final String id) {
        return new DefineExpression(location, symbolType, id, null);
    }

    public static DefineExpression create(
            final SourceLocation location,
            final Type symbolType,
            final String id,
            final Expression init) {
        return new DefineExpression(location, symbolType, id, init);
    }

    private final Type symbolType;
    private final String id;
    private final Expression init;

    private DefineExpression(
            final SourceLocation location,
            final Type symbolType,
            final String id,
            final Expression init) {
        super(location, null);
        this.symbolType = symbolType;
        this.id = id;
        this.init = init;
    }

    public Type getSymbolType() {
        return symbolType;
    }

    public String getId() {
        return id;
    }

    public Expression getInit() {
        return init;
    }

    @Override
    public String toString() {
        if (init == null)
            return "def %s %s".formatted(symbolType, id);
        return "def %s %s = %s".formatted(symbolType, id, init);
    }

    @Override
    public Value gen(final IRBuilder builder, final IRModule module) {
        throw new UnsupportedOperationException();
    }
}

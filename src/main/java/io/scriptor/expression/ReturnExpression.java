package io.scriptor.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class ReturnExpression extends Expression {

    public static ReturnExpression create(
            final SourceLocation location,
            final Type result,
            final Expression expression) {
        return new ReturnExpression(location, result, expression);
    }

    public static ReturnExpression create(
            final SourceLocation location,
            final Type result) {
        return new ReturnExpression(location, result, null);
    }

    private final Type result;
    private final Expression expression;

    private ReturnExpression(
            final SourceLocation location,
            final Type result,
            final Expression expression) {
        super(location, null);
        this.result = result;
        this.expression = expression;
    }

    public Type getResult() {
        return result;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "return %s".formatted(expression);
    }

    @Override
    public Value gen(final IRBuilder builder, final IRModule module) {
        throw new UnsupportedOperationException();
    }
}

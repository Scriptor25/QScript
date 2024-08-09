package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class ReturnExpr extends Expression {

    public static ReturnExpr create(
            final SourceLocation location,
            final Type result,
            final Expression expression) {
        return new ReturnExpr(location, result, expression);
    }

    public static ReturnExpr create(
            final SourceLocation location,
            final Type result) {
        return new ReturnExpr(location, result, null);
    }

    private final Type result;
    private final Expression expression;

    private ReturnExpr(
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

    public Expression getExpr() {
        return expression;
    }

    @Override
    public String toString() {
        return "return %s".formatted(expression);
    }
}

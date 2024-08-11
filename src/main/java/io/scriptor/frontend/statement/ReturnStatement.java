package io.scriptor.frontend.statement;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expression.Expression;
import io.scriptor.type.Type;

public class ReturnStatement extends Statement {

    public static ReturnStatement create(
            final SourceLocation location,
            final Type result,
            final Expression expression) {
        return new ReturnStatement(location, result, expression);
    }

    public static ReturnStatement create(
            final SourceLocation location,
            final Type result) {
        return new ReturnStatement(location, result, null);
    }

    private final Type result;
    private final Expression expression;

    private ReturnStatement(
            final SourceLocation location,
            final Type result,
            final Expression expression) {
        super(location);
        this.result = result;
        this.expression = expression;
    }

    public Type getResult() {
        return result;
    }

    public Expression getExpr() {
        return expression;
    }

    public boolean hasExpr() {
        return expression != null;
    }

    @Override
    public String toString() {
        return "return %s".formatted(expression);
    }
}

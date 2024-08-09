package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class BinaryExpr extends Expression {

    public static BinaryExpr create(
            final SourceLocation location,
            final String operator,
            final Expression lhs,
            final Expression rhs) {
        final var type = Type.getHigherOrder(location, lhs.getType(), rhs.getType());
        return new BinaryExpr(location, type, operator, lhs, rhs);
    }

    private final String operator;
    private final Expression lhs;
    private final Expression rhs;

    private BinaryExpr(
            final SourceLocation location,
            final Type type,
            final String operator,
            final Expression lhs,
            final Expression rhs) {
        super(location, type);
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getLHS() {
        return lhs;
    }

    public Expression getRHS() {
        return rhs;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(lhs, operator, rhs);
    }
}

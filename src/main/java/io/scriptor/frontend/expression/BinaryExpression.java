package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class BinaryExpression extends Expression {

    public static BinaryExpression create(
            final SourceLocation sl,
            final String op,
            final Expression lhs,
            final Expression rhs) {
        final var ty = Type.getHigherOrder(sl, lhs.getTy(), rhs.getTy());
        return new BinaryExpression(sl, ty, op, lhs, rhs);
    }

    private final String op;
    private final Expression lhs;
    private final Expression rhs;

    private BinaryExpression(
            final SourceLocation sl,
            final Type ty,
            final String op,
            final Expression lhs,
            final Expression rhs) {
        super(sl, ty);
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public String getOp() {
        return op;
    }

    public Expression getLHS() {
        return lhs;
    }

    public Expression getRHS() {
        return rhs;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(lhs, op, rhs);
    }
}

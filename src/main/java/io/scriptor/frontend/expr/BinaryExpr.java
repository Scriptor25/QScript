package io.scriptor.frontend.expr;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class BinaryExpr extends Expr {

    public static BinaryExpr create(
            final SourceLocation sl,
            final String op,
            final Expr lhs,
            final Expr rhs) {
        final var ty = Type.getHigherOrder(sl, lhs.getTy(), rhs.getTy());
        return new BinaryExpr(sl, ty, op, lhs, rhs);
    }

    private final String op;
    private final Expr lhs;
    private final Expr rhs;

    private BinaryExpr(
            final SourceLocation sl,
            final Type ty,
            final String op,
            final Expr lhs,
            final Expr rhs) {
        super(sl, ty);
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public String getOp() {
        return op;
    }

    public Expr getLHS() {
        return lhs;
    }

    public Expr getRHS() {
        return rhs;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(lhs, op, rhs);
    }
}

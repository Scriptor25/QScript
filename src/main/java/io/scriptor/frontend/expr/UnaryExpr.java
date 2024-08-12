package io.scriptor.frontend.expr;

import static io.scriptor.util.Util.getUnOpResult;

import io.scriptor.frontend.SourceLocation;

public class UnaryExpr extends Expr {

    public static UnaryExpr createR(
            final SourceLocation sl,
            final String op,
            final Expr val) {
        return new UnaryExpr(sl, true, op, val);
    }

    public static UnaryExpr createL(
            final SourceLocation sl,
            final String op,
            final Expr val) {
        return new UnaryExpr(sl, false, op, val);
    }

    private final boolean right;
    private final String op;
    private final Expr val;

    private UnaryExpr(
            final SourceLocation sl,
            final boolean right,
            final String op,
            final Expr val) {
        super(sl, getUnOpResult(op, val.getTy()));
        this.right = right;
        this.op = op;
        this.val = val;
    }

    public boolean isRight() {
        return right;
    }

    public String getOp() {
        return op;
    }

    public Expr getVal() {
        return val;
    }

    @Override
    public String toString() {
        if (right)
            return "%s%s".formatted(val, op);
        return "%s%s".formatted(op, val);
    }
}

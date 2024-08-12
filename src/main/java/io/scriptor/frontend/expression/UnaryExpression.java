package io.scriptor.frontend.expression;

import static io.scriptor.util.Util.getUnOpResult;

import io.scriptor.frontend.SourceLocation;

public class UnaryExpression extends Expression {

    public static UnaryExpression createR(
            final SourceLocation sl,
            final String op,
            final Expression val) {
        return new UnaryExpression(sl, true, op, val);
    }

    public static UnaryExpression createL(
            final SourceLocation sl,
            final String op,
            final Expression val) {
        return new UnaryExpression(sl, false, op, val);
    }

    private final boolean right;
    private final String op;
    private final Expression val;

    private UnaryExpression(
            final SourceLocation sl,
            final boolean right,
            final String op,
            final Expression val) {
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

    public Expression getVal() {
        return val;
    }

    @Override
    public String toString() {
        if (right)
            return "%s%s".formatted(val, op);
        return "%s%s".formatted(op, val);
    }
}

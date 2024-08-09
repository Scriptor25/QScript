package io.scriptor.frontend.expression;

import static io.scriptor.util.Util.getUnOpResult;

import io.scriptor.frontend.SourceLocation;

public class UnaryExpr extends Expression {

    public static UnaryExpr createR(
            final SourceLocation location,
            final String operator,
            final Expression operand) {
        return new UnaryExpr(location, true, operator, operand);
    }

    public static UnaryExpr createL(
            final SourceLocation location,
            final String operator,
            final Expression operand) {
        return new UnaryExpr(location, false, operator, operand);
    }

    private final boolean right;
    private final String operator;
    private final Expression operand;

    private UnaryExpr(
            final SourceLocation location,
            final boolean right,
            final String operator,
            final Expression operand) {
        super(location, getUnOpResult(operator, operand.getType()));
        this.right = right;
        this.operator = operator;
        this.operand = operand;
    }

    public boolean isRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        if (right)
            return "%s%s".formatted(operand, operator);
        return "%s%s".formatted(operator, operand);
    }
}

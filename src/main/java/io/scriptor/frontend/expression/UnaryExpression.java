package io.scriptor.frontend.expression;

import static io.scriptor.util.Util.getUnOpResult;

import io.scriptor.frontend.SourceLocation;

public class UnaryExpression extends Expression {

    public static UnaryExpression createR(
            final SourceLocation location,
            final String operator,
            final Expression operand) {
        return new UnaryExpression(location, true, operator, operand);
    }

    public static UnaryExpression createL(
            final SourceLocation location,
            final String operator,
            final Expression operand) {
        return new UnaryExpression(location, false, operator, operand);
    }

    private final boolean right;
    private final String operator;
    private final Expression operand;

    private UnaryExpression(
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
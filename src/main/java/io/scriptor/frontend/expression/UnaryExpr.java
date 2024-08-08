package io.scriptor.frontend.expression;

import static io.scriptor.util.Util.getUnOpResult;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.ref.RValueRef;
import io.scriptor.backend.ref.ValueRef;
import io.scriptor.backend.value.ConstValue;
import io.scriptor.backend.value.Value;
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

    @Override
    public ValueRef genIR(final IRBuilder builder, final IRModule module) {
        final var operand = this.operand.genIR(builder, module);
        final var value = operand.get();

        final boolean assign;
        final Value result;

        if ("++".equals(operator)) {
            assign = true;
            result = builder.createAdd(value, ConstValue.getConstInt(getType(), 1));
        } else if ("--".equals(operator)) {
            assign = true;
            result = builder.createSub(value, ConstValue.getConstInt(getType(), 1));
        } else {
            assign = false;
            result = switch (operator) {
                case "-" -> builder.createNeg(value);
                case "~" -> builder.createNot(value);
                case "!" -> builder.createLNot(value);
                default -> null;
            };
        }

        if (result != null) {
            if (assign) {
            }

            if (right)
                return RValueRef.create(value);
            return RValueRef.create(result);
        }

        throw new UnsupportedOperationException();
    }
}

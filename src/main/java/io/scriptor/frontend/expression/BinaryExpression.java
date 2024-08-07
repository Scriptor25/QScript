package io.scriptor.frontend.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class BinaryExpression extends Expression {

    public static BinaryExpression create(
            final SourceLocation location,
            final String operator,
            final Expression lhs,
            final Expression rhs) {
        final var type = Type.getHigherOrder(location, lhs.getType(), rhs.getType());
        return new BinaryExpression(location, type, operator, lhs, rhs);
    }

    private final String operator;
    private final Expression lhs;
    private final Expression rhs;

    private BinaryExpression(
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

    @Override
    public Value genIR(final IRBuilder builder, final IRModule module) {
        if ("=".equals(operator)) {
            throw new UnsupportedOperationException();
        }

        Value result;

        final var left = lhs.genIR(builder, module);
        final var right = rhs.genIR(builder, module);

        result = switch (operator) {
            case "==" -> builder.createCmpEQ(left, right);
            case "!=" -> builder.createCmpNE(left, right);
            case "<=" -> builder.createCmpLE(left, right);
            case ">=" -> builder.createCmpGE(left, right);
            case "<" -> builder.createCmpLT(left, right);
            case ">" -> builder.createCmpGT(left, right);
            default -> null;
        };

        if (result != null)
            return result;

        final var assign = operator.contains("=");
        final var op = operator.replace("=", "");

        result = switch (op) {
            case "+" -> builder.createAdd(left, right);
            case "-" -> builder.createSub(left, right);
            case "*" -> builder.createMul(left, right);
            case "/" -> builder.createDiv(left, right);
            case "%" -> builder.createRem(left, right);
            case "&" -> builder.createAnd(left, right);
            case "|" -> builder.createOr(left, right);
            case "^" -> builder.createXOr(left, right);
            case "&&" -> builder.createLAnd(left, right);
            case "||" -> builder.createLOr(left, right);
            case "^^" -> builder.createLXOr(left, right);
            case "<<" -> builder.createShL(left, right);
            case ">>" -> builder.createLShR(left, right);
            case ">>>" -> builder.createAShR(left, right);
            default -> null;
        };

        if (result != null) {
            if (assign) {
                throw new UnsupportedOperationException();
            }
            return result;
        }

        throw new UnsupportedOperationException();
    }
}

package io.scriptor.frontend.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.ref.LValueRef;
import io.scriptor.backend.ref.RValueRef;
import io.scriptor.backend.ref.ValueRef;
import io.scriptor.backend.value.Value;
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

    @Override
    public ValueRef genIR(final IRBuilder builder, final IRModule module) {
        if ("=".equals(operator)) {
            final var left = (LValueRef) lhs.genIR(builder, module);
            final var right = rhs.genIR(builder, module);

            left.copy(right);
            return left;
        }

        Value result;

        final var left = lhs.genIR(builder, module);
        final var right = rhs.genIR(builder, module);

        final var lv = left.get();
        final var rv = right.get();

        result = switch (operator) {
            case "==" -> builder.createCmpEQ(lv, rv);
            case "!=" -> builder.createCmpNE(lv, rv);
            case "<=" -> builder.createCmpLE(lv, rv);
            case ">=" -> builder.createCmpGE(lv, rv);
            case "<" -> builder.createCmpLT(lv, rv);
            case ">" -> builder.createCmpGT(lv, rv);
            default -> null;
        };

        if (result != null)
            return RValueRef.create(result);

        final var assign = operator.contains("=");
        final var op = operator.replace("=", "");

        result = switch (op) {
            case "+" -> builder.createAdd(lv, rv);
            case "-" -> builder.createSub(lv, rv);
            case "*" -> builder.createMul(lv, rv);
            case "/" -> builder.createDiv(lv, rv);
            case "%" -> builder.createRem(lv, rv);
            case "&" -> builder.createAnd(lv, rv);
            case "|" -> builder.createOr(lv, rv);
            case "^" -> builder.createXOr(lv, rv);
            case "&&" -> builder.createLAnd(lv, rv);
            case "||" -> builder.createLOr(lv, rv);
            case "^^" -> builder.createLXOr(lv, rv);
            case "<<" -> builder.createShL(lv, rv);
            case ">>" -> builder.createLShR(lv, rv);
            case ">>>" -> builder.createAShR(lv, rv);
            default -> null;
        };

        if (result != null) {
            if (assign) {
                ((LValueRef) left).set(result);
                return left;
            }
            return RValueRef.create(result);
        }

        throw new UnsupportedOperationException();
    }
}

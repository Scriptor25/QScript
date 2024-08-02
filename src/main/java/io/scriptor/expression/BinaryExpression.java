package io.scriptor.expression;

import io.scriptor.QScriptException;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Operation;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class BinaryExpression extends Expression {

    private final String operator;
    private final Expression lhs;
    private final Expression rhs;

    public BinaryExpression(
            final SourceLocation location,
            final String operator,
            final Expression lhs,
            final Expression rhs) {
        super(location, Type.getHigherOrder(lhs.getType(), rhs.getType()));
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Value eval(final Environment env) {
        if (operator.equals("="))
            return Operation.assign(env, lhs, rhs.eval(env));

        final var left = Operation.cast(lhs.eval(env), getType());
        final var right = Operation.cast(rhs.eval(env), getType());

        switch (operator) {
            case "<=" -> {
                return Operation.le(left, right);
            }
            case ">=" -> {
                return Operation.ge(left, right);
            }
            case "==" -> {
                return Operation.eq(left, right);
            }
            case "!=" -> {
                return Operation.ne(left, right);
            }
        }

        final var assign = operator.contains("=");
        final var op = this.operator.replace("=", "");

        final var result = switch (op) {
            case "+" -> Operation.add(left, right);
            case "-" -> Operation.sub(left, right);
            case "*" -> Operation.mul(left, right);
            case "/" -> Operation.div(left, right);
            case "%" -> Operation.rem(left, right);
            case "<" -> Operation.lt(left, right);
            case ">" -> Operation.gt(left, right);
            default -> throw new QScriptException(
                    getLocation(),
                    "no such operator '%s %s %s'",
                    left.getType(),
                    op,
                    right.getType());
        };

        if (assign)
            return Operation.assign(env, lhs, result);
        return result;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(lhs, operator, rhs);
    }
}

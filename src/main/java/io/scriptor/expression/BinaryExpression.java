package io.scriptor.expression;

import io.scriptor.QScriptException;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;

public class BinaryExpression extends Expression {

    private final String operator;
    private final Expression lhs;
    private final Expression rhs;

    public BinaryExpression(
            final SourceLocation location,
            final String operator,
            final Expression lhs,
            final Expression rhs) {
        super(location, null);
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
    public Value eval(final Environment env) {
        if (operator.equals("="))
            return Operation.assign(env, lhs, rhs.eval(env));

        final var left = lhs.eval(env);
        final var right = rhs.eval(env);

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

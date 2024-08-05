package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Operation;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class BinaryExpression extends Expression {

    public static BinaryExpression create(
            final SourceLocation location,
            final String operator,
            final Expression lhs,
            final Expression rhs) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(operator != null, () -> new QScriptException(location, "operator is null"));
        rtassert(lhs != null, () -> new QScriptException(location, "lhs is null"));
        rtassert(rhs != null, () -> new QScriptException(location, "rhs is null"));
        final var type = Type.getHigherOrder(location, lhs.getType(), rhs.getType());
        rtassert(type != null, () -> new QScriptException(location, "type is null"));
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

    @Override
    public Value eval(final Environment env) {
        if (operator.equals("="))
            return Operation.assign(getLocation(), env, lhs, rhs.eval(env));

        final var left = Operation.cast(getLocation(), lhs.eval(env), getType());
        final var right = Operation.cast(getLocation(), rhs.eval(env), getType());

        switch (operator) {
            case "<=" -> {
                return Operation.le(getLocation(), left, right);
            }
            case ">=" -> {
                return Operation.ge(getLocation(), left, right);
            }
            case "==" -> {
                return Operation.eq(getLocation(), left, right);
            }
            case "!=" -> {
                return Operation.ne(getLocation(), left, right);
            }
        }

        final var assign = operator.contains("=");
        final var op = this.operator.replace("=", "");

        final var result = switch (op) {
            case "+" -> Operation.add(getLocation(), left, right);
            case "-" -> Operation.sub(getLocation(), left, right);
            case "*" -> Operation.mul(getLocation(), left, right);
            case "/" -> Operation.div(getLocation(), left, right);
            case "%" -> Operation.rem(getLocation(), left, right);
            case "<" -> Operation.lt(getLocation(), left, right);
            case ">" -> Operation.gt(getLocation(), left, right);
            default -> throw new QScriptException(
                    getLocation(),
                    "no such operator '%s %s %s'",
                    left.getType(),
                    op,
                    right.getType());
        };

        if (assign)
            return Operation.assign(getLocation(), env, lhs, result);
        return result;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(lhs, operator, rhs);
    }
}

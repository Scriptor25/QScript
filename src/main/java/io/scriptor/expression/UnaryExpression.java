package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Operation;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;

public class UnaryExpression extends Expression {

    public static UnaryExpression createR(
            final SourceLocation location,
            final String operator,
            final Expression operand) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(operator != null, () -> new QScriptException(location, "operator is null"));
        rtassert(operand != null, () -> new QScriptException(location, "operand is null"));
        return new UnaryExpression(location, true, operator, operand);
    }

    public static UnaryExpression createL(
            final SourceLocation location,
            final String operator,
            final Expression operand) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(operator != null, () -> new QScriptException(location, "operator is null"));
        rtassert(operand != null, () -> new QScriptException(location, "operand is null"));
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
        super(location, operand.getType());
        this.right = right;
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public Value eval(final Environment env) {
        final var value = operand.eval(env);
        switch (operator) {
            case "++" -> {
                final var result = Operation.inc(getLocation(), value);
                Operation.assign(getLocation(), env, operand, result);
                if (right)
                    return value;
                return result;
            }

            case "--" -> {
                final var result = Operation.dec(getLocation(), value);
                Operation.assign(getLocation(), env, operand, result);
                if (right)
                    return value;
                return result;
            }

            case "-" -> {
                return Operation.neg(getLocation(), value);
            }

            default -> throw new QScriptException(getLocation(), "no such operator '%s%s'", value.getType(), operator);
        }
    }

    @Override
    public String toString() {
        if (right)
            return "%s%s".formatted(operand, operator);
        return "%s%s".formatted(operator, operand);
    }
}

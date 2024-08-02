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
        rtassert(location != null);
        rtassert(operator != null);
        rtassert(operand != null);
        return new UnaryExpression(location, true, operator, operand);
    }

    public static UnaryExpression createL(
            final SourceLocation location,
            final String operator,
            final Expression operand) {
        rtassert(location != null);
        rtassert(operator != null);
        rtassert(operand != null);
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
                final var result = Operation.inc(value);
                Operation.assign(env, operand, result);
                if (right)
                    return value;
                return result;
            }

            case "--" -> {
                final var result = Operation.dec(value);
                Operation.assign(env, operand, result);
                if (right)
                    return value;
                return result;
            }

            case "-" -> {
                return Operation.neg(value);
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

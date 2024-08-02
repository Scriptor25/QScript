package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Operation;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;

public class UnaryExpression extends Expression {

    public static UnaryExpression create(
            final SourceLocation location,
            final String operator,
            final Expression operand) {
        rtassert(location != null);
        rtassert(operator != null);
        rtassert(operand != null);
        return new UnaryExpression(location, operator, operand);
    }

    private final String operator;
    private final Expression operand;

    private UnaryExpression(
            final SourceLocation location,
            final String operator,
            final Expression operand) {
        super(location, operand.getType());
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
                return value;
            }

            case "--" -> {
                final var result = Operation.dec(value);
                Operation.assign(env, operand, result);
                return value;
            }
        }

        throw new QScriptException();
    }

    @Override
    public String toString() {
        return "%s%s".formatted(operand, operator);
    }
}

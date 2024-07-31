package io.scriptor.expression;

import io.scriptor.QScriptException;
import io.scriptor.environment.ConstValue;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;

public class UnaryExpression extends Expression {

    public static Value inc(final Value value) {
        final var type = value.getType();

        if (type.isInt() || type.isFloat())
            return Operation.add(value, new ConstValue<>(type, 1));

        throw new QScriptException();
    }

    public static Value dec(final Value value) {
        final var type = value.getType();

        if (type.isInt() || type.isFloat())
            return Operation.sub(value, new ConstValue<>(type, 1));

        throw new QScriptException();
    }

    private final String operator;
    private final Expression operand;

    public UnaryExpression(
            final SourceLocation location,
            final String operator,
            final Expression operand) {
        super(location, null);
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public Value eval(final Environment env) {
        final var value = operand.eval(env);
        switch (operator) {
            case "++" -> {
                final var result = inc(value);
                Operation.assign(env, operand, result);
                return value;
            }

            case "--" -> {
                final var result = dec(value);
                Operation.assign(env, operand, result);
                return value;
            }
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "%s%s".formatted(operand, operator);
    }
}

package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Operation;
import io.scriptor.environment.UndefinedValue;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class ReturnExpression extends Expression {

    public static ReturnExpression create(
            final SourceLocation location,
            final Type result,
            final Expression expression) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(result != null, () -> new QScriptException(location, "result is null"));
        rtassert(expression != null, () -> new QScriptException(location, "expression is null"));
        return new ReturnExpression(location, result, expression);
    }

    public static ReturnExpression create(
            final SourceLocation location,
            final Type result) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(result != null, () -> new QScriptException(location, "result is null"));
        return new ReturnExpression(location, result, null);
    }

    private final Type result;
    private final Expression expression;

    private ReturnExpression(
            final SourceLocation location,
            final Type result,
            final Expression expression) {
        super(location, result);
        this.result = result;
        this.expression = expression;
    }

    @Override
    public Value eval(final Environment env) {
        if (expression == null)
            return new UndefinedValue(result).setReturn(true);
        return Operation.cast(getLocation(), expression.eval(env), result).setReturn(true);
    }

    @Override
    public String toString() {
        return "return %s".formatted(expression);
    }
}

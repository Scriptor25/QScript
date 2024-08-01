package io.scriptor.expression;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Operation;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class ReturnExpression extends Expression {

    private final Type result;
    private final Expression expression;

    public ReturnExpression(final SourceLocation location, final Type result, final Expression expression) {
        super(location, null);
        this.result = result;
        this.expression = expression;
    }

    @Override
    public Value eval(final Environment env) {
        var value = expression.eval(env);
        value = Operation.cast(value, result);
        return value.setReturn(true);
    }

    @Override
    public String toString() {
        return "return %s".formatted(expression);
    }
}

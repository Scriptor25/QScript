package io.scriptor.expression;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;

public class ReturnExpression extends Expression {

    private final Expression expression;

    public ReturnExpression(final SourceLocation location, final Expression expression) {
        super(location, null);
        this.expression = expression;
    }

    @Override
    public Value eval(final Environment env) {
        return expression.eval(env).setReturn(true);
    }

    @Override
    public String toString() {
        return "return %s".formatted(expression);
    }
}

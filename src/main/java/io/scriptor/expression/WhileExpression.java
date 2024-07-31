package io.scriptor.expression;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;

public class WhileExpression extends Expression {

    private final Expression condition;
    private final Expression loop;

    public WhileExpression(final SourceLocation location, final Expression condition, final Expression loop) {
        super(location, null);
        this.condition = condition;
        this.loop = loop;
    }

    @Override
    public Value eval(final Environment env) {
        while (condition.eval(env).getBoolean()) {
            final var value = loop.eval(new Environment(env));
            if (value != null && value.isReturn())
                return value;
        }
        return null;
    }

    @Override
    public String toString() {
        return "while %s %s".formatted(condition, loop);
    }
}

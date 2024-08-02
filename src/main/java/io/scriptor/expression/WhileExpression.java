package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;

public class WhileExpression extends Expression {

    public static WhileExpression create(
            final SourceLocation location,
            final Expression condition,
            final Expression loop) {
        rtassert(location != null);
        rtassert(condition != null);
        rtassert(loop != null);
        return new WhileExpression(location, condition, loop);
    }

    private final Expression condition;
    private final Expression loop;

    private WhileExpression(
            final SourceLocation location,
            final Expression condition,
            final Expression loop) {
        super(location, null);
        this.condition = condition;
        this.loop = loop;
    }

    @Override
    public Value eval(final Environment env) {
        while (condition.eval(env).getBoolean()) {
            final var value = loop.eval(env);
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

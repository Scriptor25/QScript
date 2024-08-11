package io.scriptor.frontend.statement;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expression.Expression;

public class WhileStatement extends Statement {

    public static WhileStatement create(
            final SourceLocation location,
            final Expression condition,
            final Statement loop) {
        return new WhileStatement(location, condition, loop);
    }

    private final Expression condition;
    private final Statement loop;

    private WhileStatement(
            final SourceLocation location,
            final Expression condition,
            final Statement loop) {
        super(location);
        this.condition = condition;
        this.loop = loop;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getLoop() {
        return loop;
    }

    @Override
    public String toString() {
        return "while %s %s".formatted(condition, loop);
    }
}

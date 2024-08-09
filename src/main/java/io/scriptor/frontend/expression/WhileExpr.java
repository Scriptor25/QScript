package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;

public class WhileExpr extends Expression {

    public static WhileExpr create(
            final SourceLocation location,
            final Expression condition,
            final Expression loop) {
        return new WhileExpr(location, condition, loop);
    }

    private final Expression condition;
    private final Expression loop;

    private WhileExpr(
            final SourceLocation location,
            final Expression condition,
            final Expression loop) {
        super(location, null);
        this.condition = condition;
        this.loop = loop;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getLoop() {
        return loop;
    }

    @Override
    public String toString() {
        return "while %s %s".formatted(condition, loop);
    }
}

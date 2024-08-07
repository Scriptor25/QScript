package io.scriptor.frontend.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;

public class WhileExpression extends Expression {

    public static WhileExpression create(
            final SourceLocation location,
            final Expression condition,
            final Expression loop) {
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

    @Override
    public Value genIR(final IRBuilder builder, final IRModule module) {
        throw new UnsupportedOperationException();
    }
}

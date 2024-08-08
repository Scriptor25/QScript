package io.scriptor.frontend.expression;

import io.scriptor.backend.Block;
import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.ref.ValueRef;
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

    @Override
    public ValueRef genIR(final IRBuilder builder, final IRModule module) {
        final var function = builder.getInsertFunction();
        final var header = new Block(function, "header");
        final var loop = new Block(function, "loop");
        final var end = new Block(function, "end");

        builder.createBr(header);

        builder.setInsertPoint(header);
        final var condition = this.condition.genIR(builder, module).get();
        builder.createCondBr(condition, loop, end);

        builder.setInsertPoint(loop);
        this.loop.genIR(builder, module);
        builder.createBr(header);

        builder.setInsertPoint(end);
        return null;
    }
}

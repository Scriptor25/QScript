package io.scriptor.frontend.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;

public class IfExpression extends Expression {

    public static IfExpression create(
            final SourceLocation location,
            final Expression condition,
            final Expression thendo) {
        return new IfExpression(location, condition, thendo, null);
    }

    public static IfExpression create(
            final SourceLocation location,
            final Expression condition,
            final Expression thendo,
            final Expression elsedo) {
        return new IfExpression(location, condition, thendo, elsedo);
    }

    private final Expression condition;
    private final Expression thendo;
    private final Expression elsedo;

    private IfExpression(
            final SourceLocation location,
            final Expression condition,
            final Expression thendo,
            final Expression elsedo) {
        super(location, null);
        this.condition = condition;
        this.thendo = thendo;
        this.elsedo = elsedo;
    }

    @Override
    public String toString() {
        if (elsedo != null)
            return "if %s %s else %s".formatted(condition, thendo, elsedo);
        return "if %s %s".formatted(condition, thendo);
    }

    @Override
    public Value genIR(final IRBuilder builder, final IRModule module) {
        throw new UnsupportedOperationException();
    }

}

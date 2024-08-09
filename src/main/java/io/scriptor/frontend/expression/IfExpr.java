package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;

public class IfExpr extends Expression {

    public static IfExpr create(
            final SourceLocation location,
            final Expression condition,
            final Expression thendo) {
        return new IfExpr(location, condition, thendo, null);
    }

    public static IfExpr create(
            final SourceLocation location,
            final Expression condition,
            final Expression thendo,
            final Expression elsedo) {
        return new IfExpr(location, condition, thendo, elsedo);
    }

    private final Expression condition;
    private final Expression thendo;
    private final Expression elsedo;

    private IfExpr(
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
}

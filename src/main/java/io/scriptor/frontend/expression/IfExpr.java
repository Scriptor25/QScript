package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;

public class IfExpr extends Expression {

    public static IfExpr create(
            final SourceLocation location,
            final Expression condition,
            final Expression then) {
        return new IfExpr(location, condition, then, null);
    }

    public static IfExpr create(
            final SourceLocation location,
            final Expression condition,
            final Expression then,
            final Expression else_) {
        return new IfExpr(location, condition, then, else_);
    }

    private final Expression condition;
    private final Expression then;
    private final Expression else_;

    private IfExpr(
            final SourceLocation location,
            final Expression condition,
            final Expression then,
            final Expression else_) {
        super(location, null);
        this.condition = condition;
        this.then = then;
        this.else_ = else_;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getThen() {
        return then;
    }

    public Expression getElse() {
        return else_;
    }

    public boolean hasElse() {
        return else_ != null;
    }

    @Override
    public String toString() {
        if (else_ != null)
            return "if %s %s else %s".formatted(condition, then, else_);
        return "if %s %s".formatted(condition, then);
    }
}

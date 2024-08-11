package io.scriptor.frontend.statement;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expression.Expression;

public class IfStatement extends Statement {

    public static IfStatement create(
            final SourceLocation location,
            final Expression condition,
            final Statement then) {
        return new IfStatement(location, condition, then, null);
    }

    public static IfStatement create(
            final SourceLocation location,
            final Expression condition,
            final Statement then,
            final Statement else_) {
        return new IfStatement(location, condition, then, else_);
    }

    private final Expression condition;
    private final Statement then;
    private final Statement else_;

    private IfStatement(
            final SourceLocation location,
            final Expression condition,
            final Statement then,
            final Statement else_) {
        super(location);
        this.condition = condition;
        this.then = then;
        this.else_ = else_;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getThen() {
        return then;
    }

    public Statement getElse() {
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

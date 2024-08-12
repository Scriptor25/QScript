package io.scriptor.frontend.statement;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expression.Expression;

public class WhileStatement extends Statement {

    public static WhileStatement create(
            final SourceLocation sl,
            final Expression c,
            final Statement l) {
        return new WhileStatement(sl, c, l);
    }

    private final Expression c;
    private final Statement l;

    private WhileStatement(
            final SourceLocation sl,
            final Expression c,
            final Statement l) {
        super(sl);
        this.c = c;
        this.l = l;
    }

    public Expression getC() {
        return c;
    }

    public Statement getL() {
        return l;
    }

    @Override
    public String toString() {
        return "while %s %s".formatted(c, l);
    }
}

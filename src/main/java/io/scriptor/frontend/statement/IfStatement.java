package io.scriptor.frontend.statement;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expression.Expression;

public class IfStatement extends Statement {

    public static IfStatement create(
            final SourceLocation sl,
            final Expression c,
            final Statement t) {
        return new IfStatement(sl, c, t, null);
    }

    public static IfStatement create(
            final SourceLocation sl,
            final Expression c,
            final Statement t,
            final Statement e) {
        return new IfStatement(sl, c, t, e);
    }

    private final Expression c;
    private final Statement t;
    private final Statement e;

    private IfStatement(
            final SourceLocation sl,
            final Expression c,
            final Statement t,
            final Statement e) {
        super(sl);
        this.c = c;
        this.t = t;
        this.e = e;
    }

    public Expression getC() {
        return c;
    }

    public Statement getT() {
        return t;
    }

    public Statement getE() {
        return e;
    }

    public boolean hasE() {
        return e != null;
    }

    @Override
    public String toString() {
        if (e != null)
            return "if %s %s else %s".formatted(c, t, e);
        return "if %s %s".formatted(c, t);
    }
}

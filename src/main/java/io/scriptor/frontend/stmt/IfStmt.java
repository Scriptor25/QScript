package io.scriptor.frontend.stmt;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expr.Expr;

public class IfStmt extends Stmt {

    public static IfStmt create(
            final SourceLocation sl,
            final Expr c,
            final Stmt t) {
        return new IfStmt(sl, c, t, null);
    }

    public static IfStmt create(
            final SourceLocation sl,
            final Expr c,
            final Stmt t,
            final Stmt e) {
        return new IfStmt(sl, c, t, e);
    }

    private final Expr c;
    private final Stmt t;
    private final Stmt e;

    private IfStmt(
            final SourceLocation sl,
            final Expr c,
            final Stmt t,
            final Stmt e) {
        super(sl);
        this.c = c;
        this.t = t;
        this.e = e;
    }

    public Expr getC() {
        return c;
    }

    public Stmt getT() {
        return t;
    }

    public Stmt getE() {
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

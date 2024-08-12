package io.scriptor.frontend.stmt;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expr.Expr;

public class WhileStmt extends Stmt {

    public static WhileStmt create(
            final SourceLocation sl,
            final Expr c,
            final Stmt l) {
        return new WhileStmt(sl, c, l);
    }

    private final Expr c;
    private final Stmt l;

    private WhileStmt(
            final SourceLocation sl,
            final Expr c,
            final Stmt l) {
        super(sl);
        this.c = c;
        this.l = l;
    }

    public Expr getC() {
        return c;
    }

    public Stmt getL() {
        return l;
    }

    @Override
    public String toString() {
        return "while %s %s".formatted(c, l);
    }
}

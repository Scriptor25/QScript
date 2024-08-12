package io.scriptor.frontend.expr;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.stmt.Stmt;
import io.scriptor.type.Type;

public abstract class Expr extends Stmt {

    private final Type ty;

    protected Expr(final SourceLocation sl, final Type ty) {
        super(sl);
        this.ty = ty;
    }

    public Type getTy() {
        return ty;
    }

    public boolean isConst() {
        return false;
    }
}

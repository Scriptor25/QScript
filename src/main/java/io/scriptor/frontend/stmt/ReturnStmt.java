package io.scriptor.frontend.stmt;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expr.Expr;
import io.scriptor.type.Type;

public class ReturnStmt extends Stmt {

    public static ReturnStmt create(
            final SourceLocation sl,
            final Type res,
            final Expr val) {
        return new ReturnStmt(sl, res, val);
    }

    public static ReturnStmt create(
            final SourceLocation sl,
            final Type res) {
        return new ReturnStmt(sl, res, null);
    }

    private final Type res;
    private final Expr val;

    private ReturnStmt(
            final SourceLocation sl,
            final Type res,
            final Expr val) {
        super(sl);
        this.res = res;
        this.val = val;
    }

    public Type getRes() {
        return res;
    }

    public Expr getVal() {
        return val;
    }

    public boolean hasVal() {
        return val != null;
    }

    @Override
    public String toString() {
        return "return %s".formatted(val);
    }
}

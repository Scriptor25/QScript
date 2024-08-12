package io.scriptor.frontend.stmt;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expr.Expr;
import io.scriptor.type.Type;

public class DefVariableStmt extends Stmt {

    public static DefVariableStmt create(
            final SourceLocation sl,
            final Type ty,
            final String name) {
        return new DefVariableStmt(sl, ty, name, null);
    }

    public static DefVariableStmt create(
            final SourceLocation sl,
            final Type ty,
            final String name,
            final Expr init) {
        return new DefVariableStmt(sl, ty, name, init);
    }

    private final Type ty;
    private final String name;
    private final Expr init;

    private DefVariableStmt(
            final SourceLocation sl,
            final Type ty,
            final String name,
            final Expr init) {
        super(sl);
        this.ty = ty;
        this.name = name;
        this.init = init;
    }

    public Type getTy() {
        return ty;
    }

    public String getName() {
        return name;
    }

    public Expr getInit() {
        return init;
    }

    public boolean hasInit() {
        return init != null;
    }

    @Override
    public String toString() {
        if (init == null)
            return "def %s %s".formatted(ty, name);
        return "def %s %s = %s".formatted(ty, name, init);
    }
}

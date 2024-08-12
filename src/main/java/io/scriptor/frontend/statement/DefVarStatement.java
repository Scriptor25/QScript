package io.scriptor.frontend.statement;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expression.Expression;
import io.scriptor.type.Type;

public class DefVarStatement extends Statement {

    public static DefVarStatement create(
            final SourceLocation sl,
            final Type ty,
            final String name) {
        return new DefVarStatement(sl, ty, name, null);
    }

    public static DefVarStatement create(
            final SourceLocation sl,
            final Type ty,
            final String name,
            final Expression init) {
        return new DefVarStatement(sl, ty, name, init);
    }

    private final Type ty;
    private final String name;
    private final Expression init;

    private DefVarStatement(
            final SourceLocation sl,
            final Type ty,
            final String name,
            final Expression init) {
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

    public Expression getInit() {
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

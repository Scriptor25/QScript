package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class DefVarExpr extends Expression {

    public static DefVarExpr create(
            final SourceLocation location,
            final Type type,
            final String name) {
        return new DefVarExpr(location, type, name, null);
    }

    public static DefVarExpr create(
            final SourceLocation location,
            final Type type,
            final String name,
            final Expression init) {
        return new DefVarExpr(location, type, name, init);
    }

    private final Type type;
    private final String name;
    private final Expression init;

    private DefVarExpr(
            final SourceLocation location,
            final Type type,
            final String name,
            final Expression init) {
        super(location, null);
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Expression getInit() {
        return init;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        if (init == null)
            return "def %s %s".formatted(type, name);
        return "def %s %s = %s".formatted(type, name, init);
    }
}

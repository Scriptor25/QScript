package io.scriptor.frontend.statement;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expression.Expression;
import io.scriptor.type.Type;

public class DefVarStatement extends Statement {

    public static DefVarStatement create(
            final SourceLocation location,
            final Type type,
            final String name) {
        return new DefVarStatement(location, type, name, null);
    }

    public static DefVarStatement create(
            final SourceLocation location,
            final Type type,
            final String name,
            final Expression init) {
        return new DefVarStatement(location, type, name, init);
    }

    private final Type type;
    private final String name;
    private final Expression init;

    private DefVarStatement(
            final SourceLocation location,
            final Type type,
            final String name,
            final Expression init) {
        super(location);
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

    public boolean hasInit() {
        return init != null;
    }

    @Override
    public String toString() {
        if (init == null)
            return "def %s %s".formatted(type, name);
        return "def %s %s = %s".formatted(type, name, init);
    }
}

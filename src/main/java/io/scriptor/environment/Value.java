package io.scriptor.environment;

import io.scriptor.QScriptException;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public abstract class Value {

    public static Value getDefault(final SourceLocation location, final Type type) {
        if (type.isInt() || type.isFloat() || type.isPointer())
            return ConstValue.getDefault(location, type);

        if (type.isFunction())
            return FunctionValue.getDefault(type);

        throw new QScriptException(location, "no default value for %s", type);
    }

    private final Type type;
    private boolean isReturn;

    protected Value(final Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isReturn() {
        return isReturn;
    }

    public Value setReturn(final boolean isReturn) {
        this.isReturn = isReturn;
        return this;
    }

    public Number getNumber() {
        return getJava();
    }

    public abstract boolean getBoolean(final SourceLocation location);

    public abstract <T> T getJava();

    public abstract String toString();

    public abstract int hashCode();

    public abstract boolean equals(final Object o);
}

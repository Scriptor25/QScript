package io.scriptor.environment;

import io.scriptor.type.Type;

public abstract class Value {

    public static Value getDefault(final Type type) {
        if (type.isInt() || type.isFloat() || type.isPointer())
            return ConstValue.getDefault(type);

        if (type.isFunction())
            return FunctionValue.getDefault(type);

        return new UndefinedValue(type);
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

    public abstract boolean getBoolean();

    public abstract <T> T getJava();

    public abstract String toString();
}

package io.scriptor.environment;

import io.scriptor.type.Type;

public class Symbol {

    private final Type type;
    private Value value;

    public Symbol(final Type type, final Value value) {
        this.type = type;
        setValue(value);
    }

    public Type getType() {
        return type;
    }

    public Value getValue() {
        return value;
    }

    public Value setValue(Value value) {
        if (type != value.getType())
            value = Operation.cast(value, type);
        return this.value = value;
    }
}

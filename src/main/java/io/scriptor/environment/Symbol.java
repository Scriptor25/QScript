package io.scriptor.environment;

import io.scriptor.type.Type;

public class Symbol {

    private final Type type;
    private final String id;
    private Value value;

    public Symbol(final Type type, final String id, final Value value) {
        this.type = type;
        this.id = id;
        setValue(value);
    }

    public Symbol(final Type type, final String id) {
        this.type = type;
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
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

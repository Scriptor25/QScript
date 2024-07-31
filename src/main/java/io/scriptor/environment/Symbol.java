package io.scriptor.environment;

import io.scriptor.QScriptException;
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

    public Value setValue(final Value value) {
        if (type != value.getType())
            throw new QScriptException("cannot assign value of type %s to type %s", value.getType(), type);
        return this.value = value;
    }
}

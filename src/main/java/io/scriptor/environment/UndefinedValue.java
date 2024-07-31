package io.scriptor.environment;

import io.scriptor.type.Type;

public class UndefinedValue extends Value {

    public UndefinedValue(final Type type) {
        super(type);
    }

    @Override
    public boolean getBoolean() {
        return false;
    }

    @Override
    public <T> T getJava() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "undefined";
    }
}

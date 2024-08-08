package io.scriptor.backend.ref;

import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;

public class RValueRef extends ValueRef {

    public static RValueRef create(final Value value) {
        return new RValueRef(value);
    }

    private final Value value;

    private RValueRef(final Value value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return value.getType();
    }

    @Override
    public Value get() {
        return value;
    }
}

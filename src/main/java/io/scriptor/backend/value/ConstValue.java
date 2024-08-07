package io.scriptor.backend.value;

import io.scriptor.type.Type;

public abstract class ConstValue extends Value {

    protected ConstValue(final Type type) {
        super(type);
    }
}

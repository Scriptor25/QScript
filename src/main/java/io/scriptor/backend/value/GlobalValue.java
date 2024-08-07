package io.scriptor.backend.value;

import io.scriptor.type.Type;

public class GlobalValue extends Value {

    private final ConstValue init;

    public GlobalValue(final Type type, final ConstValue init) {
        this(type, init, null);
    }

    public GlobalValue(final Type type, final ConstValue init, final String name) {
        super(type, name);
        this.init = init;
    }

    public ConstValue getInit() {
        return init;
    }
}

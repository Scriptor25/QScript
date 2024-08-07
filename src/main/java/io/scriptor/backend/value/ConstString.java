package io.scriptor.backend.value;

import io.scriptor.type.Type;

public class ConstString extends ConstValue {

    private final String string;

    public ConstString(final Type type, final String string) {
        super(type);
        this.string = string;
    }

    public String getString() {
        return string;
    }
}

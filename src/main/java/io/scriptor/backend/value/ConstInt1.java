package io.scriptor.backend.value;

import io.scriptor.backend.IRContext;
import io.scriptor.type.Type;

public class ConstInt1 extends ConstValue {

    private final boolean value;

    public ConstInt1(final IRContext context, final boolean value) {
        super(Type.getInt1(context));
        this.value = value;
    }

    public ConstInt1(final Type type, final boolean value) {
        super(type);
        this.value = value;
    }

    @Override
    public boolean getInt1() {
        return value;
    }
}

package io.scriptor.backend.value;

import io.scriptor.backend.IRContext;
import io.scriptor.type.Type;

public class ConstInt16 extends ConstValue {

    private final short value;

    public ConstInt16(final IRContext context, final short value) {
        super(Type.getInt16(context));
        this.value = value;
    }

    public ConstInt16(final Type type, final short value) {
        super(type);
        this.value = value;
    }

    @Override
    public short getInt16() {
        return value;
    }
}

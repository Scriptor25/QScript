package io.scriptor.backend.value;

import io.scriptor.backend.IRContext;
import io.scriptor.type.Type;

public class ConstInt8 extends ConstValue {

    private final byte value;

    public ConstInt8(final IRContext context, final byte value) {
        super(Type.getInt8(context));
        this.value = value;
    }

    public ConstInt8(final Type type, final byte value) {
        super(type);
        this.value = value;
    }

    @Override
    public byte getInt8() {
        return value;
    }
}

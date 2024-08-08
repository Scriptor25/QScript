package io.scriptor.backend.value;

import io.scriptor.backend.IRContext;
import io.scriptor.type.Type;

public class ConstInt32 extends ConstValue {

    public final int value;

    public ConstInt32(final IRContext context, final int value) {
        super(Type.getInt32(context));
        this.value = value;
    }

    public ConstInt32(final Type type, final int value) {
        super(type);
        this.value = value;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.printf("0x%08X", value);
    }

    @Override
    public int getInt32() {
        return value;
    }
}

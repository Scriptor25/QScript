package io.scriptor.backend.value;

import io.scriptor.backend.IRContext;
import io.scriptor.type.Type;

public class ConstFlt32 extends ConstValue {

    private final float value;

    public ConstFlt32(final IRContext context, final float value) {
        super(Type.getFlt32(context));
        this.value = value;
    }

    public ConstFlt32(final Type type, final float value) {
        super(type);
        this.value = value;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print(Float.toHexString(value));
    }

    @Override
    public float getFlt32() {
        return value;
    }
}

package io.scriptor.backend.value;

import io.scriptor.backend.IRContext;
import io.scriptor.type.Type;

public class ConstFlt64 extends ConstValue {

    private final double value;

    public ConstFlt64(final IRContext context, final double value) {
        super(Type.getFlt64(context));
        this.value = value;
    }

    public ConstFlt64(final Type type, final double value) {
        super(type);
        this.value = value;
    }

    @Override
    public double getFlt64() {
        return value;
    }
}

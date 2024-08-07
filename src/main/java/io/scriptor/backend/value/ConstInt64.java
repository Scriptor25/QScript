package io.scriptor.backend.value;

import io.scriptor.backend.IRContext;
import io.scriptor.type.Type;

public class ConstInt64 extends ConstValue {

    private final long value;

    public ConstInt64(final IRContext context, final long value) {
        super(Type.getInt64(context));
        this.value = value;
    }

    public ConstInt64(final Type type, final long value) {
        super(type);
        this.value = value;
    }

    @Override
    public long getInt64() {
        return value;
    }
}

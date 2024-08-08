package io.scriptor.backend.value;

import io.scriptor.type.Type;

public class ConstPtr extends ConstValue {

    private final long value;

    public ConstPtr(final Type type, final long value) {
        super(type);
        this.value = value;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.printf("0x%016X", value);
    }

    @Override
    public long getPtr() {
        return value;
    }
}

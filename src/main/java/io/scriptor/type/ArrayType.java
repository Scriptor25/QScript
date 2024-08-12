package io.scriptor.type;

import io.scriptor.frontend.State;

public class ArrayType extends Type {

    public static ArrayType get(final Type base, final long length) {
        final var state = base.getState();
        final var id = "%s[%d]".formatted(base, length);
        if (Type.exists(state, id))
            return Type.get(null, state, id);

        return new ArrayType(state, id, base, length);
    }

    private final Type base;
    private final long length;

    private ArrayType(final State state, final String id, final Type base, final long length) {
        super(state, id, IS_ARRAY, base.getSize() * length);
        this.base = base;
        this.length = length;
    }

    public Type getBase() {
        return base;
    }

    public long getLength() {
        return length;
    }
}

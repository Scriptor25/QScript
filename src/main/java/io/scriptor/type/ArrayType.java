package io.scriptor.type;

import io.scriptor.frontend.Context;

public class ArrayType extends Type {

    public static ArrayType get(final Type base, final long length) {
        final var ctx = base.getCtx();
        final var id = "%s[%d]".formatted(base, length);
        if (Type.exists(ctx, id))
            return Type.get(ctx, id);

        return new ArrayType(ctx, id, base, length);
    }

    private final Type base;
    private final long length;

    private ArrayType(final Context ctx, final String id, final Type base, final long length) {
        super(ctx, id, IS_ARRAY, base.getSize() * length);
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

package io.scriptor.type;

import io.scriptor.frontend.Context;

public class ArrayType extends Type {

    public static ArrayType get(final Type base, final int count) {
        final var ctx = base.getCtx();
        final var id = "%s[%d]".formatted(base, count);
        if (Type.exists(ctx, id))
            return Type.get(ctx, id);

        return new ArrayType(ctx, id, base, count);
    }

    private final Type base;
    private final int count;

    private ArrayType(final Context ctx, final String id, final Type base, final int count) {
        super(ctx, id, IS_ARRAY, base.getSize() * count);
        this.base = base;
        this.count = count;
    }

    public Type getBase() {
        return base;
    }

    public int getCount() {
        return count;
    }
}

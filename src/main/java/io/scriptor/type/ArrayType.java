package io.scriptor.type;

import io.scriptor.backend.IRContext;

public class ArrayType extends Type {

    public static ArrayType get(final Type base, final int count) {
        final var context = base.getContext();
        final var id = "%s[%d]".formatted(base, count);
        if (context.existsType(id))
            return context.getType(id);

        return new ArrayType(context, id, base, count);
    }

    private final Type base;
    private final int count;

    private ArrayType(final IRContext context, final String id, final Type base, final int count) {
        super(context, id, IS_ARRAY, base.getSize() * count);
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

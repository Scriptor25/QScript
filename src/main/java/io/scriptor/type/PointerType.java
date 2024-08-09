package io.scriptor.type;

import io.scriptor.frontend.Context;

public class PointerType extends Type {

    public static PointerType get(final Type base) {
        final var ctx = base.getCtx();
        final var id = base.getId() + '*';
        if (Type.exists(ctx, id))
            return Type.get(ctx, id);

        return new PointerType(ctx, id, base);
    }

    private final Type base;

    protected PointerType(final Context ctx, final String id, final Type base) {
        super(ctx, id, Type.IS_POINTER, 64);
        this.base = base;
    }

    public Type getBase() {
        return base;
    }
}

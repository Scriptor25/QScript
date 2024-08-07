package io.scriptor.type;

import io.scriptor.backend.IRContext;

public class PointerType extends Type {

    public static PointerType get(final Type base) {
        final var context = base.getContext();
        final var id = base.getId() + '*';
        if (context.existsType(id))
            return context.getType(id);

        return new PointerType(context, id, base);
    }

    private final Type base;

    protected PointerType(final IRContext context, final String id, final Type base) {
        super(context, id, Type.IS_PTR, 64);
        this.base = base;
    }

    public Type getBase() {
        return base;
    }
}

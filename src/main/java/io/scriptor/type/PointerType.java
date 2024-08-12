package io.scriptor.type;

import io.scriptor.frontend.StackFrame;

public class PointerType extends Type {

    public static PointerType get(final Type base) {
        final var state = base.getFrame();
        final var id = base.getId() + '*';
        if (Type.exists(state, id))
            return Type.get(null, state, id);

        return new PointerType(state, id, base);
    }

    private final Type base;

    protected PointerType(final StackFrame frame, final String id, final Type base) {
        super(frame, id, Type.IS_POINTER, 64);
        this.base = base;
    }

    public Type getBase() {
        return base;
    }
}

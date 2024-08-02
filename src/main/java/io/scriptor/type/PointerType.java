package io.scriptor.type;

public class PointerType extends Type {

    public static PointerType get(final Type base) {
        final var id = base.getId() + '*';
        final var type = Type.getUnsafe(id);
        if (type != null)
            return (PointerType) type;
        return Type.create(id, new PointerType(id, base));
    }

    private final Type base;

    protected PointerType(final String id, final Type base) {
        super(id, Type.IS_POINTER, 64);
        this.base = base;
    }

    public Type getBase() {
        return base;
    }
}

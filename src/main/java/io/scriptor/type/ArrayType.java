package io.scriptor.type;

public class ArrayType extends Type {

    public static ArrayType get(final Type base, final int count) {
        final var id = "%s[%d]".formatted(base, count);
        if (Type.exists(id))
            return Type.get(id);

        return new ArrayType(id, base, count);
    }

    private final Type base;
    private final int count;

    private ArrayType(final String id, final Type base, final int count) {
        super(id, IS_ARRAY, base.getSize() * count);
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

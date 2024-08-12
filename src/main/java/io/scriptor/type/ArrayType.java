package io.scriptor.type;

import java.util.Optional;

import io.scriptor.frontend.StackFrame;

public class ArrayType extends Type {

    public static ArrayType get(final Type base, final long length) {
        final var frame = base.getFrame();
        final var name = "%s[%d]".formatted(base, length);

        final Optional<ArrayType> ty = Type.get(null, frame, name);
        if (ty.isPresent())
            return ty.get();

        return new ArrayType(frame, name, base, length);
    }

    private final Type base;
    private final long length;

    private ArrayType(final StackFrame frame, final String id, final Type base, final long length) {
        super(frame, null, id, IS_ARRAY, base.getSize() * length);
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

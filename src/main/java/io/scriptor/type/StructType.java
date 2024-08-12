package io.scriptor.type;

import java.util.Optional;

import io.scriptor.frontend.StackFrame;

public class StructType extends Type {

    private static String getName(final Type[] elements) {
        if (elements.length == 0)
            return "{}";

        final var builder = new StringBuilder()
                .append("{ ");

        for (int i = 0; i < elements.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(elements[i]);
        }

        return builder
                .append(" }")
                .toString();
    }

    public static StructType get(final StackFrame frame, final Type... elements) {
        final var name = getName(elements);
        final Optional<StructType> ty = Type.get(null, frame, name);
        if (ty.isPresent())
            return ty.get();

        int size = 0;
        for (final var element : elements)
            size += element.getSize();

        return new StructType(frame, name, size, elements);
    }

    private final Type[] elements;

    protected StructType(
            final StackFrame frame,
            final String name,
            final int size,
            final Type[] elements) {
        super(frame, null, name, IS_STRUCT, size);
        this.elements = elements;
    }

    public int getElementCount() {
        return elements.length;
    }

    public Type getElement(final int index) {
        return elements[index];
    }
}

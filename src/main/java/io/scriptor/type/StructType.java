package io.scriptor.type;

import io.scriptor.backend.IRContext;

public class StructType extends Type {

    private static String makeId(final Type[] elements) {
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

    public static StructType get(final IRContext context, final Type... elements) {
        final var id = makeId(elements);
        if (context.existsType(id))
            return context.getType(id);

        int size = 0;
        for (final var element : elements)
            size += element.getSize();

        return new StructType(context, id, size, elements);
    }

    public static StructType getOpaque(final IRContext context) {
        final var id = "{}";
        if (context.existsType(id))
            return context.getType(id);

        return new StructType(context, id, 0, null);
    }

    private final Type[] elements;

    protected StructType(final IRContext context, final String id, final int size, final Type[] elements) {
        super(context, id, IS_STRUCT, size);
        this.elements = elements;
    }

    public int getElementCount() {
        return elements.length;
    }

    public Type getElement(final int index) {
        return elements[index];
    }
}

package io.scriptor.type;

import io.scriptor.frontend.StackFrame;

public class StructType extends Type {

    private static String makeId(final Type[] elements) {
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
        final var id = makeId(elements);
        if (Type.exists(frame, id))
            return Type.get(null, frame, id);

        int size = 0;
        for (final var element : elements)
            size += element.getSize();

        return new StructType(frame, id, size, elements);
    }

    private final Type[] elements;

    protected StructType(final StackFrame frame, final String id, final int size, final Type[] elements) {
        super(frame, id, IS_STRUCT, size);
        this.elements = elements;
    }

    public int getElementCount() {
        return elements.length;
    }

    public Type getElement(final int index) {
        return elements[index];
    }
}

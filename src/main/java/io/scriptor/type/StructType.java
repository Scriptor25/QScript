package io.scriptor.type;

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

    public static StructType get(final Type... elements) {
        final var id = makeId(elements);
        if (Type.exists(id))
            return Type.get(id);

        int size = 0;
        for (final var element : elements)
            size += element.getSize();

        return new StructType(id, size, elements);
    }

    public static StructType getOpaque() {
        final var id = "{}";
        if (Type.exists(id))
            return Type.get(id);

        return new StructType(id, 0, null);
    }

    private final Type[] elements;

    protected StructType(final String id, final int size, final Type[] elements) {
        super(id, IS_STRUCT, size);
        this.elements = elements;
    }

    public int getElementCount() {
        return elements.length;
    }

    public Type getElement(final int index) {
        return elements[index];
    }
}

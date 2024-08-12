package io.scriptor.frontend.stmt;

import io.scriptor.frontend.SourceLocation;

public class CompoundStmt extends Stmt {

    public static CompoundStmt create(final SourceLocation sl, final Stmt[] body) {
        return new CompoundStmt(sl, body);
    }

    private static int depth = 0;

    public static String space() {
        final var builder = new StringBuilder();
        for (int i = 0; i < depth; ++i)
            builder.append("    ");
        return builder.toString();
    }

    public static String indent() {
        ++depth;
        return space();
    }

    public static String unindent() {
        --depth;
        return space();
    }

    private final Stmt[] body;

    private CompoundStmt(final SourceLocation sl, final Stmt[] body) {
        super(sl);
        this.body = body;
    }

    public int getCount() {
        return body.length;
    }

    public Stmt get(final int index) {
        return body[index];
    }

    @Override
    public String toString() {
        if (body.length == 0)
            return "{}";

        final var builder = new StringBuilder();
        final var indent = indent();
        for (final var stmt : body)
            builder.append(indent).append(stmt).append('\n');

        return "{%n%s%s}".formatted(builder, unindent());
    }
}

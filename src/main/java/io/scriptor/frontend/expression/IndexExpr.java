package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class IndexExpr extends Expression {

    public static IndexExpr create(final SourceLocation location, final Expression indexee, final Expression index) {
    }

    private final Expression indexee;
    private final Expression index;

    private IndexExpr(
            final SourceLocation location,
            final Type type,
            final Expression indexee,
            final Expression index) {
        super(location, type);
        this.indexee = indexee;
        this.index = index;
    }

    @Override
    public String toString() {
        return "%s[%s]".formatted(indexee, index);
    }
}

package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.ArrayType;
import io.scriptor.type.PointerType;
import io.scriptor.type.Type;

public class IndexExpr extends Expression {

    public static IndexExpr create(
            final SourceLocation location,
            final Expression ptr,
            final Expression index) {
        final Type base;
        if (ptr.getType() instanceof PointerType type)
            base = type.getBase();
        else if (ptr.getType() instanceof ArrayType type)
            base = type.getBase();
        else
            throw new UnsupportedOperationException();

        return new IndexExpr(location, base, ptr, index);
    }

    private final Expression ptr;
    private final Expression index;

    private IndexExpr(
            final SourceLocation location,
            final Type type,
            final Expression ptr,
            final Expression index) {
        super(location, type);
        this.ptr = ptr;
        this.index = index;
    }

    public Expression getPtr() {
        return ptr;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "%s[%s]".formatted(ptr, index);
    }
}

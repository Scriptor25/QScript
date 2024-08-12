package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.ArrayType;
import io.scriptor.type.PointerType;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class IndexExpression extends Expression {

    public static IndexExpression create(
            final SourceLocation sl,
            final Expression ptr,
            final Expression idx) {
        final Type ty;
        if (ptr.getTy() instanceof PointerType type)
            ty = type.getBase();
        else if (ptr.getTy() instanceof ArrayType type)
            ty = type.getBase();
        else
            throw new QScriptException(sl, "not a suitable type");

        return new IndexExpression(sl, ty, ptr, idx);
    }

    private final Expression ptr;
    private final Expression idx;

    private IndexExpression(
            final SourceLocation sl,
            final Type ty,
            final Expression ptr,
            final Expression idx) {
        super(sl, ty);
        this.ptr = ptr;
        this.idx = idx;
    }

    public Expression getPtr() {
        return ptr;
    }

    public Expression getIdx() {
        return idx;
    }

    @Override
    public String toString() {
        return "%s[%s]".formatted(ptr, idx);
    }
}

package io.scriptor.frontend.expr;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.ArrayType;
import io.scriptor.type.PointerType;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class IndexExpr extends Expr {

    public static IndexExpr create(
            final SourceLocation sl,
            final Expr ptr,
            final Expr idx) {
        final Type ty;
        if (ptr.getTy() instanceof PointerType type)
            ty = type.getBase();
        else if (ptr.getTy() instanceof ArrayType type)
            ty = type.getBase();
        else
            throw new QScriptException(sl, "not a suitable type");

        return new IndexExpr(sl, ty, ptr, idx);
    }

    private final Expr ptr;
    private final Expr idx;

    private IndexExpr(
            final SourceLocation sl,
            final Type ty,
            final Expr ptr,
            final Expr idx) {
        super(sl, ty);
        this.ptr = ptr;
        this.idx = idx;
    }

    public Expr getPtr() {
        return ptr;
    }

    public Expr getIdx() {
        return idx;
    }

    @Override
    public String toString() {
        return "%s[%s]".formatted(ptr, idx);
    }
}

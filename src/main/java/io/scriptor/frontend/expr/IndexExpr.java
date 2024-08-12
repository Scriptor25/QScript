package io.scriptor.frontend.expr;

import java.util.Optional;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.ArrayType;
import io.scriptor.type.PointerType;
import io.scriptor.type.Type;

public class IndexExpr extends Expr {

    public static Optional<Expr> create(final SourceLocation sl, final Expr ptr, final Expr idx) {
        final Type ty;
        if (ptr.getTy() instanceof PointerType type) {
            ty = type.getBase();
        } else if (ptr.getTy() instanceof ArrayType type) {
            ty = type.getBase();
        } else {
            return Optional.empty();
        }

        return Optional.of(new IndexExpr(sl, ty, ptr, idx));
    }

    private final Expr ptr;
    private final Expr idx;

    private IndexExpr(final SourceLocation sl, final Type ty, final Expr ptr, final Expr idx) {
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

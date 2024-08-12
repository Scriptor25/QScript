package io.scriptor.frontend.expr;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class IntExpr extends Expr {

    public static IntExpr create(final SourceLocation sl, final Type ty, final long val) {
        return new IntExpr(sl, ty, val);
    }

    private final long val;

    private IntExpr(final SourceLocation sl, final Type ty, final long val) {
        super(sl, ty);
        this.val = val;
    }

    public long getVal() {
        return val;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        return Long.toString(val);
    }
}

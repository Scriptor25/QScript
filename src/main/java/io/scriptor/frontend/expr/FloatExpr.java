package io.scriptor.frontend.expr;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class FloatExpr extends Expr {

    public static FloatExpr create(final SourceLocation sl, final Type ty, final double val) {
        return new FloatExpr(sl, ty, val);
    }

    private final double val;

    private FloatExpr(final SourceLocation sl, final Type ty, final double val) {
        super(sl, ty);
        this.val = val;
    }

    public double getVal() {
        return val;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        return Double.toString(val);
    }
}

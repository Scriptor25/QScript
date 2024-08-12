package io.scriptor.frontend.expr;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;
import io.scriptor.util.Util;

public class CharExpr extends Expr {

    public static CharExpr create(final SourceLocation sl, final Type ty, final char val) {
        return new CharExpr(sl, ty, val);
    }

    private final char val;

    private CharExpr(final SourceLocation sl, final Type ty, final char val) {
        super(sl, ty);
        this.val = val;
    }

    public char getVal() {
        return val;
    }

    @Override
    public String toString() {
        return Util.unescape("'" + val + "'");
    }
}

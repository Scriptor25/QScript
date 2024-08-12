package io.scriptor.frontend.expr;

import static io.scriptor.util.Util.unescape;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class StringExpr extends Expr {

    public static StringExpr create(final SourceLocation sl, final Type ty, final String val) {
        return new StringExpr(sl, ty, val);
    }

    private final String val;

    private StringExpr(final SourceLocation sl, final Type ty, final String val) {
        super(sl, ty);
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        return "\"%s\"".formatted(unescape(val));
    }
}

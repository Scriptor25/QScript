package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;
import io.scriptor.util.Util;

public class CharExpression extends Expression {

    public static CharExpression create(final SourceLocation sl, final Type ty, final char val) {
        return new CharExpression(sl, ty, val);
    }

    private final char val;

    private CharExpression(final SourceLocation sl, final Type ty, final char val) {
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

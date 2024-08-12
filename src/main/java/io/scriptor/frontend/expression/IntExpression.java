package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class IntExpression extends Expression {

    public static IntExpression create(final SourceLocation sl, final Type ty, final long val) {
        return new IntExpression(sl, ty, val);
    }

    private final long val;

    private IntExpression(final SourceLocation sl, final Type ty, final long val) {
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

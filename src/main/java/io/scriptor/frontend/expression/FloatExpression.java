package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class FloatExpression extends Expression {

    public static FloatExpression create(final SourceLocation sl, final Type ty, final double val) {
        return new FloatExpression(sl, ty, val);
    }

    private final double val;

    private FloatExpression(final SourceLocation sl, final Type ty, final double val) {
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

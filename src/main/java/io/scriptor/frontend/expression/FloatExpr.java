package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class FloatExpr extends Expression {

    public static FloatExpr create(final SourceLocation location, final Type type, final double value) {
        return new FloatExpr(location, type, value);
    }

    private final double value;

    private FloatExpr(final SourceLocation location, final Type type, final double value) {
        super(location, type);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}

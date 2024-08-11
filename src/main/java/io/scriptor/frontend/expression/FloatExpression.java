package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class FloatExpression extends Expression {

    public static FloatExpression create(final SourceLocation location, final Type type, final double value) {
        return new FloatExpression(location, type, value);
    }

    private final double value;

    private FloatExpression(final SourceLocation location, final Type type, final double value) {
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
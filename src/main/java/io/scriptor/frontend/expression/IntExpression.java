package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class IntExpression extends Expression {

    public static IntExpression create(final SourceLocation location, final Type type, final long value) {
        return new IntExpression(location, type, value);
    }

    private final long value;

    private IntExpression(final SourceLocation location, final Type type, final long value) {
        super(location, type);
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}

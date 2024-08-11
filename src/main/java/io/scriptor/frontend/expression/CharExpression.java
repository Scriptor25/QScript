package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;
import io.scriptor.util.Util;

public class CharExpression extends Expression {

    public static CharExpression create(final SourceLocation location, final Type type, final char value) {
        return new CharExpression(location, type, value);
    }

    private final char value;

    private CharExpression(final SourceLocation location, final Type type, final char value) {
        super(location, type);
        this.value = value;
    }

    @Override
    public String toString() {
        return Util.unescape("'" + value + "'");
    }
}

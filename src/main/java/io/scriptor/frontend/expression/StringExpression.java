package io.scriptor.frontend.expression;

import static io.scriptor.util.Util.unescape;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class StringExpression extends Expression {

    public static StringExpression create(final SourceLocation sl, final Type ty, final String val) {
        return new StringExpression(sl, ty, val);
    }

    private final String val;

    private StringExpression(final SourceLocation sl, final Type ty, final String val) {
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

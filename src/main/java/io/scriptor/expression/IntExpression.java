package io.scriptor.expression;

import io.scriptor.environment.ConstValue;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class IntExpression extends Expression {

    private final int value;

    public IntExpression(final SourceLocation location, final Type promise, final int value) {
        super(location, promise);
        this.value = value;
    }

    @Override
    public Value eval(final Environment env) {
        return new ConstValue<>(Type.get("i32"), value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}

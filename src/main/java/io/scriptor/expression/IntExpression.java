package io.scriptor.expression;

import io.scriptor.environment.ConstValue;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class IntExpression extends Expression {

    private final long value;

    public IntExpression(final SourceLocation location, final long value) {
        super(location, Type.get("i64"));
        this.value = value;
    }

    @Override
    public Value eval(final Environment env) {
        return new ConstValue<>(Type.get("i64"), value);
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}

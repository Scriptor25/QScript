package io.scriptor.expression;

import io.scriptor.environment.ConstValue;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class FloatExpression extends Expression {

    private final double value;

    public FloatExpression(final SourceLocation location, final double value) {
        super(location, Type.get("f64"));
        this.value = value;
    }

    @Override
    public Value eval(final Environment env) {
        return new ConstValue<>(Type.get("f64"), value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}

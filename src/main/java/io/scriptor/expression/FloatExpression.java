package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
import io.scriptor.environment.ConstValue;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class FloatExpression extends Expression {

    public static FloatExpression create(final SourceLocation location, final double value) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        return new FloatExpression(location, value);
    }

    private final double value;

    private FloatExpression(final SourceLocation location, final double value) {
        super(location, Type.getFlt64());
        this.value = value;
    }

    @Override
    public Value eval(final Environment env) {
        return new ConstValue<>(getType(), value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}

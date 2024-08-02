package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.environment.ConstValue;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class IntExpression extends Expression {

    public static IntExpression create(final SourceLocation location, final long value) {
        rtassert(location != null);
        return new IntExpression(location, value);
    }

    private final long value;

    private IntExpression(final SourceLocation location, final long value) {
        super(location, Type.getInt64());
        this.value = value;
    }

    @Override
    public Value eval(final Environment env) {
        return new ConstValue<>(Type.getInt64(), value);
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}

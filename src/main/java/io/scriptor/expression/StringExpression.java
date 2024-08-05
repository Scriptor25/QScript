package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
import io.scriptor.environment.ConstValue;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.parser.Token;
import io.scriptor.type.Type;

public class StringExpression extends Expression {

    public static StringExpression create(final SourceLocation location, final String value) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(value != null, () -> new QScriptException(location, "value is null"));
        return new StringExpression(location, value);
    }

    private final String value;

    private StringExpression(final SourceLocation location, final String value) {
        super(location, Type.getInt8Ptr());
        this.value = value;
    }

    @Override
    public Value eval(final Environment env) {
        return new ConstValue<>(getType(), value);
    }

    @Override
    public String toString() {
        return "\"%s\"".formatted(Token.unescape(value));
    }
}

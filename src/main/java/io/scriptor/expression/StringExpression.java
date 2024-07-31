package io.scriptor.expression;

import io.scriptor.environment.ConstValue;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.parser.Token;
import io.scriptor.type.PointerType;
import io.scriptor.type.Type;

public class StringExpression extends Expression {

    private final String value;

    public StringExpression(final SourceLocation location, final Type promise, final String value) {
        super(location, promise);
        this.value = value;
    }

    @Override
    public Value eval(final Environment env) {
        return new ConstValue<>(PointerType.get(Type.get("i8")), value);
    }

    @Override
    public String toString() {
        return "\"%s\"".formatted(Token.unescape(value));
    }
}

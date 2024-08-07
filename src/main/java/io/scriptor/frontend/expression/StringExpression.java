package io.scriptor.frontend.expression;

import static io.scriptor.util.Util.unescape;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.ConstString;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.ArrayType;
import io.scriptor.type.Type;

public class StringExpression extends Expression {

    public static StringExpression create(final SourceLocation location, final Type type, final String value) {
        return new StringExpression(location, type, value);
    }

    private final String value;

    private StringExpression(final SourceLocation location, final Type type, final String value) {
        super(location, type);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "\"%s\"".formatted(unescape(value));
    }

    @Override
    public Value genIR(final IRBuilder builder, final IRModule module) {
        final var type = ArrayType.get(Type.getInt8(module.getContext()), value.length() + 1);
        return module.createGlobal(type, new ConstString(type, value));
    }
}

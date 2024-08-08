package io.scriptor.frontend.expression;

import static io.scriptor.util.Util.unescape;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.ref.RValueRef;
import io.scriptor.backend.ref.ValueRef;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class StringExpr extends Expression {

    public static StringExpr create(final SourceLocation location, final Type type, final String value) {
        return new StringExpr(location, type, value);
    }

    private final String value;

    private StringExpr(final SourceLocation location, final Type type, final String value) {
        super(location, type);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        return "\"%s\"".formatted(unescape(value));
    }

    @Override
    public ValueRef genIR(final IRBuilder builder, final IRModule module) {
        final var ptr = builder.createGlobalString(value, null, module);
        return RValueRef.create(ptr);
    }
}
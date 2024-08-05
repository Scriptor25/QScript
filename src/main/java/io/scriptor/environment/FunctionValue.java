package io.scriptor.environment;

import io.scriptor.expression.Expression;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;

public class FunctionValue extends Value {

    public static interface IFunction {

        Value call(final Environment global, final Value... args);
    }

    public static FunctionValue getDefault(final Type type) {
        return new FunctionValue(null, type, null);
    }

    private final Expression source;
    private final IFunction function;

    public FunctionValue(final Expression source, final Type type, final IFunction function) {
        super(type);
        this.source = source;
        this.function = function;
    }

    public Value call(final Environment global, final Value... args) {
        if (function == null)
            return new UndefinedValue(((FunctionType) getType()).getResult());
        return function.call(global, args);
    }

    @Override
    public boolean getBoolean(final SourceLocation location) {
        return function != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getJava() {
        return (T) function;
    }

    @Override
    public String toString() {
        if (function == null)
            return "undefined";
        if (source == null)
            return "native";
        return source.toString();
    }

    @Override
    public int hashCode() {
        return function.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (o instanceof FunctionValue v)
            return function.equals(v.function);
        return false;
    }
}

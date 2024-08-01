package io.scriptor.environment;

import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;

public class FunctionValue extends Value {

    public static interface IFunction {

        Value call(final Environment global, final Value... args);
    }

    public static FunctionValue getDefault(final Type type) {
        return new FunctionValue(type, null);
    }

    private final IFunction function;

    public FunctionValue(final Type type, final IFunction function) {
        super(type);
        this.function = function;
    }

    public Value call(final Environment global, final Value... args) {
        if (function == null)
            return new UndefinedValue(((FunctionType) getType()).getResult());
        return function.call(global, args);
    }

    @Override
    public boolean getBoolean() {
        return function != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getJava() {
        return (T) function;
    }

    @Override
    public String toString() {
        return function.toString();
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

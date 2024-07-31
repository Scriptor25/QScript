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

    public Value call(final Environment env, final Value... args) {
        if (function == null)
            return new UndefinedValue(((FunctionType) getType()).getResult());
        return function.call(env, args);
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
}

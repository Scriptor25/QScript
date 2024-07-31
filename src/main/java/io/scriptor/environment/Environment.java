package io.scriptor.environment;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.QScriptException;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;

public class Environment {

    private final Environment global;
    private final Environment parent;
    private final Map<String, Symbol> symbols = new HashMap<>();

    private Value[] varargs;

    public Environment() {
        this.parent = null;
        this.global = this;
    }

    public Environment(final Environment parent) {
        this.parent = parent;
        this.global = parent.global;
    }

    public void setVarargs(final Value... values) {
        this.varargs = values;
    }

    public Value getVararg(final int i) {
        return varargs[i];
    }

    public void createSymbol(final Type type, final String id, Value value) {
        if (symbols.containsKey(id))
            throw new QScriptException("symbol '%s' aready defined", id);

        if (value == null)
            value = Value.getDefault(type);

        final var symbol = new Symbol(type, value);
        symbols.put(id, symbol);
    }

    public Symbol getSymbol(final String id) {
        if (!symbols.containsKey(id)) {
            if (parent != null)
                return parent.getSymbol(id);
            throw new QScriptException("undefined symbol '%s'", id);
        }
        return symbols.get(id);
    }

    public Value call(final Value callee, final Value... args) {
        if (!callee.getType().isFunction())
            throw new QScriptException("cannot call on non-function value of type %s", callee.getType());

        return ((FunctionValue) callee).call(global, args);
    }

    public <T> T call(final String id, final Object... args) {
        final var symbol = getSymbol(id);
        final var callee = symbol.getValue();
        final var type = (FunctionType) callee.getType();
        final var vargs = new Value[type.hasVararg() ? args.length : type.getArgCount()];
        for (int i = 0; i < args.length; ++i)
            vargs[i] = ConstValue.fromJava(args[i]);
        for (int i = args.length; i < vargs.length; ++i)
            vargs[i] = new UndefinedValue(type.getArg(i));
        return call(callee, vargs).getJava();
    }
}

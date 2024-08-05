package io.scriptor.environment;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.QScriptException;
import io.scriptor.parser.SourceLocation;
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

    public Symbol declareSymbol(final SourceLocation location, final Type type, final String id) {
        return symbols.computeIfAbsent(id, key -> new Symbol(location, type, key, new UndefinedValue(type)));
    }

    public void defineSymbol(final SourceLocation location, final Type type, final String id, final Value value) {
        final var symbol = declareSymbol(location, type, id);
        if (!(symbol.getValue() == null || symbol.getValue() instanceof UndefinedValue))
            throw new QScriptException(location, "symbol '%s' aready defined", id);
        symbol.setValue(location, value);
    }

    public Symbol getSymbol(final SourceLocation location, final String id) {
        if (!symbols.containsKey(id)) {
            if (parent != null)
                return parent.getSymbol(location, id);
            throw new QScriptException(location, "undefined symbol '%s'", id);
        }
        return symbols.get(id);
    }

    public Value call(final SourceLocation location, final Value callee, final Value... args) {
        if (!callee.getType().isFunction())
            throw new QScriptException(location, "cannot create call on non-function value of type %s",
                    callee.getType());

        if (callee instanceof UndefinedValue)
            throw new QScriptException(location, "cannot create call on undefined function");

        final var type = (FunctionType) callee.getType();
        if (type.hasVararg() && type.getArgCount() > args.length)
            throw new QScriptException(location, "not enough arguments");
        if (!type.hasVararg() && type.getArgCount() != args.length)
            throw new QScriptException(location, "wrong number of arguments");
        for (int i = 0; i < type.getArgCount(); ++i)
            if (type.getArg(location, i) != args[i].getType())
                args[i] = Operation.cast(location, args[i], type.getArg(location, i));

        return ((FunctionValue) callee).call(global, args);
    }

    public <T> T call(final SourceLocation location, final String id, final Object... args) {
        final var symbol = getSymbol(location, id);
        final var callee = symbol.getValue();
        final var type = (FunctionType) callee.getType();
        final var vargs = new Value[type.hasVararg() ? args.length : type.getArgCount()];
        for (int i = 0; i < args.length; ++i)
            vargs[i] = ConstValue.fromJava(location, args[i]);
        for (int i = args.length; i < vargs.length; ++i)
            vargs[i] = new UndefinedValue(type.getArg(location, i));
        return call(location, callee, vargs).getJava();
    }

    public EnvState getState() {
        final var state = new EnvState();
        for (final var entry : symbols.entrySet())
            state.declareSymbol(entry.getValue().getType(), entry.getKey());
        return state;
    }
}

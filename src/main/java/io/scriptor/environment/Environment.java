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

    public Symbol declareSymbol(final Type type, final String id) {
        return symbols.computeIfAbsent(id, key -> new Symbol(type, new UndefinedValue(type)));
    }

    public void defineSymbol(final Type type, final String id, final Value value) {
        final var symbol = declareSymbol(type, id);
        if (!(symbol.getValue() instanceof UndefinedValue))
            throw new QScriptException("symbol '%s' aready defined", id);

        symbol.setValue(value != null ? value : Value.getDefault(type));
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
            throw new QScriptException("cannot make call non-function value of type %s", callee.getType());

        final var type = (FunctionType) callee.getType();
        if (type.hasVararg() && type.getArgCount() > args.length)
            throw new QScriptException();
        if (!type.hasVararg() && type.getArgCount() != args.length)
            throw new QScriptException();
        for (int i = 0; i < type.getArgCount(); ++i)
            if (type.getArg(i) != args[i].getType())
                args[i] = Operation.cast(args[i], type.getArg(i));

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

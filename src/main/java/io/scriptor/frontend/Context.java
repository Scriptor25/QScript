package io.scriptor.frontend;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class Context {

    private final Context global;
    private final Context parent;

    private final Map<String, Type> types;
    private final Map<String, Symbol> symbols = new HashMap<>();

    public Context() {
        this.global = this;
        this.parent = null;
        this.types = new HashMap<>();

        new Type(this, "void", Type.IS_VOID, 0);
        new Type(this, "i1", Type.IS_INTEGER, 1);
        new Type(this, "i8", Type.IS_INTEGER, 8);
        new Type(this, "i16", Type.IS_INTEGER, 16);
        new Type(this, "i32", Type.IS_INTEGER, 32);
        new Type(this, "i64", Type.IS_INTEGER, 64);
        new Type(this, "f32", Type.IS_FLOAT, 32);
        new Type(this, "f64", Type.IS_FLOAT, 64);
    }

    public Context(final Context parent) {
        this.global = parent.global;
        this.parent = parent;
        this.types = parent.types;
    }

    public void putType(final String id, final Type type) {
        types.put(id, type);
    }

    public boolean existsType(final String id) {
        return types.containsKey(id);
    }

    @SuppressWarnings("unchecked")
    public <T extends Type> T getType(final String id) {
        if (!existsType(id))
            throw new QScriptException("no such type with id '%s'", id);
        return (T) types.get(id);
    }

    public Symbol declareSymbol(final Type type, final String id) {
        return symbols.computeIfAbsent(id, key -> new Symbol(id, type));
    }

    public Symbol getSymbol(final SourceLocation location, final String id) {
        if (!symbols.containsKey(id)) {
            if (parent != null)
                return parent.getSymbol(location, id);
            throw new QScriptException(location, "undefined symbol '%s'", id);
        }
        return symbols.get(id);
    }
}

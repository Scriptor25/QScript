package io.scriptor.frontend;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.frontend.statement.Statement;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class State {

    private final State global;
    private final State parent;

    private final Map<String, Type> types;
    private final Map<String, Symbol> symbols = new HashMap<>();
    private final Map<String, Statement> marcos = new HashMap<>();

    public State() {
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

    public State(final State parent) {
        this.global = parent.global;
        this.parent = parent;
        this.types = parent.types;
    }

    public State getParent() {
        return parent;
    }

    public void putType(final String name, final Type type) {
        types.put(name, type);
    }

    public boolean existsType(final String name) {
        return types.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Type> T getType(final SourceLocation sl, final String name) {
        if (!existsType(name))
            throw new QScriptException(sl, "no such type '%s'", name);
        return (T) types.get(name);
    }

    public boolean existsSymbol(final String name) {
        if (symbols.containsKey(name))
            return true;
        if (parent == null)
            return false;
        return parent.existsSymbol(name);
    }

    public Symbol declareSymbol(final Type type, final String name) {
        return symbols.computeIfAbsent(name, key -> new Symbol(name, type));
    }

    public Symbol getSymbol(final SourceLocation sl, final String name) {
        if (symbols.containsKey(name))
            return symbols.get(name);
        if (parent == null)
            throw new QScriptException(sl, "undefined symbol '%s'", name);
        return parent.getSymbol(sl, name);
    }

    public void putMacro(final String name, final Statement stmt) {
        if (existsMacro(name))
            System.err.printf(
                    "warning: overriding macro '%s' at %s, first defined at %s\n",
                    name,
                    stmt.getSl(),
                    getMacro(stmt.getSl(), name).getSl());
        marcos.put(name, stmt);
    }

    public boolean existsMacro(final String name) {
        if (marcos.containsKey(name))
            return true;
        if (parent == null)
            return false;
        return parent.existsMacro(name);
    }

    @SuppressWarnings("unchecked")
    public <S extends Statement> S getMacro(final SourceLocation sl, final String name) {
        if (marcos.containsKey(name))
            return (S) marcos.get(name);
        if (parent == null)
            throw new QScriptException(sl, "undefined macro '%s'", name);
        return parent.getMacro(sl, name);
    }
}

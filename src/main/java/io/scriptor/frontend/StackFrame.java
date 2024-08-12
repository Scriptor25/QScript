package io.scriptor.frontend;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.frontend.stmt.Stmt;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class StackFrame {

    private final StackFrame global;
    private final StackFrame parent;

    private final Map<String, Type> types;
    private final Map<String, Stmt> marcos = new HashMap<>();
    private final Map<String, Type> symbols = new HashMap<>();

    public StackFrame() {
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

    public StackFrame(final StackFrame parent) {
        this.global = parent.global;
        this.parent = parent;
        this.types = parent.types;
    }

    public StackFrame getParent() {
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

    public void putMacro(final String name, final Stmt stmt) {
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
    public <S extends Stmt> S getMacro(final SourceLocation sl, final String name) {
        if (marcos.containsKey(name))
            return (S) marcos.get(name);
        if (parent == null)
            throw new QScriptException(sl, "undefined macro '%s'", name);
        return parent.getMacro(sl, name);
    }

    public boolean existsSymbol(final String name) {
        if (symbols.containsKey(name))
            return true;
        if (parent == null)
            return false;
        return parent.existsSymbol(name);
    }

    public Type declareSymbol(final String name, final Type ty) {
        return symbols.computeIfAbsent(name, key -> ty);
    }

    public Type getSymbol(final SourceLocation sl, final String name) {
        if (symbols.containsKey(name))
            return symbols.get(name);
        if (parent == null)
            throw new QScriptException(sl, "undefined symbol '%s'", name);
        return parent.getSymbol(sl, name);
    }
}

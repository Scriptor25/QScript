package io.scriptor.frontend;

import static org.bytedeco.llvm.global.LLVM.LLVMContextCreate;
import static org.bytedeco.llvm.global.LLVM.LLVMContextDispose;

import java.util.HashMap;
import java.util.Map;

import org.bytedeco.llvm.LLVM.LLVMContextRef;

import io.scriptor.frontend.statement.Statement;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class Context {

    private final LLVMContextRef llvm;
    private final Context global;
    private final Context parent;

    private final Map<String, Type> types;
    private final Map<String, Symbol> symbols = new HashMap<>();
    private final Map<String, Statement> marcos = new HashMap<>();

    public Context() {
        this.llvm = LLVMContextCreate();
        this.global = this;
        this.parent = null;
        this.types = new HashMap<>();
    }

    public Context(final Context parent) {
        this.llvm = parent.llvm;
        this.global = parent.global;
        this.parent = parent;
        this.types = parent.types;
    }

    public void dispose() {
        if (global == this)
            LLVMContextDispose(llvm);
    }

    public LLVMContextRef getLLVM() {
        return llvm;
    }

    public void clear() {
        types.clear();
        new Type(this, "void", Type.IS_VOID, 0);
        new Type(this, "i1", Type.IS_INTEGER, 1);
        new Type(this, "i8", Type.IS_INTEGER, 8);
        new Type(this, "i16", Type.IS_INTEGER, 16);
        new Type(this, "i32", Type.IS_INTEGER, 32);
        new Type(this, "i64", Type.IS_INTEGER, 64);
        new Type(this, "f32", Type.IS_FLOAT, 32);
        new Type(this, "f64", Type.IS_FLOAT, 64);

        symbols.clear();
        marcos.clear();
    }

    public void putType(final String name, final Type type) {
        types.put(name, type);
    }

    public boolean existsType(final String name) {
        return types.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Type> T getType(final String name) {
        if (!existsType(name))
            throw new QScriptException("no such type '%s'", name);
        return (T) types.get(name);
    }

    public boolean existsSymbol(final String name) {
        if (symbols.containsKey(name))
            return true;
        if (parent == null)
            return false;
        return true;
    }

    public Symbol declareSymbol(final Type type, final String name) {
        return symbols.computeIfAbsent(name, key -> new Symbol(name, type));
    }

    public Symbol getSymbol(final String name) {
        if (symbols.containsKey(name))
            return symbols.get(name);
        if (parent == null)
            throw new QScriptException("undefined symbol '%s'", name);
        return parent.getSymbol(name);
    }

    public void putMacro(final String name, final Statement stmt) {
        if (existsMacro(name))
            System.err.printf(
                    "warning: overriding macro '%s' at %s, first defined at %s\n",
                    name,
                    stmt.getLocation(),
                    getMacro(name).getLocation());
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
    public <S extends Statement> S getMacro(final String name) {
        if (marcos.containsKey(name))
            return (S) marcos.get(name);
        if (parent == null)
            throw new QScriptException("undefined macro '%s'", name);
        return parent.getMacro(name);
    }
}

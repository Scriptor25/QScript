package io.scriptor.backend;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.backend.value.ConstValue;
import io.scriptor.backend.value.Function;
import io.scriptor.backend.value.GlobalValue;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;

public class IRModule {

    private final String name;
    private final IRContext context;

    private final Map<String, GlobalValue> globals = new HashMap<>();

    public IRModule(final String name, final IRContext context) {
        this.name = name;
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public IRContext getContext() {
        return context;
    }

    public GlobalValue getGlobal(final String id) {
        return globals.get(id);
    }

    public GlobalValue createGlobal(final Type type, final ConstValue init) {
        final var symbol = new GlobalValue(type, init);
        globals.put(name, symbol);
        return symbol;
    }

    public GlobalValue createGlobal(final Type type, final ConstValue init, final String name) {
        final var symbol = new GlobalValue(type, init, name);
        globals.put(name, symbol);
        return symbol;
    }

    public void createCtor(final Block block) {
        throw new UnsupportedOperationException("Unimplemented method 'createCtor'");
    }

    public Function getFunction(final String name) {
        return (Function) globals.get(name);
    }

    public Function getFunction(final FunctionType type, final String name) {
        return (Function) globals.computeIfAbsent(name, key -> new Function(type, key));
    }
}

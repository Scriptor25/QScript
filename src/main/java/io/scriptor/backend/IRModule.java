package io.scriptor.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.scriptor.backend.value.Function;
import io.scriptor.backend.value.ConstValue;
import io.scriptor.backend.value.GlobalValue;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;

public class IRModule {

    private final String name;
    private final IRContext context;

    private final Map<String, GlobalValue> globals = new HashMap<>();
    private final List<Block> ctors = new ArrayList<>();

    public IRModule(final String name, final IRContext context) {
        this.name = name;
        this.context = context;
    }

    public void dump() {
        System.out.printf("name = \"%s\"\n", name);
        System.out.println();
        globals
                .values()
                .stream()
                .filter(g -> !(g instanceof Function))
                .forEach(g -> {
                    g.dump();
                    System.out.println();
                });

        for (final var block : ctors) {
            System.out.println();
            System.out.println("ctor {");
            block.dumpContent();
            System.out.println();
            System.out.println("}");
        }

        globals
                .values()
                .stream()
                .filter(g -> (g instanceof Function f) ? f.isEmpty() : false)
                .forEach(g -> {
                    System.out.println();
                    g.dump();
                    System.out.println();
                });

        globals
                .values()
                .stream()
                .filter(g -> (g instanceof Function f) ? !f.isEmpty() : false)
                .forEach(g -> {
                    System.out.println();
                    g.dump();
                    System.out.println();
                });
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
        ctors.add(block);
    }

    public Function getFunction(final String name) {
        return (Function) globals.get(name);
    }

    public Function getFunction(final FunctionType type, final String name) {
        return (Function) globals.computeIfAbsent(name, key -> new Function(type, key));
    }
}

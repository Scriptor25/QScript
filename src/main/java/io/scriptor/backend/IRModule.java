package io.scriptor.backend;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.Symbol;
import io.scriptor.backend.value.ConstValue;
import io.scriptor.type.Type;

public class IRModule {

    private final String name;
    private final IRContext context;
    private final Map<String, Symbol> symbols = new HashMap<>();

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

    public Symbol getSymbol(final String id) {
        return symbols.get(id);
    }

    public Symbol createSymbol(final String id, final Type type, final ConstValue init) {
        final var symbol = new Symbol(id, type, init);
        symbols.put(id, symbol);
        return symbol;
    }
}

package io.scriptor.frontend;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.QScriptException;
import io.scriptor.Symbol;
import io.scriptor.type.Type;

public class State {

    private final State global;
    private final State parent;

    private final Map<String, Symbol> symbols = new HashMap<>();

    public State() {
        this.global = this;
        this.parent = null;
    }

    public State(final State parent) {
        this.global = parent.global;
        this.parent = parent;
    }

    public Symbol declareSymbol(final Type type, final String id) {
        return symbols.computeIfAbsent(id, key -> new Symbol(id, type, null));
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

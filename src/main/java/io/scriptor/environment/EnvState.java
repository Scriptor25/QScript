package io.scriptor.environment;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.QScriptException;
import io.scriptor.type.Type;

public class EnvState {

    private final EnvState global;
    private final EnvState parent;

    private final Map<String, Symbol> symbols = new HashMap<>();

    public EnvState() {
        this.global = this;
        this.parent = null;
    }

    public EnvState(final EnvState parent) {
        this.global = parent.global;
        this.parent = parent;
    }

    public Symbol declareSymbol(final Type type, final String id) {
        return symbols.computeIfAbsent(id, key -> new Symbol(type, id));
    }

    public Symbol getSymbol(final String id) {
        if (!symbols.containsKey(id)) {
            if (parent != null)
                return parent.getSymbol(id);
            throw new QScriptException("undefined symbol '%s'", id);
        }
        return symbols.get(id);
    }
}

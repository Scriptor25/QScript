package io.scriptor.expression;

import io.scriptor.environment.EnvState;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Symbol;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;

public class IDExpression extends Expression {

    private final String id;

    public IDExpression(final SourceLocation location, final EnvState state, final String id) {
        this(location, state.getSymbol(id));
    }

    public IDExpression(final SourceLocation location, final Symbol symbol) {
        super(location, symbol.getType());
        this.id = symbol.getId();
    }

    @Override
    public Value eval(final Environment env) {
        return env.getSymbol(id).getValue();
    }

    @Override
    public String toString() {
        return id;
    }
}

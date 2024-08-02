package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.environment.EnvState;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class IDExpression extends Expression {

    private final String id;

    public static IDExpression create(final SourceLocation location, final EnvState state, final String id) {
        rtassert(location != null);
        rtassert(state != null);
        rtassert(id != null);
        final var symbol = state.getSymbol(id);
        rtassert(symbol != null);
        return new IDExpression(location, symbol.getType(), symbol.getId());
    }

    private IDExpression(final SourceLocation location, final Type type, final String id) {
        super(location, type);
        this.id = id;
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

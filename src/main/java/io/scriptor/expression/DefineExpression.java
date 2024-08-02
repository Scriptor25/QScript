package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.environment.EnvState;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class DefineExpression extends Expression {

    public static DefineExpression create(
            final SourceLocation location,
            final EnvState state,
            final Type type,
            final String id) {
        rtassert(location != null);
        rtassert(state != null);
        rtassert(type != null);
        rtassert(id != null);
        return new DefineExpression(location, state, type, id, null);
    }

    public static DefineExpression create(
            final SourceLocation location,
            final EnvState state,
            final Type type,
            final String id,
            final Expression init) {
        rtassert(location != null);
        rtassert(state != null);
        rtassert(type != null);
        rtassert(id != null);
        rtassert(init != null);
        return new DefineExpression(location, state, type, id, init);
    }

    private final Type type;
    private final String id;
    private final Expression init;

    private DefineExpression(
            final SourceLocation location,
            final EnvState state,
            final Type type,
            final String id,
            final Expression init) {
        super(location, null);
        this.type = type;
        this.id = id;
        this.init = init;
        state.declareSymbol(type, id);
    }

    @Override
    public Value eval(final Environment env) {
        if (init != null)
            env.defineSymbol(type, id, init == null ? Value.getDefault(type) : init.eval(env));
        else if (type.isFunction())
            env.declareSymbol(type, id);
        return null;
    }

    @Override
    public String toString() {
        if (init == null)
            return "def %s %s".formatted(type, id);
        return "def %s %s = %s".formatted(type, id, init);
    }
}

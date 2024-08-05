package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
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
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(state != null, () -> new QScriptException(location, "state is null"));
        rtassert(type != null, () -> new QScriptException(location, "type is null"));
        rtassert(id != null, () -> new QScriptException(location, "id is null"));
        return new DefineExpression(location, state, type, id, null);
    }

    public static DefineExpression create(
            final SourceLocation location,
            final EnvState state,
            final Type type,
            final String id,
            final Expression init) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(state != null, () -> new QScriptException(location, "state is null"));
        rtassert(type != null, () -> new QScriptException(location, "type is null"));
        rtassert(id != null, () -> new QScriptException(location, "id is null"));
        rtassert(init != null, () -> new QScriptException(location, "init is null"));
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
            env.defineSymbol(
                    getLocation(),
                    type,
                    id,
                    init == null
                            ? Value.getDefault(getLocation(), type)
                            : init.eval(env));
        else if (type.isFunction())
            env.declareSymbol(getLocation(), type, id);
        return null;
    }

    @Override
    public String toString() {
        if (init == null)
            return "def %s %s".formatted(type, id);
        return "def %s %s = %s".formatted(type, id, init);
    }
}

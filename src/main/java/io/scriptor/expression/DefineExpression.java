package io.scriptor.expression;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class DefineExpression extends Expression {

    private final Type type;
    private final String id;
    private final Expression init;

    public DefineExpression(final SourceLocation location, final Type type, final String id) {
        this(location, type, id, null);
    }

    public DefineExpression(final SourceLocation location, final Type type, final String id, final Expression init) {
        super(location, null);
        this.type = type;
        this.id = id;
        this.init = init;
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Expression getInit() {
        return init;
    }

    @Override
    public Value eval(final Environment env) {
        if (init == null && type.isFunction()) {
            env.declareSymbol(type, id);
        } else {
            env.defineSymbol(type, id, init.eval(env));
        }
        return null;
    }

    @Override
    public String toString() {
        if (init == null)
            return "def %s %s".formatted(type, id);
        return "def %s %s = %s".formatted(type, id, init);
    }
}

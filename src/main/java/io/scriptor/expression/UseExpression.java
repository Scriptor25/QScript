package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class UseExpression extends Expression {

    public static UseExpression create(final SourceLocation location, final String id, final Type type) {
        rtassert(location != null);
        rtassert(id != null);
        rtassert(type != null);
        return new UseExpression(location, id, type);
    }

    private final String id;
    private final Type type;

    private UseExpression(final SourceLocation location, final String id, final Type type) {
        super(location, null);
        this.id = id;
        this.type = type;
    }

    public void use() {
        Type.use(id, type);
    }

    @Override
    public Value eval(final Environment env) {
        return null;
    }

    @Override
    public String toString() {
        return "use %s as %s".formatted(id, type);
    }
}

package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.statement.Statement;
import io.scriptor.type.Type;

public abstract class Expression extends Statement {

    private final Type type;

    protected Expression(final SourceLocation location, final Type type) {
        super(location);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isConst() {
        return false;
    }
}

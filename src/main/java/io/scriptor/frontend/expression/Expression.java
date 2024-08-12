package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.statement.Statement;
import io.scriptor.type.Type;

public abstract class Expression extends Statement {

    private final Type ty;

    protected Expression(final SourceLocation sl, final Type ty) {
        super(sl);
        this.ty = ty;
    }

    public Type getTy() {
        return ty;
    }

    public boolean isConst() {
        return false;
    }
}

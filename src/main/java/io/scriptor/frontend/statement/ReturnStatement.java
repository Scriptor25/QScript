package io.scriptor.frontend.statement;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.expression.Expression;
import io.scriptor.type.Type;

public class ReturnStatement extends Statement {

    public static ReturnStatement create(
            final SourceLocation sl,
            final Type res,
            final Expression val) {
        return new ReturnStatement(sl, res, val);
    }

    public static ReturnStatement create(
            final SourceLocation sl,
            final Type res) {
        return new ReturnStatement(sl, res, null);
    }

    private final Type res;
    private final Expression val;

    private ReturnStatement(
            final SourceLocation sl,
            final Type res,
            final Expression val) {
        super(sl);
        this.res = res;
        this.val = val;
    }

    public Type getRes() {
        return res;
    }

    public Expression getVal() {
        return val;
    }

    public boolean hasVal() {
        return val != null;
    }

    @Override
    public String toString() {
        return "return %s".formatted(val);
    }
}

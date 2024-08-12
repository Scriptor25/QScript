package io.scriptor.frontend.expr;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.stmt.CompoundStmt;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class FunctionExpr extends Expr {

    public static FunctionExpr create(
            final SourceLocation sl,
            final Type ty,
            final String[] args,
            final CompoundStmt body) {
        return new FunctionExpr(sl, ty, args, body);
    }

    private final String[] args;
    private final CompoundStmt body;

    private FunctionExpr(
            final SourceLocation sl,
            final Type ty,
            final String[] args,
            final CompoundStmt body) {
        super(sl, ty);

        if (ty == null)
            throw new QScriptException(sl, "function expression must have a promise type");

        this.args = args;
        this.body = body;
    }

    public int getArgCount() {
        return args.length;
    }

    public String getArg(final int i) {
        return args[i];
    }

    public CompoundStmt getBody() {
        return body;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder()
                .append("$(");
        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }

        return builder
                .append(") ")
                .append(body)
                .toString();
    }
}

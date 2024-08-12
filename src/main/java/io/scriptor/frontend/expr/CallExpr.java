package io.scriptor.frontend.expr;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class CallExpr extends Expr {

    public static CallExpr create(
            final SourceLocation sl,
            final Type ty,
            final Expr callee,
            final Expr[] args) {
        return new CallExpr(sl, ty, callee, args);
    }

    private final Expr callee;
    private final Expr[] args;

    private CallExpr(
            final SourceLocation sl,
            final Type ty,
            final Expr callee,
            final Expr[] args) {
        super(sl, ty);
        this.callee = callee;
        this.args = args;
    }

    public Expr getCallee() {
        return callee;
    }

    public int getArgCount() {
        return args.length;
    }

    public Expr getArg(final int index) {
        return args[index];
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }
        return "%s(%s)".formatted(callee, builder);
    }
}

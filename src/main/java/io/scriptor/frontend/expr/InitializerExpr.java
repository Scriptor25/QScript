package io.scriptor.frontend.expr;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class InitializerExpr extends Expr {

    public static InitializerExpr create(final SourceLocation sl, final Type ty, final Expr... args) {
        return new InitializerExpr(sl, ty, args);
    }

    private final Expr[] args;

    private InitializerExpr(final SourceLocation sl, final Type ty, final Expr[] args) {
        super(sl, ty);
        this.args = args;
    }

    public int getArgCount() {
        return args.length;
    }

    public Expr getArg(final int i) {
        return args[i];
    }

    @Override
    public String toString() {
        if (args.length == 0)
            return "{}";

        final var builder = new StringBuilder()
                .append("{ ");
        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }
        return builder
                .append(" }")
                .toString();
    }
}

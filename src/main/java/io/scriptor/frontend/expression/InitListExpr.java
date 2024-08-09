package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class InitListExpr extends Expression {

    public static InitListExpr create(final SourceLocation location, final Type type, final Expression... args) {
        return new InitListExpr(location, type, args);
    }

    private final Expression[] args;

    private InitListExpr(final SourceLocation location, final Type type, final Expression[] args) {
        super(location, type);
        this.args = args;
    }

    public int getArgCount() {
        return args.length;
    }

    public Expression getArg(final int i) {
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

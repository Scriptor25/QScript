package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class InitListExpression extends Expression {

    public static InitListExpression create(final SourceLocation sl, final Type ty, final Expression... args) {
        return new InitListExpression(sl, ty, args);
    }

    private final Expression[] args;

    private InitListExpression(final SourceLocation sl, final Type ty, final Expression[] args) {
        super(sl, ty);
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

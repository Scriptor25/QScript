package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class CallExpression extends Expression {

    public static CallExpression create(
            final SourceLocation sl,
            final Type ty,
            final Expression callee,
            final Expression[] args) {
        return new CallExpression(sl, ty, callee, args);
    }

    private final Expression callee;
    private final Expression[] args;

    private CallExpression(
            final SourceLocation sl,
            final Type ty,
            final Expression callee,
            final Expression[] args) {
        super(sl, ty);
        this.callee = callee;
        this.args = args;
    }

    public Expression getCallee() {
        return callee;
    }

    public int getArgCount() {
        return args.length;
    }

    public Expression getArg(final int index) {
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

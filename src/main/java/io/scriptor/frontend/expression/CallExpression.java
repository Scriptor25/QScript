package io.scriptor.frontend.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class CallExpression extends Expression {

    public static CallExpression create(
            final SourceLocation location,
            final Type result,
            final Expression callee,
            final Expression[] args) {
        return new CallExpression(location, result, callee, args);
    }

    private final Expression callee;
    private final Expression[] args;

    private CallExpression(
            final SourceLocation location,
            final Type result,
            final Expression callee,
            final Expression[] args) {
        super(location, result);
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

    @Override
    public Value gen(final IRBuilder builder, final IRModule module) {
        throw new UnsupportedOperationException();
    }
}

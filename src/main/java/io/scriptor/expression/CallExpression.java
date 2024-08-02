package io.scriptor.expression;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class CallExpression extends Expression {

    private final Expression callee;
    private final Expression[] args;

    public CallExpression(
            final SourceLocation location,
            final Type result,
            final Expression callee,
            final Expression[] args) {
        super(location, result);
        this.callee = callee;
        this.args = args;
    }

    @Override
    public Value eval(final Environment env) {
        final var callee = this.callee.eval(env);
        final var args = new Value[this.args.length];
        for (int i = 0; i < args.length; ++i)
            args[i] = this.args[i].eval(env);
        return env.call(callee, args);
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

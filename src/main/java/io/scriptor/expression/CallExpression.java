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
            final Type promise,
            final Expression callee,
            final Expression[] args) {
        super(location, promise);
        this.callee = callee;
        this.args = args;
    }

    public Expression getCallee() {
        return callee;
    }

    public int getArgCount() {
        return args.length;
    }

    public Expression getArg(final int i) {
        return args[i];
    }

    @Override
    public Value eval(final Environment env) {
        final var vcallee = callee.eval(env);
        final var vargs = new Value[args.length];
        for (int i = 0; i < vargs.length; ++i)
            vargs[i] = args[i].eval(env);
        return env.call(vcallee, vargs);
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

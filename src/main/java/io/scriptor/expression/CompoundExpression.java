package io.scriptor.expression;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;

public class CompoundExpression extends Expression {

    private static int depth = 0;

    public static String space() {
        final var builder = new StringBuilder();
        for (int i = 0; i < depth; ++i)
            builder.append("    ");
        return builder.toString();
    }

    public static String indent() {
        ++depth;
        return space();
    }

    public static String unindent() {
        --depth;
        return space();
    }

    private final Expression[] expressions;

    public CompoundExpression(final SourceLocation location, final Expression[] expressions) {
        super(location, null);
        this.expressions = expressions;
    }

    @Override
    public Value eval(final Environment env) {
        final var subenv = new Environment(env);
        for (final var expression : expressions) {
            final var value = expression.eval(subenv);
            if (value != null && value.isReturn())
                return value;
        }
        return null;
    }

    @Override
    public String toString() {
        if (expressions.length == 0)
            return "{}";

        final var builder = new StringBuilder();
        final var indent = indent();
        for (final var expression : expressions)
            builder.append(indent).append(expression).append('\n');

        return "{%n%s%s}".formatted(builder, unindent());
    }
}

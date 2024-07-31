package io.scriptor.expression;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;

public class CompoundExpression extends Expression implements Iterable<Expression> {

    private static int depth = 0;

    private static String space() {
        final var builder = new StringBuilder();
        for (int i = 0; i < depth; ++i)
            builder.append("    ");
        return builder.toString();
    }

    private static String indent() {
        ++depth;
        return space();
    }

    private static String unindent() {
        --depth;
        return space();
    }

    private final Expression[] expressions;

    public CompoundExpression(final SourceLocation location, final Expression[] expressions) {
        super(location, null);
        this.expressions = expressions;
    }

    public int getExpressionCount() {
        return expressions.length;
    }

    public Expression getExpression(final int i) {
        return expressions[i];
    }

    public Stream<Expression> stream() {
        return Arrays.stream(expressions);
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
        if (expressions.length == 1)
            return "{ %s }".formatted(expressions[0]);

        final var builder = new StringBuilder();
        final var indent = indent();
        for (final var expression : expressions)
            builder.append(indent).append(expression).append('\n');

        return "{%n%s%s}".formatted(builder, unindent());
    }

    @Override
    public Iterator<Expression> iterator() {
        return new Iterator<>() {

            int i = 0;

            @Override
            public boolean hasNext() {
                return i < expressions.length;
            }

            @Override
            public Expression next() {
                return expressions[i++];
            }
        };
    }
}

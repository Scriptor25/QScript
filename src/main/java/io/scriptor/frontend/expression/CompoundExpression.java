package io.scriptor.frontend.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;

public class CompoundExpression extends Expression {

    public static CompoundExpression create(final SourceLocation location, final Expression[] expressions) {
        return new CompoundExpression(location, expressions);
    }

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

    private CompoundExpression(final SourceLocation location, final Expression[] expressions) {
        super(location, null);
        this.expressions = expressions;
    }

    public int getExpressionCount() {
        return expressions.length;
    }

    public Expression getExpression(final int index) {
        return expressions[index];
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

    @Override
    public Value genIR(final IRBuilder builder, final IRModule module) {
        builder.push();
        for (final var expression : expressions)
            expression.genIR(builder, module);
        builder.pop();
        return null;
    }
}

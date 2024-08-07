package io.scriptor.frontend.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class FunctionExpression extends Expression {

    public static FunctionExpression create(
            final SourceLocation location,
            final Type type,
            final String[] args,
            final Expression[] expressions) {
        return new FunctionExpression(location, type, args, expressions);
    }

    private final String[] args;
    private final Expression[] expressions;

    private FunctionExpression(
            final SourceLocation location,
            final Type type,
            final String[] args,
            final Expression[] expressions) {
        super(location, type);

        if (type == null)
            throw new QScriptException(location, "function expression must have a promise type");

        this.args = args;
        this.expressions = expressions;
    }

    public int getArgCount() {
        return args.length;
    }

    public String getArg(final int index) {
        return args[index];
    }

    public int getExpressionCount() {
        return expressions.length;
    }

    public Expression getExpression(final int index) {
        return expressions[index];
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder()
                .append("$(");
        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }
        builder.append(") {");

        if (expressions.length == 0)
            return builder
                    .append("}")
                    .toString();
        builder.append('\n');

        final var indent = CompoundExpression.indent();
        for (final var expression : expressions)
            builder
                    .append(indent)
                    .append(expression)
                    .append('\n');

        return builder
                .append(CompoundExpression.unindent())
                .append("}")
                .toString();
    }

    @Override
    public Value gen(final IRBuilder builder, final IRModule module) {
        throw new UnsupportedOperationException();
    }
}

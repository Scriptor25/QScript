package io.scriptor.frontend.expression;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class FunctionExpr extends Expression {

    public static FunctionExpr create(
            final SourceLocation location,
            final Type type,
            final String[] args,
            final CompoundExpr body) {
        return new FunctionExpr(location, type, args, body);
    }

    private final String[] args;
    private final CompoundExpr body;

    private FunctionExpr(
            final SourceLocation location,
            final Type type,
            final String[] args,
            final CompoundExpr body) {
        super(location, type);

        if (type == null)
            throw new QScriptException(location, "function expression must have a promise type");

        this.args = args;
        this.body = body;
    }

    public int getArgCount() {
        return args.length;
    }

    public String getArg(final int index) {
        return args[index];
    }

    public CompoundExpr getBody() {
        return body;
    }

    @Override
    public boolean isConst() {
        return true;
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

        return builder
                .append(") ")
                .append(body)
                .toString();
    }
}

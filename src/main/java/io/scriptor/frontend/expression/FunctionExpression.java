package io.scriptor.frontend.expression;

import io.scriptor.backend.Block;
import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Function;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class FunctionExpression extends Expression {

    public static FunctionExpression create(
            final SourceLocation location,
            final Type type,
            final String[] args,
            final CompoundExpression body) {
        return new FunctionExpression(location, type, args, body);
    }

    private final String[] args;
    private final CompoundExpression body;

    private FunctionExpression(
            final SourceLocation location,
            final Type type,
            final String[] args,
            final CompoundExpression body) {
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

    public CompoundExpression getBody() {
        return body;
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

    @Override
    public Value genIR(final IRBuilder builder, final IRModule module) {
        final var type = (FunctionType) getType();
        final var function = new Function(type);

        final var entry = new Block(function, "entry");
        final var bkpInsertPoint = builder.getInsertPoint();
        builder.setInsertPoint(entry);

        type.getResult();
        type.getArgs();
        type.isVarArg();

        builder.setInsertPoint(bkpInsertPoint);
        return function;
    }
}

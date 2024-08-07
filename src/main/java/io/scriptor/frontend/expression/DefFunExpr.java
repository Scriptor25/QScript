package io.scriptor.frontend.expression;

import java.util.Arrays;

import io.scriptor.frontend.Arg;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;

public class DefFunExpr extends Expression {

    public static DefFunExpr create(
            final SourceLocation location,
            final Type result,
            final String name,
            final Arg[] args,
            final boolean vararg) {
        return create(location, result, name, args, vararg, null);
    }

    public static DefFunExpr create(
            final SourceLocation location,
            final Type result,
            final String name,
            final Arg[] args,
            final boolean vararg,
            final CompoundExpr body) {
        return new DefFunExpr(location, result, name, args, vararg, body);
    }

    private final Type result;
    private final String name;
    private final Arg[] args;
    private final boolean vararg;
    private final CompoundExpr body;

    private DefFunExpr(
            final SourceLocation location,
            final Type result,
            final String name,
            final Arg[] args,
            final boolean vararg,
            final CompoundExpr body) {
        super(location, null);
        this.result = result;
        this.name = name;
        this.args = args;
        this.vararg = vararg;
        this.body = body;
    }

    public Type getResult() {
        return result;
    }

    public String getName() {
        return name;
    }

    public int getArgCount() {
        return args.length;
    }

    public Arg getArg(final int i) {
        return args[i];
    }

    public boolean isVarArg() {
        return vararg;
    }

    public CompoundExpr getBody() {
        return body;
    }

    public FunctionType getFunctionType() {
        return FunctionType.get(result, vararg, Arrays.stream(args).map(Arg::type).toArray(Type[]::new));
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }

        if (vararg) {
            if (args.length > 0)
                builder.append(", ");
            builder.append('?');
        }

        if (body == null)
            return "def %s %s(%s)".formatted(result, name, builder);
        return "def %s %s(%s) %s".formatted(result, name, builder, body);
    }
}

package io.scriptor.frontend.stmt;

import java.util.Arrays;

import io.scriptor.frontend.Arg;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;

public class DefFunctionStmt extends Stmt {

    public static DefFunctionStmt create(
            final SourceLocation sl,
            final Type res,
            final String name,
            final Arg[] args,
            final boolean va) {
        return create(sl, res, name, args, va, null);
    }

    public static DefFunctionStmt create(
            final SourceLocation sl,
            final Type res,
            final String name,
            final Arg[] args,
            final boolean va,
            final CompoundStmt body) {
        return new DefFunctionStmt(sl, res, name, args, va, body);
    }

    private final Type res;
    private final String name;
    private final Arg[] args;
    private final boolean va;
    private final CompoundStmt body;

    private DefFunctionStmt(
            final SourceLocation sl,
            final Type res,
            final String name,
            final Arg[] args,
            final boolean va,
            final CompoundStmt body) {
        super(sl);
        this.res = res;
        this.name = name;
        this.args = args;
        this.va = va;
        this.body = body;
    }

    public Type getRes() {
        return res;
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
        return va;
    }

    public CompoundStmt getBody() {
        return body;
    }

    public FunctionType getFunctionType() {
        return FunctionType.get(res, va, Arrays.stream(args).map(Arg::ty).toArray(Type[]::new));
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }

        if (va) {
            if (args.length > 0)
                builder.append(", ");
            builder.append('?');
        }

        if (body == null)
            return "def %s %s(%s)".formatted(res, name, builder);
        return "def %s %s(%s) %s".formatted(res, name, builder, body);
    }
}

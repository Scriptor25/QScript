package io.scriptor.type;

import java.util.Arrays;
import java.util.stream.Stream;

public class FunctionType extends Type {

    private static String makeId(final Type result, final boolean vararg, final Type... args) {
        final var builder = new StringBuilder()
                .append(result.getId())
                .append('(');

        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i].getId());
        }

        if (vararg) {
            if (args.length != 0)
                builder.append(", ");
            builder.append('?');
        }

        return builder
                .append(')')
                .toString();
    }

    public static FunctionType get(final Type result, final boolean vararg, final Type... args) {
        final var id = makeId(result, vararg, args);
        if (Type.exists(id))
            return Type.get(id);

        return new FunctionType(id, result, vararg, args);
    }

    private final Type result;
    private final boolean vararg;
    private final Type[] args;

    protected FunctionType(
            final String id,
            final Type result,
            final boolean vararg,
            final Type... args) {
        super(id, Type.IS_FUNCTION, 64);
        this.result = result;
        this.vararg = vararg;
        this.args = args;
    }

    public Type getResult() {
        return result;
    }

    public boolean isVarArg() {
        return vararg;
    }

    public int getArgCount() {
        return args.length;
    }

    public Type getArg(final int i) {
        if (vararg && i >= args.length)
            return null;

        return args[i];
    }

    public Stream<Type> getArgs() {
        return Arrays.stream(args);
    }
}

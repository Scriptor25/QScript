package io.scriptor.type;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
import io.scriptor.parser.SourceLocation;

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
        final var type = Type.getUnsafe(id);
        if (type != null)
            return (FunctionType) type;
        return Type.create(id, new FunctionType(id, result, vararg, args));
    }

    private final Type result;
    private final boolean vararg;
    private final Type[] args;

    protected FunctionType(final String id, final Type result, final boolean vararg, final Type... args) {
        super(id, Type.IS_FUNCTION, 64);
        this.result = result;
        this.vararg = vararg;
        this.args = args;
    }

    public Type getResult() {
        return result;
    }

    public boolean hasVararg() {
        return vararg;
    }

    public int getArgCount() {
        return args.length;
    }

    public Type getArg(final SourceLocation location, final int i) {
        if (vararg && i >= args.length)
            return null;

        rtassert(i >= 0 && i < args.length,
                () -> new QScriptException(location, "index %d out of bounds [0;%d[", i, args.length));

        return args[i];
    }
}

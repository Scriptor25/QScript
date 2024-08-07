package io.scriptor.frontend.expression;

import java.util.Arrays;

import io.scriptor.backend.Block;
import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.Arg;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class DefFunExpression extends Expression {

    public static DefFunExpression create(
            final SourceLocation location,
            final Type result,
            final String name,
            final Arg[] args,
            final boolean vararg) {
        return create(location, result, name, args, vararg, null);
    }

    public static DefFunExpression create(
            final SourceLocation location,
            final Type result,
            final String name,
            final Arg[] args,
            final boolean vararg,
            final CompoundExpression body) {
        return new DefFunExpression(location, result, name, args, vararg, body);
    }

    private final Type result;
    private final String name;
    private final Arg[] args;
    private final boolean vararg;
    private final CompoundExpression body;

    private DefFunExpression(
            final SourceLocation location,
            final Type result,
            final String name,
            final Arg[] args,
            final boolean vararg,
            final CompoundExpression body) {
        super(location, null);
        this.result = result;
        this.name = name;
        this.args = args;
        this.vararg = vararg;
        this.body = body;
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

    @Override
    public Value genIR(final IRBuilder builder, final IRModule module) {
        final var name = builder.isGlobal()
                ? this.name
                : builder.getInsertFunction().getName() + "$" + this.name;

        final var type = FunctionType.get(result, vararg, Arrays.stream(args).map(Arg::type).toArray(Type[]::new));
        final var function = module.getFunction(type, name);

        if (body == null)
            return null;

        if (!function.isEmpty())
            throw new QScriptException(getLocation(), "function cannot be redefined");

        final var bkp = builder.getInsertPoint();
        final var entry = new Block(function, "entry");
        builder.setInsertPoint(entry);

        builder.clearStack();
        for (int i = 0; i < args.length; ++i) {
            final var arg = function.getArg(i);
            arg.setName(args[i].name());

            final var ptr = builder.createAlloca(args[i].type());
            builder.createStore(ptr, arg);

            builder.setValue(arg.getName(), ptr);
        }

        body.genIR(builder, module);

        builder.setInsertPoint(bkp);
        return null;
    }
}

package io.scriptor.frontend.expression;

import java.util.Arrays;

import io.scriptor.backend.Block;
import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.ref.LValueRef;
import io.scriptor.backend.ref.ValueRef;
import io.scriptor.frontend.Arg;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

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

    @Override
    public ValueRef genIR(final IRBuilder builder, final IRModule module) {
        final var name = builder.isGlobal()
                ? this.name
                : builder.getInsertFunction().getName() + "$" + this.name;

        final var type = FunctionType.get(result, vararg, Arrays.stream(args).map(Arg::type).toArray(Type[]::new));
        final var function = module.getFunction(type, name);

        builder.getOrCreateRef(this.name, () -> LValueRef.create(builder, function));

        if (body == null)
            return null;

        if (!function.isEmpty())
            throw new QScriptException(getLocation(), "function cannot be redefined");

        final var bkp = builder.getInsertPoint();
        final var entry = new Block(function, "entry");
        builder.setInsertPoint(entry);
        builder.push();

        for (int i = 0; i < args.length; ++i) {
            final var arg = function.getArg(i);
            arg.setName(args[i].name());

            builder.putRef(arg.getName(), LValueRef.alloca(builder, arg.getType(), arg));
        }

        body.genIR(builder, module);

        builder.pop();
        builder.setInsertPoint(bkp);
        return null;
    }
}

package io.scriptor.frontend.expression;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.ref.RValueRef;
import io.scriptor.backend.ref.ValueRef;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class CallExpr extends Expression {

    public static CallExpr create(
            final SourceLocation location,
            final Type result,
            final Expression callee,
            final Expression[] args) {
        return new CallExpr(location, result, callee, args);
    }

    private final Expression callee;
    private final Expression[] args;

    private CallExpr(
            final SourceLocation location,
            final Type result,
            final Expression callee,
            final Expression[] args) {
        super(location, result);
        this.callee = callee;
        this.args = args;
    }

    public Expression getCallee() {
        return callee;
    }

    public int getArgCount() {
        return args.length;
    }

    public Expression getArg(final int index) {
        return args[index];
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }
        return "%s(%s)".formatted(callee, builder);
    }

    @Override
    public ValueRef genIR(final IRBuilder builder, final IRModule module) {
        final var callee = this.callee.genIR(builder, module).get();
        final var args = new Value[this.args.length];
        for (int i = 0; i < args.length; ++i)
            args[i] = this.args[i].genIR(builder, module).get();
        return RValueRef.create(builder.createCall(callee, args));
    }
}

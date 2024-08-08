package io.scriptor.frontend.expression;

import io.scriptor.backend.Block;
import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.ref.LValueRef;
import io.scriptor.backend.ref.ValueRef;
import io.scriptor.backend.value.ConstValue;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class DefVarExpr extends Expression {

    public static DefVarExpr create(
            final SourceLocation location,
            final Type type,
            final String name) {
        return new DefVarExpr(location, type, name, null);
    }

    public static DefVarExpr create(
            final SourceLocation location,
            final Type type,
            final String name,
            final Expression init) {
        return new DefVarExpr(location, type, name, init);
    }

    private final Type type;
    private final String name;
    private final Expression init;

    private DefVarExpr(
            final SourceLocation location,
            final Type type,
            final String name,
            final Expression init) {
        super(location, null);
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Expression getInit() {
        return init;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        if (init == null)
            return "def %s %s".formatted(type, name);
        return "def %s %s = %s".formatted(type, name, init);
    }

    @Override
    public ValueRef genIR(final IRBuilder builder, final IRModule module) {
        if (builder.isGlobal()) {
            final ConstValue constInit;
            if (init != null && init.isConst() && init.getType() == type) {
                constInit = (ConstValue) init.genIR(builder, module).get();
            } else {
                constInit = null;
            }

            final var ptr = module.createGlobal(type, constInit, name);
            final var ref = LValueRef.create(builder, ptr);

            if (constInit == null && init != null) {
                final var block = new Block();
                builder.setInsertPoint(block);
                final var init = this.init.genIR(builder, module);
                ref.set(builder.createCast(init.get(), type));
                builder.resetInsertPoint();
                module.createCtor(block);
            }

            builder.putRef(name, ref);
            return null;
        }

        final ValueRef ref;
        if (init == null) {
            ref = LValueRef.alloca(builder, type);
        } else {
            final var init = this.init.genIR(builder, module);
            ref = LValueRef.alloca(builder, type, builder.createCast(init.get(), type));
        }

        builder.putRef(name, ref);
        return null;
    }
}

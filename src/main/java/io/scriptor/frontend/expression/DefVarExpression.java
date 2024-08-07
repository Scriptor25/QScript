package io.scriptor.frontend.expression;

import io.scriptor.backend.Block;
import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRModule;
import io.scriptor.backend.value.ConstValue;
import io.scriptor.backend.value.Value;
import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class DefVarExpression extends Expression {

    public static DefVarExpression create(
            final SourceLocation location,
            final Type type,
            final String name) {
        return new DefVarExpression(location, type, name, null);
    }

    public static DefVarExpression create(
            final SourceLocation location,
            final Type type,
            final String name,
            final Expression init) {
        return new DefVarExpression(location, type, name, init);
    }

    private final Type type;
    private final String name;
    private final Expression init;

    private DefVarExpression(
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
    public String toString() {
        if (init == null)
            return "def %s %s".formatted(type, name);
        return "def %s %s = %s".formatted(type, name, init);
    }

    @Override
    public Value genIR(final IRBuilder builder, final IRModule module) {
        if (builder.isGlobal()) {
            final ConstValue constInit;
            if (init != null && init.isConst()) {
                constInit = (ConstValue) init.genIR(builder, module);
            } else {
                constInit = null;
            }
            final var ptr = module.createGlobal(type, constInit, name);
            if (init != null && constInit == null) {
                final var block = new Block();
                builder.setInsertPoint(block);
                builder.createStore(ptr, init.genIR(builder, module));
                builder.resetInsertPoint();
                module.createCtor(block);
            }
            return null;
        }

        final var ptr = builder.createAlloca(type);
        if (init != null) {
            builder.createStore(ptr, init.genIR(builder, module));
        }
        return null;
    }
}

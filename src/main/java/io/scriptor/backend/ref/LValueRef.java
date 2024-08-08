package io.scriptor.backend.ref;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.value.Value;
import io.scriptor.type.PointerType;
import io.scriptor.type.Type;

public class LValueRef extends ValueRef {

    public static LValueRef create(final IRBuilder builder, final Value ptr) {
        assert builder != null;
        assert ptr != null;
        return new LValueRef(builder, ptr);
    }

    public static LValueRef alloca(final IRBuilder builder, final Type base) {
        assert builder != null;
        assert base != null;
        final var ptr = builder.createAlloca(base, 1);
        return new LValueRef(builder, ptr);
    }

    public static LValueRef alloca(final IRBuilder builder, final Type base, final Value value) {
        assert builder != null;
        assert base != null;
        assert value != null;
        assert value.getType() == base;
        final var ptr = builder.createAlloca(value.getType(), 1);
        builder.createStore(ptr, value);
        return new LValueRef(builder, ptr);
    }

    public static LValueRef copy(final IRBuilder builder, final Type base, final ValueRef ref) {
        assert builder != null;
        assert base != null;
        assert ref != null;
        assert ref.getType() == base;
        final var ptr = builder.createAlloca(ref.getType(), 1);
        builder.createStore(ptr, ref.get());
        return new LValueRef(builder, ptr);
    }

    private final IRBuilder builder;
    private final Value ptr;

    private LValueRef(final IRBuilder builder, final Value ptr) {
        this.builder = builder;
        this.ptr = ptr;
    }

    @Override
    public Type getType() {
        return ((PointerType) ptr.getType()).getBase();
    }

    @Override
    public Value get() {
        return builder.createLoad(ptr);
    }

    public PointerType getPtrType() {
        return (PointerType) ptr.getType();
    }

    public Value getPtr() {
        return ptr;
    }

    public void set(final Value value) {
        builder.createStore(ptr, value);
    }

    public void copy(final ValueRef ref) {
        set(ref.get());
    }
}

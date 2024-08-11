package io.scriptor.backend;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class LValue extends Value {

    public static LValue alloca(final SourceLocation sl, final Builder builder, final Type type) {
        final var ptr = builder.createAlloca(builder.genIR(sl, type));
        return new LValue(sl, builder, type, ptr);
    }

    public static LValue alloca(
            final SourceLocation sl,
            final Builder builder,
            final Type type,
            final LLVMValueRef value) {
        final var ref = alloca(sl, builder, type);
        ref.setValue(value);
        return ref;
    }

    public static LValue copy(final SourceLocation sl, final Builder builder, final Value value) {
        return alloca(sl, builder, value.getType(), value.get());
    }

    public static LValue direct(
            final SourceLocation sl,
            final Builder builder,
            final Type type,
            final LLVMValueRef ptr) {
        return new LValue(sl, builder, type, ptr);
    }

    private final LLVMValueRef ptr;

    protected LValue(final SourceLocation sl, final Builder builder, final Type type, final LLVMValueRef ptr) {
        super(sl, builder, type);
        this.ptr = ptr;
    }

    @Override
    public LLVMValueRef get() {
        return getBuilder().createLoad(getLLVMType(), ptr);
    }

    public LLVMValueRef setValue(final LLVMValueRef value) {
        return getBuilder().createStore(value, ptr);
    }

    public LLVMValueRef getPtr() {
        return ptr;
    }
}

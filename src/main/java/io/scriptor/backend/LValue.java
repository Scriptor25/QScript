package io.scriptor.backend;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.type.Type;

public class LValue extends Value {

    public static LValue alloca(final Builder builder, final Type type) {
        final var ptr = builder.createAlloca(builder.genIR(type));
        return new LValue(builder, type, ptr);
    }

    public static LValue alloca(final Builder builder, final Type type, final LLVMValueRef value) {
        final var ref = LValue.alloca(builder, type);
        ref.setValue(value);
        return ref;
    }

    public static LValue copy(final Builder builder, final Value value) {
        return alloca(builder, value.getType(), value.get());
    }

    public static LValue direct(final Builder builder, final Type type, final LLVMValueRef ptr) {
        return new LValue(builder, type, ptr);
    }

    private final LLVMValueRef ptr;

    protected LValue(final Builder builder, final Type type, final LLVMValueRef ptr) {
        super(builder, type);
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

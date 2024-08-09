package io.scriptor.backend;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.type.Type;

public class RValue extends Value {

    public static RValue create(final Builder builder, final Type type, final LLVMValueRef value) {
        return new RValue(builder, type, value);
    }

    private final LLVMValueRef value;

    protected RValue(final Builder builder, final Type type, final LLVMValueRef value) {
        super(builder, type);
        this.value = value;
    }

    @Override
    public LLVMValueRef getValue() {
        return value;
    }
}

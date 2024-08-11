package io.scriptor.backend;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class RValue extends Value {

    public static RValue create(
            final SourceLocation sl,
            final Builder builder,
            final Type type,
            final LLVMValueRef value) {
        return new RValue(sl, builder, type, value);
    }

    private final LLVMValueRef value;

    protected RValue(final SourceLocation sl, final Builder builder, final Type type, final LLVMValueRef value) {
        super(sl, builder, type);
        this.value = value;
    }

    @Override
    public LLVMValueRef get() {
        return value;
    }
}

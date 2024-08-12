package io.scriptor.backend;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class RValue extends Value {

    public static RValue createR(final Builder b, final SourceLocation sl, final Type ty, final LLVMValueRef val) {
        return new RValue(b, sl, ty, val);
    }

    private final LLVMValueRef value;

    protected RValue(final Builder b, final SourceLocation sl, final Type ty, final LLVMValueRef val) {
        super(b, sl, ty);
        this.value = val;
    }

    @Override
    public LLVMValueRef get() {
        return value;
    }
}

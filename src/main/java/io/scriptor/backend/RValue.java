package io.scriptor.backend;

import java.util.Optional;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class RValue extends Value {

    public static Optional<Value> createOptR(
            final Builder b,
            final SourceLocation sl,
            final Type ty,
            final LLVMValueRef v) {
        return Optional.of(createR(b, sl, ty, v));
    }

    public static RValue createR(final Builder b, final SourceLocation sl, final Type ty, final LLVMValueRef v) {
        return new RValue(b, sl, ty, v);
    }

    private final LLVMValueRef v;

    protected RValue(final Builder b, final SourceLocation sl, final Type ty, final LLVMValueRef v) {
        super(b, sl, ty);
        this.v = v;
    }

    @Override
    public LLVMValueRef get() {
        return v;
    }
}

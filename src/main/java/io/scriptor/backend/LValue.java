package io.scriptor.backend;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

import static io.scriptor.backend.GenType.*;

import java.util.Optional;

public class LValue extends Value {

    public static LValue allocaL(final Builder b, final SourceLocation sl, final Type ty) {
        final var ptr = b.genAlloca(genType(sl, ty).get());
        return new LValue(b, sl, ty, ptr);
    }

    public static LValue allocaL(final Builder b, final SourceLocation sl, final Type ty, final LLVMValueRef val) {
        final var ref = allocaL(b, sl, ty);
        ref.setValue(val);
        return ref;
    }

    public static LValue copyL(final Builder b, final SourceLocation sl, final Value val) {
        return allocaL(b, sl, val.getType(), val.get());
    }

    public static Optional<Value> directOptL(
            final Builder b,
            final SourceLocation sl,
            final Type ty,
            final LLVMValueRef ptr) {
        return Optional.of(directL(b, sl, ty, ptr));
    }

    public static LValue directL(final Builder b, final SourceLocation sl, final Type ty, final LLVMValueRef ptr) {
        return new LValue(b, sl, ty, ptr);
    }

    private final LLVMValueRef ptr;

    protected LValue(final Builder b, final SourceLocation sl, final Type ty, final LLVMValueRef ptr) {
        super(b, sl, ty);
        this.ptr = ptr;
    }

    @Override
    public LLVMValueRef get() {
        return getBuilder().genLoad(getLLVMType(), ptr);
    }

    public LLVMValueRef setValue(final LLVMValueRef value) {
        return getBuilder().genStore(value, ptr);
    }

    public LLVMValueRef getPtr() {
        return ptr;
    }
}

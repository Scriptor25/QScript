package io.scriptor.backend;

import static io.scriptor.backend.GenType.genType;

import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public abstract class Value {

    private final Builder builder;
    private final Type type;
    private final LLVMTypeRef llvmType;

    protected Value(final Builder b, final SourceLocation sl, final Type ty) {
        this.builder = b;
        this.type = ty;
        this.llvmType = genType(sl, ty).get();
    }

    public Builder getBuilder() {
        return builder;
    }

    public Type getType() {
        return type;
    }

    public LLVMTypeRef getLLVMType() {
        return llvmType;
    }

    public abstract LLVMValueRef get();
}

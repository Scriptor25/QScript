package io.scriptor.backend;

import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public abstract class Value {

    private final Builder builder;
    private final Type type;
    private final LLVMTypeRef llvmType;

    protected Value(final SourceLocation sl, final Builder builder, final Type type) {
        this.builder = builder;
        this.type = type;
        this.llvmType = builder.genIR(sl, type);
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

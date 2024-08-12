package io.scriptor.backend;

import static io.scriptor.backend.GenType.genType;
import static io.scriptor.backend.RValue.createR;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFPCast;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFPToSI;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildIntCast2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildIntToPtr;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildPointerCast;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildPtrToInt;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildSIToFP;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class GenCast {

    private static void assertCast(final Value v, final Type t) {
        assert v != null;
        assert t != null;
    }

    public static Value genCast(final Builder b, final SourceLocation sl, final Value v, final Type ty) {
        assertCast(v, ty);

        final var vtype = v.getType();
        if (vtype == ty || ty == null)
            return v;

        final var llvmvalue = v.get();
        final var llvmtype = genType(sl, ty);

        if (vtype.isInt()) {
            if (ty.isInt()) {
                final var result = LLVMBuildIntCast2(b.getBuilder(), llvmvalue, llvmtype, 1, "");
                return createR(b, sl, ty, result);
            }

            if (ty.isFlt()) {
                final var result = LLVMBuildSIToFP(b.getBuilder(), llvmvalue, llvmtype, "");
                return createR(b, sl, ty, result);
            }

            if (ty.isPointer()) {
                final var result = LLVMBuildIntToPtr(b.getBuilder(), llvmvalue, llvmtype, "");
                return createR(b, sl, ty, result);
            }
        }

        if (vtype.isFlt()) {
            if (ty.isInt()) {
                final var result = LLVMBuildFPToSI(b.getBuilder(), llvmvalue, llvmtype, "");
                return createR(b, sl, ty, result);
            }

            if (ty.isFlt()) {
                final var result = LLVMBuildFPCast(b.getBuilder(), llvmvalue, llvmtype, "");
                return createR(b, sl, ty, result);
            }
        }

        if (vtype.isPointer()) {
            if (ty.isInt()) {
                final var result = LLVMBuildPtrToInt(b.getBuilder(), llvmvalue, llvmtype, "");
                return createR(b, sl, ty, result);
            }

            if (ty.isPointer()) {
                final var result = LLVMBuildPointerCast(b.getBuilder(), llvmvalue, llvmtype, "");
                return createR(b, sl, ty, result);
            }
        }

        throw new QScriptException(sl, "cannot cast from '%s' to '%s'", vtype, ty);
    }

    private GenCast() {
    }
}

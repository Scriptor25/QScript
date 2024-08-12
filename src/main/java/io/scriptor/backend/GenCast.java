package io.scriptor.backend;

import static io.scriptor.backend.GenType.genType;
import static io.scriptor.backend.RValue.createOptR;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFPCast;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFPToSI;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildIntCast2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildIntToPtr;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildPointerCast;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildPtrToInt;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildSIToFP;

import java.util.Optional;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptError;

public class GenCast {

    private static void assertCast(final Value v, final Type t) {
        assert v != null;
        assert t != null;
    }

    public static Optional<Value> genCast(final Builder b, final SourceLocation sl, final Value v, final Type ty) {
        assertCast(v, ty);

        final var vty = v.getType();
        if (vty == ty)
            return Optional.of(v);

        final var val = v.get();
        final var type = genType(sl, ty).get();

        if (vty.isInt()) {
            if (ty.isInt()) {
                final var result = LLVMBuildIntCast2(b.getBuilder(), val, type, 1, "");
                return createOptR(b, sl, ty, result);
            }

            if (ty.isFlt()) {
                final var result = LLVMBuildSIToFP(b.getBuilder(), val, type, "");
                return createOptR(b, sl, ty, result);
            }

            if (ty.isPointer()) {
                final var result = LLVMBuildIntToPtr(b.getBuilder(), val, type, "");
                return createOptR(b, sl, ty, result);
            }
        }

        if (vty.isFlt()) {
            if (ty.isInt()) {
                final var result = LLVMBuildFPToSI(b.getBuilder(), val, type, "");
                return createOptR(b, sl, ty, result);
            }

            if (ty.isFlt()) {
                final var result = LLVMBuildFPCast(b.getBuilder(), val, type, "");
                return createOptR(b, sl, ty, result);
            }
        }

        if (vty.isPointer()) {
            if (ty.isInt()) {
                final var result = LLVMBuildPtrToInt(b.getBuilder(), val, type, "");
                return createOptR(b, sl, ty, result);
            }

            if (ty.isPointer()) {
                final var result = LLVMBuildPointerCast(b.getBuilder(), val, type, "");
                return createOptR(b, sl, ty, result);
            }
        }

        QScriptError.print(sl, "cannot cast from '%s' to '%s'", vty, ty);
        return Optional.empty();
    }

    private GenCast() {
    }
}

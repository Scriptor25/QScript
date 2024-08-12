package io.scriptor.backend;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.bytedeco.llvm.global.LLVM.LLVMArrayType2;
import static org.bytedeco.llvm.global.LLVM.LLVMDoubleTypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMFloatTypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMFunctionType;
import static org.bytedeco.llvm.global.LLVM.LLVMInt16TypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMInt1TypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMInt32TypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMInt64TypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMInt8TypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMPointerType;
import static org.bytedeco.llvm.global.LLVM.LLVMStructTypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMVoidTypeInContext;

import java.util.Optional;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptError;

public class GenType {

    public static Optional<LLVMTypeRef> genType(final SourceLocation sl, final Type type) {
        if (type.isFunction()) {
            final var ty = type.asFunction().get();
            final var rty = genType(sl, ty.getResult()).get();
            final var pty = new PointerPointer<LLVMTypeRef>(ty.getArgCount());
            for (int i = 0; i < ty.getArgCount(); ++i) {
                pty.put(i, genType(sl, ty.getArg(i).get()).get());
            }
            return of(LLVMFunctionType(rty, pty, ty.getArgCount(), ty.isVarArg() ? 1 : 0));
        }

        if (type.isStruct()) {
            final var ty = type.asStruct().get();
            final var ety = new PointerPointer<LLVMTypeRef>(ty.getElementCount());
            for (int i = 0; i < ty.getElementCount(); ++i)
                ety.put(i, genType(sl, ty.getElement(i)).get());
            return of(LLVMStructTypeInContext(Builder.getContext(), ety, ty.getElementCount(), 0));
        }

        if (type.isPointer())
            return of(LLVMPointerType(genType(sl, type.getPointerBase().get()).get(), 0));

        if (type.isArray())
            return of(LLVMArrayType2(genType(sl, type.getArrayBase().get()).get(), type.getArrayLength()));

        if (type.isVoid())
            return of(LLVMVoidTypeInContext(Builder.getContext()));

        if (type.isInt1())
            return of(LLVMInt1TypeInContext(Builder.getContext()));
        if (type.isInt8())
            return of(LLVMInt8TypeInContext(Builder.getContext()));
        if (type.isInt16())
            return of(LLVMInt16TypeInContext(Builder.getContext()));
        if (type.isInt32())
            return of(LLVMInt32TypeInContext(Builder.getContext()));
        if (type.isInt64())
            return of(LLVMInt64TypeInContext(Builder.getContext()));

        if (type.isFlt32())
            return of(LLVMFloatTypeInContext(Builder.getContext()));
        if (type.isFlt64())
            return of(LLVMDoubleTypeInContext(Builder.getContext()));

        QScriptError.print(sl, "no genIR for class '%s': %s", type.getClass(), type);
        return empty();
    }

    private GenType() {
    }
}

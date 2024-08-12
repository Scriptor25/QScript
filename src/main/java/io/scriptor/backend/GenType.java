package io.scriptor.backend;

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

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class GenType {

    public static LLVMTypeRef genType(final SourceLocation sl, final Type type) {
        if (type == null)
            throw new QScriptException(sl, "type must not be null");

        if (type.isFunction()) {
            final var t = type.asFunction();
            final var rt = genType(sl, t.getResult());
            final var pt = new PointerPointer<LLVMTypeRef>(t.getArgCount());
            for (int i = 0; i < t.getArgCount(); ++i)
                pt.put(i, genType(sl, t.getArg(i)));
            return LLVMFunctionType(rt, pt, t.getArgCount(), t.isVarArg() ? 1 : 0);
        }

        if (type.isStruct()) {
            final var t = type.asStruct();
            final var et = new PointerPointer<LLVMTypeRef>(t.getElementCount());
            for (int i = 0; i < t.getElementCount(); ++i)
                et.put(i, genType(sl, t.getElement(i)));
            return LLVMStructTypeInContext(Builder.getContext(), et, t.getElementCount(), 0);
        }

        if (type.isPointer())
            return LLVMPointerType(genType(sl, type.asPointer().getBase()), 0);

        if (type.isArray())
            return LLVMArrayType2(genType(sl, type.asArray().getBase()), type.asArray().getLength());

        if (type.isVoid())
            return LLVMVoidTypeInContext(Builder.getContext());

        if (type.isInt1())
            return LLVMInt1TypeInContext(Builder.getContext());
        if (type.isInt8())
            return LLVMInt8TypeInContext(Builder.getContext());
        if (type.isInt16())
            return LLVMInt16TypeInContext(Builder.getContext());
        if (type.isInt32())
            return LLVMInt32TypeInContext(Builder.getContext());
        if (type.isInt64())
            return LLVMInt64TypeInContext(Builder.getContext());

        if (type.isFlt32())
            return LLVMFloatTypeInContext(Builder.getContext());
        if (type.isFlt64())
            return LLVMDoubleTypeInContext(Builder.getContext());

        throw new QScriptException(sl, "no genIR for class '%s': %s", type.getClass(), type);
    }

    private GenType() {
    }
}

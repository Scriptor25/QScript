package io.scriptor.backend;

import static io.scriptor.backend.RValue.createR;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildAdd;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildAnd;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFAdd;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFCmp;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFDiv;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFMul;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFSub;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildICmp;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildIsNotNull;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildMul;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildNot;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildSDiv;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildSub;
import static org.bytedeco.llvm.global.LLVM.LLVMIntEQ;
import static org.bytedeco.llvm.global.LLVM.LLVMIntNE;
import static org.bytedeco.llvm.global.LLVM.LLVMIntSLT;
import static org.bytedeco.llvm.global.LLVM.LLVMRealOEQ;
import static org.bytedeco.llvm.global.LLVM.LLVMRealOLT;
import static org.bytedeco.llvm.global.LLVM.LLVMRealONE;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.PointerType;
import io.scriptor.type.Type;

public class GenOperation {

    private static void assertBinary(final Value l, final Value r) {
        assert l != null;
        assert r != null;
        assert l.getType() == r.getType();
    }

    private static void assertUnary(final Value v) {
        assert v != null;
    }

    public static Value genEQ(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildICmp(b.getBuilder(), LLVMIntEQ, left.get(), right.get(), "");
            return createR(b, sl, Type.getInt1(b.getState()), result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFCmp(b.getBuilder(), LLVMRealOEQ, left.get(), right.get(), "");
            return createR(b, sl, Type.getInt1(b.getState()), result);
        }

        return null;
    }

    public static Value genNE(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildICmp(b.getBuilder(), LLVMIntNE, left.get(), right.get(), "");
            return createR(b, sl, Type.getInt1(b.getState()), result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFCmp(b.getBuilder(), LLVMRealONE, left.get(), right.get(), "");
            return createR(b, sl, Type.getInt1(b.getState()), result);
        }

        return null;
    }

    public static Value genLT(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildICmp(b.getBuilder(), LLVMIntSLT, left.get(), right.get(), "");
            return createR(b, sl, Type.getInt1(b.getState()), result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFCmp(b.getBuilder(), LLVMRealOLT, left.get(), right.get(), "");
            return createR(b, sl, Type.getInt1(b.getState()), result);
        }

        return null;
    }

    public static Value genGT(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        return null;
    }

    public static Value genLE(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        return null;
    }

    public static Value genGE(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        return null;
    }

    public static Value genLAnd(final Builder b, final SourceLocation sl, final Value left,
            final Value right) {
        assertBinary(left, right);

        final var lb = LLVMBuildIsNotNull(b.getBuilder(), left.get(), "");
        final var rb = LLVMBuildIsNotNull(b.getBuilder(), right.get(), "");

        final var result = LLVMBuildAnd(b.getBuilder(), lb, rb, "");
        return createR(b, sl, Type.getInt1(b.getState()), result);
    }

    public static Value genLOr(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        return null;
    }

    public static Value genLXor(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        return null;
    }

    public static Value genAdd(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildAdd(b.getBuilder(), left.get(), right.get(), "");
            return createR(b, sl, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFAdd(b.getBuilder(), left.get(), right.get(), "");
            return createR(b, sl, type, result);
        }

        return null;
    }

    public static Value genSub(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildSub(b.getBuilder(), left.get(), right.get(), "");
            return createR(b, sl, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFSub(b.getBuilder(), left.get(), right.get(), "");
            return createR(b, sl, type, result);
        }

        return null;
    }

    public static Value genMul(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildMul(b.getBuilder(), left.get(), right.get(), "");
            return createR(b, sl, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFMul(b.getBuilder(), left.get(), right.get(), "");
            return createR(b, sl, type, result);
        }

        return null;
    }

    public static Value genDiv(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildSDiv(b.getBuilder(), left.get(), right.get(), "");
            return createR(b, sl, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFDiv(b.getBuilder(), left.get(), right.get(), "");
            return createR(b, sl, type, result);
        }

        return null;
    }

    public static Value genRem(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        return null;
    }

    public static Value genAnd(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        return null;
    }

    public static Value genOr(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        return null;
    }

    public static Value genXor(final Builder b, final SourceLocation sl, final Value left, final Value right) {
        assertBinary(left, right);
        return null;
    }

    public static Value genNot(final Builder b, final SourceLocation sl, final Value value) {
        assertUnary(value);

        final var val = value.get();
        final var boolval = LLVMBuildIsNotNull(b.getBuilder(), val, "");
        final var result = LLVMBuildNot(b.getBuilder(), boolval, "");

        return createR(b, sl, Type.getInt1(b.getState()), result);
    }

    public static Value genNeg(final Builder b, final SourceLocation sl, final Value value) {
        assertUnary(value);
        return null;
    }

    public static Value genLNeg(final Builder b, final SourceLocation sl, final Value value) {
        assertUnary(value);
        return null;
    }

    public static Value genRef(final Builder b, final SourceLocation sl, final Value value) {
        assertUnary(value);

        if (value instanceof LValue l) {
            return createR(b, sl, PointerType.get(l.getType()), l.getPtr());
        }

        return null;
    }

    private GenOperation() {
    }
}

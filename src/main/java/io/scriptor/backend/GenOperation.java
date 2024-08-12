package io.scriptor.backend;

import static io.scriptor.backend.LValue.directOptL;
import static io.scriptor.backend.RValue.createOptR;
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

import java.util.Optional;

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

    public static Optional<Value> genEQ(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        final var type = l.getType();

        if (type.isInt()) {
            final var result = LLVMBuildICmp(b.getBuilder(), LLVMIntEQ, l.get(), r.get(), "");
            return createOptR(b, sl, Type.getInt1(b.getFrame()), result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFCmp(b.getBuilder(), LLVMRealOEQ, l.get(), r.get(), "");
            return createOptR(b, sl, Type.getInt1(b.getFrame()), result);
        }

        return Optional.empty();
    }

    public static Optional<Value> genNE(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        final var type = l.getType();

        if (type.isInt()) {
            final var result = LLVMBuildICmp(b.getBuilder(), LLVMIntNE, l.get(), r.get(), "");
            return createOptR(b, sl, Type.getInt1(b.getFrame()), result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFCmp(b.getBuilder(), LLVMRealONE, l.get(), r.get(), "");
            return createOptR(b, sl, Type.getInt1(b.getFrame()), result);
        }

        return Optional.empty();
    }

    public static Optional<Value> genLT(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        final var type = l.getType();

        if (type.isInt()) {
            final var result = LLVMBuildICmp(b.getBuilder(), LLVMIntSLT, l.get(), r.get(), "");
            return createOptR(b, sl, Type.getInt1(b.getFrame()), result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFCmp(b.getBuilder(), LLVMRealOLT, l.get(), r.get(), "");
            return createOptR(b, sl, Type.getInt1(b.getFrame()), result);
        }

        return Optional.empty();
    }

    public static Optional<Value> genGT(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        return Optional.empty();
    }

    public static Optional<Value> genLE(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        return Optional.empty();
    }

    public static Optional<Value> genGE(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        return Optional.empty();
    }

    public static Optional<Value> genLAnd(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);

        final var lb = LLVMBuildIsNotNull(b.getBuilder(), l.get(), "");
        final var rb = LLVMBuildIsNotNull(b.getBuilder(), r.get(), "");

        final var result = LLVMBuildAnd(b.getBuilder(), lb, rb, "");
        return createOptR(b, sl, Type.getInt1(b.getFrame()), result);
    }

    public static Optional<Value> genLOr(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        return Optional.empty();
    }

    public static Optional<Value> genLXor(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        return Optional.empty();
    }

    public static Optional<Value> genAdd(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        final var type = l.getType();

        if (type.isInt()) {
            final var result = LLVMBuildAdd(b.getBuilder(), l.get(), r.get(), "");
            return createOptR(b, sl, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFAdd(b.getBuilder(), l.get(), r.get(), "");
            return createOptR(b, sl, type, result);
        }

        return Optional.empty();
    }

    public static Optional<Value> genSub(final Builder b, final SourceLocation sl, final Value l,
            final Value r) {
        assertBinary(l, r);
        final var type = l.getType();

        if (type.isInt()) {
            final var result = LLVMBuildSub(b.getBuilder(), l.get(), r.get(), "");
            return createOptR(b, sl, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFSub(b.getBuilder(), l.get(), r.get(), "");
            return createOptR(b, sl, type, result);
        }

        return Optional.empty();
    }

    public static Optional<Value> genMul(final Builder b, final SourceLocation sl, final Value l,
            final Value r) {
        assertBinary(l, r);
        final var type = l.getType();

        if (type.isInt()) {
            final var result = LLVMBuildMul(b.getBuilder(), l.get(), r.get(), "");
            return createOptR(b, sl, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFMul(b.getBuilder(), l.get(), r.get(), "");
            return createOptR(b, sl, type, result);
        }

        return Optional.empty();
    }

    public static Optional<Value> genDiv(final Builder b, final SourceLocation sl, final Value l,
            final Value r) {
        assertBinary(l, r);
        final var type = l.getType();

        if (type.isInt()) {
            final var result = LLVMBuildSDiv(b.getBuilder(), l.get(), r.get(), "");
            return createOptR(b, sl, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFDiv(b.getBuilder(), l.get(), r.get(), "");
            return createOptR(b, sl, type, result);
        }

        return Optional.empty();
    }

    public static Optional<Value> genRem(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        return Optional.empty();
    }

    public static Optional<Value> genAnd(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        return Optional.empty();
    }

    public static Optional<Value> genOr(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        return Optional.empty();
    }

    public static Optional<Value> genXor(final Builder b, final SourceLocation sl, final Value l, final Value r) {
        assertBinary(l, r);
        return Optional.empty();
    }

    public static Optional<Value> genNot(final Builder b, final SourceLocation sl, final Value v) {
        assertUnary(v);

        final var val = v.get();
        final var boolval = LLVMBuildIsNotNull(b.getBuilder(), val, "");
        final var result = LLVMBuildNot(b.getBuilder(), boolval, "");

        return createOptR(b, sl, Type.getInt1(b.getFrame()), result);
    }

    public static Optional<Value> genNeg(final Builder b, final SourceLocation sl, final Value v) {
        assertUnary(v);
        return Optional.empty();
    }

    public static Optional<Value> genLNeg(final Builder b, final SourceLocation sl, final Value v) {
        assertUnary(v);
        return Optional.empty();
    }

    public static Optional<Value> genRef(final Builder b, final SourceLocation sl, final Value v) {
        assertUnary(v);

        if (v instanceof LValue l) {
            return createOptR(b, sl, PointerType.get(l.getType()), l.getPtr());
        }

        return Optional.empty();
    }

    public static Optional<Value> genDeref(final Builder b, final SourceLocation sl, final Value v) {
        assertUnary(v);

        final var optty = v.getType().getPointerBase();
        if (optty.isEmpty())
            return Optional.empty();

        return directOptL(b, sl, optty.get(), v.get());
    }

    private GenOperation() {
    }
}

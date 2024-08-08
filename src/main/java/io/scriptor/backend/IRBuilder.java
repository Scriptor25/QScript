package io.scriptor.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

import io.scriptor.backend.inst.AllocaInst;
import io.scriptor.backend.inst.BrInst;
import io.scriptor.backend.inst.CallInst;
import io.scriptor.backend.inst.CondBrInst;
import io.scriptor.backend.inst.FAddInst;
import io.scriptor.backend.inst.FCastInst;
import io.scriptor.backend.inst.FCmpLTInst;
import io.scriptor.backend.inst.FtoICastInst;
import io.scriptor.backend.inst.IAddInst;
import io.scriptor.backend.inst.ICastInst;
import io.scriptor.backend.inst.ICmpLTInst;
import io.scriptor.backend.inst.Instruction;
import io.scriptor.backend.inst.ItoFCastInst;
import io.scriptor.backend.inst.ItoPCastInst;
import io.scriptor.backend.inst.LoadInst;
import io.scriptor.backend.inst.PCastInst;
import io.scriptor.backend.inst.PtoICastInst;
import io.scriptor.backend.inst.RetInst;
import io.scriptor.backend.inst.RetVoidInst;
import io.scriptor.backend.inst.StoreInst;
import io.scriptor.backend.ref.ValueRef;
import io.scriptor.backend.value.ConstArray;
import io.scriptor.backend.value.ConstFlt32;
import io.scriptor.backend.value.ConstFlt64;
import io.scriptor.backend.value.Function;
import io.scriptor.backend.value.ConstInt1;
import io.scriptor.backend.value.ConstInt16;
import io.scriptor.backend.value.ConstInt32;
import io.scriptor.backend.value.ConstInt64;
import io.scriptor.backend.value.ConstInt8;
import io.scriptor.backend.value.ConstValue;
import io.scriptor.backend.value.GlobalValue;
import io.scriptor.backend.value.Value;
import io.scriptor.type.PointerType;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class IRBuilder {

    private static void assertBinary(final Value left, final Value right) {
        assert left != null;
        assert right != null;
        assert left.getType() == right.getType();
    }

    private static void assertUnary(final Value value) {
        assert value != null;
    }

    private static void assertCast(final Value value, final Type type) {
        assert value != null;
        assert type != null;
    }

    private final IRContext context;

    private final Stack<Map<String, ValueRef>> stack = new Stack<>();

    private Block ib;

    public IRBuilder(final IRContext context) {
        this.context = context;
        push();
    }

    public IRContext getContext() {
        return context;
    }

    public void push() {
        stack.push(new HashMap<>());
    }

    public void pop() {
        stack.pop();
    }

    public ValueRef getRef(final String name) {
        for (int i = stack.size() - 1; i >= 0; --i) {
            final var refs = stack.get(i);
            if (refs.containsKey(name))
                return refs.get(name);
        }
        return null;
    }

    public ValueRef getOrCreateRef(final String name, final Supplier<ValueRef> ctor) {
        return stack.peek().computeIfAbsent(name, key -> ctor.get());
    }

    public void putRef(final String name, final ValueRef ref) {
        stack.peek().put(name, ref);
    }

    public Type getVoidTy() {
        return Type.getVoid(context);
    }

    public Type getIntNTy(final int size) {
        return Type.getIntN(context, size);
    }

    public Type getInt1Ty() {
        return Type.getInt1(context);
    }

    public Type getInt8Ty() {
        return Type.getInt8(context);
    }

    public Type getInt16Ty() {
        return Type.getInt16(context);
    }

    public Type getInt32Ty() {
        return Type.getInt32(context);
    }

    public Type getInt64Ty() {
        return Type.getInt64(context);
    }

    public Type getFltNTy(final int size) {
        return Type.getFltN(context, size);
    }

    public Type getFlt32Ty() {
        return Type.getFlt32(context);
    }

    public Type getFlt64Ty() {
        return Type.getFlt64(context);
    }

    public PointerType getPtrTy(final Type base) {
        return PointerType.get(base);
    }

    public ConstValue getIntN(final int size, final long value) {
        return ConstValue.getConstInt(getIntNTy(size), value);
    }

    public ConstInt1 getInt1(final boolean value) {
        return new ConstInt1(getInt1Ty(), value);
    }

    public ConstInt8 getInt8(final byte value) {
        return new ConstInt8(getInt8Ty(), value);
    }

    public ConstInt16 getInt16(final short value) {
        return new ConstInt16(getInt16Ty(), value);
    }

    public ConstInt32 getInt32(final int value) {
        return new ConstInt32(getInt32Ty(), value);
    }

    public ConstInt64 getInt64(final long value) {
        return new ConstInt64(getInt64Ty(), value);
    }

    public ConstValue getFltN(final int size, final double value) {
        return ConstValue.getConstFlt(getFltNTy(size), value);
    }

    public ConstFlt32 getFlt32(final float value) {
        return new ConstFlt32(getFlt32Ty(), value);
    }

    public ConstFlt64 getFlt64(final double value) {
        return new ConstFlt64(getFlt64Ty(), value);
    }

    public void setInsertPoint(final Block block) {
        ib = block;
    }

    public void resetInsertPoint() {
        ib = null;
    }

    public Block getInsertPoint() {
        return ib;
    }

    public Function getInsertFunction() {
        return ib.getParent();
    }

    public boolean isGlobal() {
        return ib == null;
    }

    public <I extends Instruction> I insert(final I inst) {
        if (ib != null) {
            ib.insert(inst);
            return inst;
        }

        throw new QScriptException("no insert point");
    }

    public GlobalValue createGlobalString(final CharSequence string, final String name, final IRModule module) {
        final var strConst = ConstArray.getString(context, string);
        final var global = module.createGlobal(strConst.getType(), strConst, name);
        return global;
    }

    public AllocaInst createAlloca(final Type base, final int count) {
        return insert(new AllocaInst(base, count));
    }

    public StoreInst createStore(final Value ptr, final Value value) {
        return insert(new StoreInst(ptr, value));
    }

    public LoadInst createLoad(final Value ptr) {
        return insert(new LoadInst(ptr));
    }

    public CallInst createCall(final Value callee, final Value[] args) {
        return insert(new CallInst(callee, args));
    }

    public BrInst createBr(final Block dest) {
        return insert(new BrInst(dest));
    }

    public CondBrInst createCondBr(final Value condition, final Block destThen, final Block destElse) {
        return insert(new CondBrInst(condition, destThen, destElse));
    }

    public Value createCmpEQ(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createCmpNE(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createCmpLE(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createCmpGE(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createCmpLT(final Value left, final Value right) {
        assertBinary(left, right);

        final var type = left.getType();
        if (type.isInt())
            return insert(new ICmpLTInst(Type.getInt1(context), left, right));
        if (type.isFlt())
            return insert(new FCmpLTInst(Type.getInt1(context), left, right));

        throw new UnsupportedOperationException();
    }

    public Value createCmpGT(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createAdd(final Value left, final Value right) {
        assertBinary(left, right);

        final var type = left.getType();
        if (type.isInt())
            return insert(new IAddInst(left, right));
        if (type.isFlt())
            return insert(new FAddInst(left, right));

        throw new UnsupportedOperationException();
    }

    public Value createSub(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createMul(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createDiv(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createRem(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createAnd(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createOr(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createXOr(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createLAnd(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createLOr(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createLXOr(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createShL(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createLShR(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createAShR(final Value left, final Value right) {
        assertBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createNeg(final Value value) {
        assertUnary(value);
        throw new UnsupportedOperationException();
    }

    public Value createNot(final Value value) {
        assertUnary(value);
        throw new UnsupportedOperationException();
    }

    public Value createLNot(final Value value) {
        assertUnary(value);
        throw new UnsupportedOperationException();
    }

    public Value createCast(final Value value, final Type type) {
        assertCast(value, type);

        final var vtype = value.getType();
        if (vtype == type)
            return value;

        if (vtype.isInt()) {
            if (type.isInt())
                return insert(new ICastInst(value, type));

            if (type.isFlt())
                return insert(new ItoFCastInst(value, type));

            if (type.isPtr())
                return insert(new ItoPCastInst(value, type));
        }
        if (vtype.isFlt()) {
            if (type.isInt())
                return insert(new FtoICastInst(value, type));
            if (type.isFlt())
                return insert(new FCastInst(value, type));
        }
        if (vtype.isPtr()) {
            if (type.isInt())
                return insert(new PtoICastInst(value, type));
            if (type.isPtr())
                return insert(new PCastInst(value, type));
        }

        throw new UnsupportedOperationException();
    }

    public RetInst createRet(final Value value) {
        return insert(new RetInst(value));
    }

    public RetVoidInst createRetVoid() {
        return insert(new RetVoidInst());
    }
}

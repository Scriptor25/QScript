package io.scriptor.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import io.scriptor.backend.inst.AllocaInst;
import io.scriptor.backend.inst.BrInst;
import io.scriptor.backend.inst.CallInst;
import io.scriptor.backend.inst.CondBrInst;
import io.scriptor.backend.inst.FAddInst;
import io.scriptor.backend.inst.IAddInst;
import io.scriptor.backend.inst.Instruction;
import io.scriptor.backend.inst.StoreInst;
import io.scriptor.backend.value.Function;
import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class IRBuilder {

    private final IRContext context;

    private final Stack<Map<String, Value>> stack = new Stack<>();
    private Map<String, Value> values;

    private Block insertBlock;
    private Instruction insertPoint;

    public IRBuilder(final IRContext context) {
        this.context = context;
    }

    public IRContext getContext() {
        return context;
    }

    public void clearStack() {
        stack.clear();
        values = new HashMap<>();
    }

    public void push() {
        stack.push(values);
        values = new HashMap<>(values);
    }

    public void pop() {
        values = stack.pop();
    }

    public Value getValue(final String name) {
        if (values == null)
            return null;
        return values.get(name);
    }

    public void setValue(final String name, final Value value) {
        values.put(name, value);
    }

    public void setInsertPoint(final Instruction inst) {
        this.insertBlock = inst.getParent();
        this.insertPoint = inst;
    }

    public void setInsertPoint(final Block block) {
        this.insertBlock = block;
        this.insertPoint = block.getEnd();
    }

    public void resetInsertPoint() {
        insertBlock = null;
        insertPoint = null;
    }

    public Block getInsertPoint() {
        return insertBlock;
    }

    public Function getInsertFunction() {
        return insertBlock.getParent();
    }

    public boolean isGlobal() {
        return insertBlock == null;
    }

    public <I extends Instruction> I insert(final I inst) {
        if (insertPoint != null) {
            insertPoint.append(inst);
            return inst;
        }

        if (insertBlock != null) {
            insertBlock.insert(inst);
            insertPoint = inst;
            return inst;
        }

        throw new QScriptException("no insert point");
    }

    public AllocaInst createAlloca(final Type type) {
        return insert(new AllocaInst(type));
    }

    public StoreInst createStore(final Value ptr, final Value value) {
        return insert(new StoreInst(ptr, value));
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

    private static void checkBinary(final Value left, final Value right) {
        if (left == null)
            throw new QScriptException("left is null");
        if (right == null)
            throw new QScriptException("right is null");
        if (left.getType() != right.getType())
            throw new QScriptException("types do not match: %s and %s", left.getType(), right.getType());
    }

    public Value createCmpEQ(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createCmpNE(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createCmpLE(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createCmpGE(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createCmpLT(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createCmpGT(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createAdd(final Value left, final Value right) {
        checkBinary(left, right);

        final var type = left.getType();
        if (type.isInt())
            return insert(new IAddInst(type, left, right));
        if (type.isFlt())
            return insert(new FAddInst(type, left, right));

        throw new UnsupportedOperationException();
    }

    public Value createSub(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createMul(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createDiv(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createRem(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createAnd(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createOr(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createXOr(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createLAnd(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createLOr(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createLXOr(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createShL(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createLShR(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createAShR(final Value left, final Value right) {
        checkBinary(left, right);
        throw new UnsupportedOperationException();
    }

    public Value createNeg(final Value value) {
        throw new UnsupportedOperationException();
    }

    public Value createNot(final Value value) {
        throw new UnsupportedOperationException();
    }

    public Value createLNot(final Value value) {
        throw new UnsupportedOperationException();
    }
}

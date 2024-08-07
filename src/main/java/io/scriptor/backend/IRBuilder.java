package io.scriptor.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import io.scriptor.backend.inst.AllocaInst;
import io.scriptor.backend.inst.CallInst;
import io.scriptor.backend.inst.Instruction;
import io.scriptor.backend.inst.StoreInst;
import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;

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

    public Instruction getInsertPoint() {
        return insertPoint;
    }

    public Block getInsertBlock() {
        return insertBlock;
    }

    public boolean isGlobal() {
        return insertBlock == null;
    }

    public <I extends Instruction> I insert(final I inst) {
        if (insertPoint != null) {
            insertPoint.append(inst);
            return inst;
        }
        insertBlock.insert(inst);
        insertPoint = inst;
        return inst;
    }

    public AllocaInst createAlloca(final Type type) {
        return insert(new AllocaInst(type));
    }

    public StoreInst createStore(final Value ptr, final Value value) {
        return insert(new StoreInst(ptr, value));
    }

    public Value createCall(final Value callee, final Value[] args) {
        return insert(new CallInst(callee, args));
    }
}

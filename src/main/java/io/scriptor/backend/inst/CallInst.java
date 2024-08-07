package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;
import io.scriptor.type.FunctionType;

public class CallInst extends Instruction {

    public CallInst(final Value callee, final Value[] args) {
        super(((FunctionType) callee.getType()).getResult());
    }

}

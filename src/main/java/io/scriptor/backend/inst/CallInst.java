package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;
import io.scriptor.type.FunctionType;

public class CallInst extends Instruction {

    private final Value callee;
    private final Value[] args;

    public CallInst(final Value callee, final Value[] args) {
        super(((FunctionType) callee.getType()).getResult());
        this.callee = callee;
        this.args = args;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("call ");
        callee.dumpFlat();
        for (final var arg : args) {
            System.out.print(", ");
            arg.dumpFlat();
        }
    }
}

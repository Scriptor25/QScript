package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;

public class RetInst extends Instruction {

    private final Value value;

    public RetInst(final Value value) {
        super(null);
        this.value = value;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("ret ");
        value.dumpFlat();
    }
}

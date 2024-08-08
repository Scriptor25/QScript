package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;

public class IAddInst extends Instruction {

    private final Value left;
    private final Value right;

    public IAddInst(final Value left, final Value right) {
        super(left.getType());
        this.left = left;
        this.right = right;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("iadd, ");
        left.dumpFlat();
        System.out.print(", ");
        right.dumpFlat();
    }
}

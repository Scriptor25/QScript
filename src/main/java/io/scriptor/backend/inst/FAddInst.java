package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;

public class FAddInst extends Instruction {

    private final Value left;
    private final Value right;

    public FAddInst(final Value left, final Value right) {
        super(left.getType());
        this.left = left;
        this.right = right;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("fadd ");
        left.dumpFlat();
        System.out.print(", ");
        right.dumpFlat();
    }
}

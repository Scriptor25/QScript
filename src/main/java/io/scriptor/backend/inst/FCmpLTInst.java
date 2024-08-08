package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;

public class FCmpLTInst extends Instruction {

    private final Value left;
    private final Value right;

    public FCmpLTInst(final Type type, final Value left, final Value right) {
        super(type);
        this.left = left;
        this.right = right;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("fcmp lt, ");
        left.dumpFlat();
        System.out.print(", ");
        right.dumpFlat();
    }
}

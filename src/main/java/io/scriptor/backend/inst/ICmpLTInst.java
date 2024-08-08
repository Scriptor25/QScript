package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;

public class ICmpLTInst extends Instruction {

    private final Value left;
    private final Value right;

    public ICmpLTInst(final Type type, final Value left, final Value right) {
        super(type);
        this.left = left;
        this.right = right;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("icmp lt, ");
        left.dumpFlat();
        System.out.print(", ");
        right.dumpFlat();
    }
}

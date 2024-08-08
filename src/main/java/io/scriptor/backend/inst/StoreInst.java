package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;

public class StoreInst extends Instruction {

    private final Value ptr;
    private final Value value;

    public StoreInst(final Value ptr, final Value value) {
        super(null);
        this.ptr = ptr;
        this.value = value;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("store ");
        ptr.dumpFlat();
        System.out.print(", ");
        value.dumpFlat();
    }
}
package io.scriptor.backend.inst;

import io.scriptor.backend.Block;

public class BrInst extends Instruction {

    private final Block dest;

    public BrInst(final Block dest) {
        super(null);
        this.dest = dest;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("br ");
        dest.dumpFlat();
    }
}

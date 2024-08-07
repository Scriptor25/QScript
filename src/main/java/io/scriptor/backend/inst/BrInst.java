package io.scriptor.backend.inst;

import io.scriptor.backend.Block;

public class BrInst extends Instruction {

    private final Block dest;

    public BrInst(final Block dest) {
        super(null);
        this.dest = dest;
    }

    public Block getDest() {
        return dest;
    }
}

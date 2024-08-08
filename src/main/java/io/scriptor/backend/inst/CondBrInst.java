package io.scriptor.backend.inst;

import io.scriptor.backend.Block;
import io.scriptor.backend.value.Value;

public class CondBrInst extends Instruction {

    private final Value condition;
    private final Block destThen;
    private final Block destElse;

    public CondBrInst(final Value condition, final Block destThen, final Block destElse) {
        super(null);
        this.condition = condition;
        this.destThen = destThen;
        this.destElse = destElse;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("br ");
        condition.dumpFlat();
        System.out.print(", ");
        destThen.dumpFlat();
        System.out.print(", ");
        destElse.dumpFlat();
    }
}

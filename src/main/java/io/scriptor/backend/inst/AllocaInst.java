package io.scriptor.backend.inst;

import io.scriptor.type.PointerType;
import io.scriptor.type.Type;

public class AllocaInst extends Instruction {

    private final Type base;
    private final int count;

    public AllocaInst(final Type base, final int count) {
        super(PointerType.get(base));
        this.base = base;
        this.count = count;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.printf("alloca %s, 0x%08X", base, count);
    }
}

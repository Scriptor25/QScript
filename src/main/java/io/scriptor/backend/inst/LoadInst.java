package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;
import io.scriptor.type.PointerType;

public class LoadInst extends Instruction {

    private final Value ptr;

    public LoadInst(final Value ptr) {
        super(((PointerType) ptr.getType()).getBase());
        this.ptr = ptr;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("load ");
        ptr.dumpFlat();
    }
}

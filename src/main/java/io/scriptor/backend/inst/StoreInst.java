package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;

public class StoreInst extends Instruction {

    public StoreInst(final Value ptr, final Value value) {
        super(ptr.getType());
    }
}

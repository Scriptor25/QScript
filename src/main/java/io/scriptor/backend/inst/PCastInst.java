package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;

public class PCastInst extends Instruction {

    private final Value value;

    public PCastInst(final Value value, final Type type) {
        super(type);
        this.value = value;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("cast ptop, ");
        value.dumpFlat();
        System.out.printf(", %s", getType());
    }
}

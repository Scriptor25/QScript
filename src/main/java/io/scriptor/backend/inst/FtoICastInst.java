package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;

public class FtoICastInst extends Instruction {

    private final Value value;

    public FtoICastInst(final Value value, final Type type) {
        super(type);
        this.value = value;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("cast ftoi, ");
        value.dumpFlat();
        System.out.printf(", %s", getType());
    }
}

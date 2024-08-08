package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;

public class FCastInst extends Instruction {

    private final Value value;

    public FCastInst(final Value value, final Type type) {
        super(type);
        this.value = value;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("cast ftof, ");
        value.dumpFlat();
        System.out.printf(", %s", getType());
    }
}

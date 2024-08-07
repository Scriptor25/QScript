package io.scriptor.backend.inst;

import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;

public class FAddInst extends Instruction {

    public FAddInst(final Type type, final Value left, final Value right) {
        super(type);
    }
}

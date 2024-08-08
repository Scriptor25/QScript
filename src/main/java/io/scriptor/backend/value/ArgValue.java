package io.scriptor.backend.value;

import io.scriptor.type.Type;

public class ArgValue extends Value {

    public ArgValue(final Type type) {
        super(type);
    }

    @Override
    public void dumpFlat() {
        if (hasName())
            super.dumpFlat();
        else
            System.out.printf("%s", getType());
    }
}

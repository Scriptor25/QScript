package io.scriptor.backend.value;

import io.scriptor.type.PointerType;
import io.scriptor.type.Type;

public class GlobalValue extends Value {

    private final ConstValue init;

    public GlobalValue(final Type type, final String name) {
        this(type, null, name);
    }

    public GlobalValue(final Type type, final ConstValue init) {
        this(type, init, null);
    }

    public GlobalValue(final Type type, final ConstValue init, final String name) {
        super(PointerType.get(type), name);
        this.init = init;
    }

    @Override
    public void dump() {
        dumpFlat();
        if (init != null) {
            System.out.print(" = ");
            init.dump();
        }
    }

    @Override
    public void dumpFlat() {
        System.out.printf("%s @%s", getType(), getName());
    }

    public ConstValue getInit() {
        return init;
    }
}

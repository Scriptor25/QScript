package io.scriptor.backend.value;

import io.scriptor.type.Type;
import io.scriptor.util.Util;

public class ConstString extends ConstArray {

    private final CharSequence string;

    public ConstString(final Type type, final CharSequence string) {
        super(type);
        this.string = string;
    }

    @Override
    public void dump() {
        super.dump();
        System.out.printf("\"%s\"", Util.unescape(string));
    }

    public CharSequence getString() {
        return string;
    }
}

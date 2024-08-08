package io.scriptor.backend.value;

import io.scriptor.backend.IRContext;
import io.scriptor.type.ArrayType;
import io.scriptor.type.Type;

public class ConstArray extends ConstValue {

    public static ConstArray getString(final IRContext context, final CharSequence string) {
        return new ConstString(ArrayType.get(context.getType("i8"), string.length() + 1), string);
    }

    public ConstArray(final Type type) {
        super(type);
    }

    public ConstArray(final Type base, final int count) {
        this(ArrayType.get(base, count));
    }
}

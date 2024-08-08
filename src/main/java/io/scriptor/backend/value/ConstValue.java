package io.scriptor.backend.value;

import java.lang.reflect.Array;

import io.scriptor.backend.IRContext;
import io.scriptor.type.Type;

public abstract class ConstValue extends Value {

    public static ConstValue getConstNative(final IRContext context, final Object value) {
        if (value.getClass().isArray()) {
            final var array = new Value[Array.getLength(value)];
            for (int i = 0; i < array.length; ++i) {
                array[i] = getConstNative(context, Array.get(value, i));
            }
            // final var type = Type.getNative(context, value.getClass());
            throw new UnsupportedOperationException();
        }

        if (value instanceof Boolean v)
            return new ConstInt1(context, v);
        if (value instanceof Byte v)
            return new ConstInt8(context, v);
        if (value instanceof Short v)
            return new ConstInt16(context, v);
        if (value instanceof Integer v)
            return new ConstInt32(context, v);
        if (value instanceof Long v)
            return new ConstInt64(context, v);
        if (value instanceof Float v)
            return new ConstFlt32(context, v);
        if (value instanceof Double v)
            return new ConstFlt64(context, v);
        throw new UnsupportedOperationException();
    }

    public static ConstValue getConstInt(final Type type, final long value) {
        if (type.isInt1())
            return new ConstInt1(type, value != 0);
        if (type.isInt8())
            return new ConstInt8(type, (byte) value);
        if (type.isInt16())
            return new ConstInt16(type, (short) value);
        if (type.isInt32())
            return new ConstInt32(type, (int) value);
        if (type.isInt64())
            return new ConstInt64(type, value);

        throw new UnsupportedOperationException();
    }

    public static ConstValue getConstFlt(final Type type, final double value) {
        if (type.isFlt32())
            return new ConstFlt32(type, (float) value);
        if (type.isFlt64())
            return new ConstFlt64(type, value);

        throw new UnsupportedOperationException();
    }

    protected ConstValue(final Type type) {
        super(type);
    }

    protected ConstValue(final Type type, final String name) {
        super(type, name);
    }

    @Override
    public void dump() {
        System.out.printf("%s const ", getType());
    }

    @Override
    public void dumpFlat() {
        dump();
    }
}

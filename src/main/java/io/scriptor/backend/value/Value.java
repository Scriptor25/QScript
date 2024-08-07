package io.scriptor.backend.value;

import java.lang.reflect.Array;

import io.scriptor.backend.IRContext;
import io.scriptor.type.Type;

public abstract class Value {

    public static Value getConstNative(final IRContext context, final Object value) {
        if (value.getClass().isArray()) {
            final var array = new Value[Array.getLength(value)];
            for (int i = 0; i < array.length; ++i) {
                array[i] = getConstNative(context, Array.get(value, i));
            }
            final var type = Type.getNative(context, value.getClass());
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

    public static Value getConstInt(final Type type, final long value) {
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

    public static Value getConstFlt(final Type type, final double value) {
        if (type.isFlt32())
            return new ConstFlt32(type, (float) value);
        if (type.isFlt64())
            return new ConstFlt64(type, value);
        throw new UnsupportedOperationException();
    }

    private final Type type;
    private boolean isReturn;

    protected Value(final Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isReturn() {
        return isReturn;
    }

    public Value setReturn(final boolean isReturn) {
        this.isReturn = isReturn;
        return this;
    }

    public <T> T getNative() {
        throw new UnsupportedOperationException();
    }

    public boolean getInt1() {
        throw new UnsupportedOperationException();
    }

    public byte getInt8() {
        throw new UnsupportedOperationException();
    }

    public short getInt16() {
        throw new UnsupportedOperationException();
    }

    public int getInt32() {
        throw new UnsupportedOperationException();
    }

    public long getInt64() {
        throw new UnsupportedOperationException();
    }

    public float getFlt32() {
        throw new UnsupportedOperationException();
    }

    public double getFlt64() {
        throw new UnsupportedOperationException();
    }

    public long getPtr() {
        throw new UnsupportedOperationException();
    }
}

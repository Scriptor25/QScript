package io.scriptor.environment;

import io.scriptor.QScriptException;
import io.scriptor.type.Type;

public class ConstValue<E> extends Value {

    public static ConstValue<?> getDefault(final Type type) {
        if (type == Type.get("i1"))
            return new ConstValue<>(type, false);
        if (type == Type.get("i8"))
            return new ConstValue<>(type, (byte) 0);
        if (type == Type.get("i16"))
            return new ConstValue<>(type, (short) 0);
        if (type == Type.get("i32"))
            return new ConstValue<>(type, 0);
        if (type == Type.get("i64"))
            return new ConstValue<>(type, 0L);
        if (type == Type.get("f32"))
            return new ConstValue<>(type, 0.0f);
        if (type == Type.get("f64"))
            return new ConstValue<>(type, 0.0);
        if (type.isPointer())
            return new ConstValue<>(type, 0L);
        throw new QScriptException();
    }

    public static ConstValue<?> fromJava(final Object object) {
        if (object instanceof Boolean b)
            return new ConstValue<>(Type.get("i1"), b);
        if (object instanceof Byte b)
            return new ConstValue<>(Type.get("i8"), b);
        if (object instanceof Short s)
            return new ConstValue<>(Type.get("i16"), s);
        if (object instanceof Integer i)
            return new ConstValue<>(Type.get("i32"), i);
        if (object instanceof Long l)
            return new ConstValue<>(Type.get("i64"), l);
        if (object instanceof Float f)
            return new ConstValue<>(Type.get("f32"), f);
        if (object instanceof Double d)
            return new ConstValue<>(Type.get("f64"), d);
        throw new QScriptException();
    }

    private final E java;

    public ConstValue(final Type type, final E java) {
        super(type);
        this.java = java;
    }

    @Override
    public boolean getBoolean() {
        if (java instanceof Boolean b)
            return b;
        if (java instanceof Number n)
            return n.doubleValue() != 0.0;
        throw new QScriptException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getJava() {
        return (T) java;
    }

    @Override
    public String toString() {
        return java.toString();
    }

    @Override
    public int hashCode() {
        return java.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (o instanceof ConstValue v)
            return java.equals(v.java);
        return false;
    }
}

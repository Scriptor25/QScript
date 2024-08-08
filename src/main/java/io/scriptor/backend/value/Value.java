package io.scriptor.backend.value;

import io.scriptor.type.Type;

public abstract class Value {

    private final Type type;
    private String name;

    protected Value(final Type type) {
        this.type = type;
    }

    protected Value(final Type type, final String name) {
        this.type = type;
        this.name = name;
    }

    public void dump() {
        dumpFlat();
    }

    public void dumpFlat() {
        System.out.printf("%s %%%s", type, getName());
    }

    public Type getType() {
        return type;
    }

    public boolean hasName() {
        return name != null;
    }

    public String getName() {
        return name == null ? Integer.toString(hashCode()) : name;
    }

    public void setName(final String name) {
        this.name = name;
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

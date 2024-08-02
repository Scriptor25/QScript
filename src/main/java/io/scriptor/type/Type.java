package io.scriptor.type;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.QScriptException;

public class Type {

    protected static final int IS_VOID = 1;
    protected static final int IS_INT = 2;
    protected static final int IS_FLOAT = 4;
    protected static final int IS_POINTER = 8;
    protected static final int IS_FUNCTION = 16;

    private static final Map<String, Type> TYPES = new HashMap<>();
    static {
        new Type("void", IS_VOID, 0);
        new Type("i1", IS_INT, 1);
        new Type("i8", IS_INT, 8);
        new Type("i16", IS_INT, 16);
        new Type("i32", IS_INT, 32);
        new Type("i64", IS_INT, 64);
        new Type("f32", IS_FLOAT, 32);
        new Type("f64", IS_FLOAT, 64);
    }

    public static Type get(final String id) {
        return TYPES.get(id);
    }

    public static Type getVoid() {
        return get("void");
    }

    public static Type getInt1() {
        return get("i1");
    }

    public static Type getInt8() {
        return get("i8");
    }

    public static Type getInt16() {
        return get("i16");
    }

    public static Type getInt32() {
        return get("i32");
    }

    public static Type getInt64() {
        return get("i64");
    }

    public static Type getFlt32() {
        return get("f32");
    }

    public static Type getFlt64() {
        return get("f64");
    }

    public static Type getVoidPtr() {
        return PointerType.get(getVoid());
    }

    public static Type getInt8Ptr() {
        return PointerType.get(getInt8());
    }

    public static Type getHigherOrder(final Type a, final Type b) {
        if (a == b)
            return a;

        if (a.isInt()) {
            if (b.isInt()) {
                if (a.getSize() >= b.getSize())
                    return a;
                return b;
            }

            if (b.isFloat())
                return b;

            if (b.isPointer())
                return a;
        }

        if (a.isFloat()) {
            if (b.isInt())
                return a;

            if (b.isFloat()) {
                if (a.getSize() >= b.getSize())
                    return a;
                return b;
            }

            if (b.isPointer())
                return a;
        }

        if (a.isPointer()) {
            if (b.isInt())
                return b;

            if (b.isFloat())
                return b;
        }

        throw new QScriptException();
    }

    protected static <T extends Type> T create(final String id, final T type) {
        TYPES.put(id, type);
        return type;
    }

    private final String id;
    private final int flags;
    private final int size;

    protected Type(final String id, final int flags, final int size) {
        TYPES.put(id, this);
        this.id = id;
        this.flags = flags;
        this.size = size;
    }

    @Override
    public String toString() {
        return id;
    }

    public String getId() {
        return id;
    }

    public int getFlags() {
        return flags;
    }

    public int getSize() {
        return size;
    }

    public boolean isVoid() {
        return (flags & IS_VOID) != 0;
    }

    public boolean isInt() {
        return (flags & IS_INT) != 0;
    }

    public boolean isFloat() {
        return (flags & IS_FLOAT) != 0;
    }

    public boolean isPointer() {
        return (flags & IS_POINTER) != 0;
    }

    public boolean isFunction() {
        return (flags & IS_FUNCTION) != 0;
    }
}

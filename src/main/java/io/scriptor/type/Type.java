package io.scriptor.type;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.util.QScriptException;

public class Type {

    public static final int IS_VOID = 1;
    public static final int IS_INTEGER = 2;
    public static final int IS_FLOAT = 4;
    public static final int IS_POINTER = 8;
    public static final int IS_FUNCTION = 16;
    public static final int IS_STRUCT = 32;
    public static final int IS_ARRAY = 64;

    private static final Map<String, Type> types = new HashMap<>();
    static {
        new Type("void", IS_VOID, 0);
        new Type("i1", IS_INTEGER, 1);
        new Type("i8", IS_INTEGER, 8);
        new Type("i16", IS_INTEGER, 16);
        new Type("i32", IS_INTEGER, 32);
        new Type("i64", IS_INTEGER, 64);
        new Type("f32", IS_FLOAT, 32);
        new Type("f64", IS_FLOAT, 64);
    }

    public static void useAs(final String id, final Type type) {
        types.put(id, type);
    }

    public static boolean exists(final String id) {
        return types.containsKey(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Type> T get(final String id) {
        if (!exists(id))
            throw new QScriptException("no such type with id '%s'", id);
        return (T) types.get(id);
    }

    public static Type getVoid() {
        return get("void");
    }

    public static Type getIntN(final int size) {
        return get(switch (size) {
            case 1 -> "i1";
            case 8 -> "i8";
            case 16 -> "i16";
            case 32 -> "i32";
            case 64 -> "i64";
            default -> null;
        });
    }

    public static Type getFltN(final int size) {
        return get(switch (size) {
            case 32 -> "f32";
            case 64 -> "f64";
            default -> null;
        });
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

    public static Type getHigherOrder(final SourceLocation location, final Type a, final Type b) {
        if (a == b)
            return a;

        if (a.isInt()) {
            if (b.isInt()) {
                if (a.getSize() >= b.getSize())
                    return a;
                return b;
            }

            if (b.isFlt())
                return b;

            if (b.isPtr())
                return a;
        }

        if (a.isFlt()) {
            if (b.isInt())
                return a;

            if (b.isFlt()) {
                if (a.getSize() >= b.getSize())
                    return a;
                return b;
            }

            if (b.isPtr())
                return a;
        }

        if (a.isPtr()) {
            if (b.isInt())
                return b;

            if (b.isFlt())
                return b;
        }

        throw new QScriptException(location, "cannot determine higher order type from %s and %s", a, b);
    }

    public static Type getNative(final Class<?> clazz) {
        if (clazz.isArray()) {
            final var base = getNative(clazz.getComponentType());
            return PointerType.get(base);
        }

        if (clazz == Void.class || clazz == void.class)
            return Type.getVoid();
        if (clazz == Boolean.class || clazz == boolean.class)
            return Type.getInt1();
        if (clazz == Byte.class || clazz == byte.class)
            return Type.getInt8();
        if (clazz == Short.class || clazz == short.class)
            return Type.getInt16();
        if (clazz == Integer.class || clazz == int.class)
            return Type.getInt32();
        if (clazz == Long.class || clazz == long.class)
            return Type.getInt64();
        if (clazz == Float.class || clazz == float.class)
            return Type.getFlt32();
        if (clazz == Double.class || clazz == double.class)
            return Type.getFlt64();

        if (CharSequence.class.isAssignableFrom(clazz))
            return Type.getInt8Ptr();

        return Type.get(clazz.getSimpleName());
    }

    private final String id;
    private final int flags;
    private final int size;

    public Type(final String id, final int flags, final int size) {
        types.put(id, this);

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
        return (flags & IS_INTEGER) != 0;
    }

    public boolean isInt(final int size) {
        return isInt() && this.size == size;
    }

    public boolean isInt1() {
        return isInt(1);
    }

    public boolean isInt8() {
        return isInt(8);
    }

    public boolean isInt16() {
        return isInt(16);
    }

    public boolean isInt32() {
        return isInt(32);
    }

    public boolean isInt64() {
        return isInt(64);
    }

    public boolean isFlt() {
        return (flags & IS_FLOAT) != 0;
    }

    public boolean isFlt(final int size) {
        return isFlt() && this.size == size;
    }

    public boolean isFlt32() {
        return isFlt(32);
    }

    public boolean isFlt64() {
        return isFlt(64);
    }

    public boolean isPtr() {
        return (flags & IS_POINTER) != 0;
    }

    public boolean isFun() {
        return (flags & IS_FUNCTION) != 0;
    }
}

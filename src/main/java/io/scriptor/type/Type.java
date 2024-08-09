package io.scriptor.type;

import io.scriptor.frontend.Context;
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

    public static void useAs(final Context ctx, final String id, final Type type) {
        ctx.putType(id, type);
    }

    public static boolean exists(final Context ctx, final String id) {
        return ctx.existsType(id);
    }

    public static <T extends Type> T get(final Context ctx, final String id) {
        return ctx.getType(id);
    }

    public static Type getVoid(final Context ctx) {
        return get(ctx, "void");
    }

    public static Type getIntN(final Context ctx, final int size) {
        return get(ctx, switch (size) {
            case 1 -> "i1";
            case 8 -> "i8";
            case 16 -> "i16";
            case 32 -> "i32";
            case 64 -> "i64";
            default -> null;
        });
    }

    public static Type getFltN(final Context ctx, final int size) {
        return get(ctx, switch (size) {
            case 32 -> "f32";
            case 64 -> "f64";
            default -> null;
        });
    }

    public static Type getInt1(final Context ctx) {
        return get(ctx, "i1");
    }

    public static Type getInt8(final Context ctx) {
        return get(ctx, "i8");
    }

    public static Type getInt16(final Context ctx) {
        return get(ctx, "i16");
    }

    public static Type getInt32(final Context ctx) {
        return get(ctx, "i32");
    }

    public static Type getInt64(final Context ctx) {
        return get(ctx, "i64");
    }

    public static Type getFlt32(final Context ctx) {
        return get(ctx, "f32");
    }

    public static Type getFlt64(final Context ctx) {
        return get(ctx, "f64");
    }

    public static Type getVoidPtr(final Context ctx) {
        return PointerType.get(getVoid(ctx));
    }

    public static Type getInt8Ptr(final Context ctx) {
        return PointerType.get(getInt8(ctx));
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

    public static Type getNative(final Context ctx, final Class<?> clazz) {
        if (clazz.isArray()) {
            final var base = getNative(ctx, clazz.getComponentType());
            return PointerType.get(base);
        }

        if (clazz == Void.class || clazz == void.class)
            return Type.getVoid(ctx);
        if (clazz == Boolean.class || clazz == boolean.class)
            return Type.getInt1(ctx);
        if (clazz == Byte.class || clazz == byte.class)
            return Type.getInt8(ctx);
        if (clazz == Short.class || clazz == short.class)
            return Type.getInt16(ctx);
        if (clazz == Integer.class || clazz == int.class)
            return Type.getInt32(ctx);
        if (clazz == Long.class || clazz == long.class)
            return Type.getInt64(ctx);
        if (clazz == Float.class || clazz == float.class)
            return Type.getFlt32(ctx);
        if (clazz == Double.class || clazz == double.class)
            return Type.getFlt64(ctx);

        if (CharSequence.class.isAssignableFrom(clazz))
            return Type.getInt8Ptr(ctx);

        return Type.get(ctx, clazz.getSimpleName());
    }

    private final Context ctx;
    private final String id;
    private final int flags;
    private final int size;

    public Type(final Context ctx, final String id, final int flags, final int size) {
        ctx.putType(id, this);
        this.ctx = ctx;
        this.id = id;
        this.flags = flags;
        this.size = size;
    }

    @Override
    public String toString() {
        return id;
    }

    public Context getCtx() {
        return ctx;
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

    public boolean isFunction() {
        return (flags & IS_FUNCTION) != 0;
    }

    public boolean isStruct() {
        return (flags & IS_STRUCT) != 0;
    }
}

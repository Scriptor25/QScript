package io.scriptor.type;

import io.scriptor.QScriptException;
import io.scriptor.backend.IRContext;
import io.scriptor.frontend.SourceLocation;

public class Type {

    public static final int IS_VOID = 1;
    public static final int IS_INT = 2;
    public static final int IS_FLT = 4;
    public static final int IS_PTR = 8;
    public static final int IS_FUN = 16;
    public static final int IS_STRUCT = 32;

    public static void useAs(final IRContext context, final String id, final Type type) {
        context.getType(id, () -> type);
    }

    public static Type get(final IRContext context, final String id) {
        return context.getType(id);
    }

    public static Type getVoid(final IRContext context) {
        return get(context, "void");
    }

    public static Type getInt1(final IRContext context) {
        return get(context, "i1");
    }

    public static Type getInt8(final IRContext context) {
        return get(context, "i8");
    }

    public static Type getInt16(final IRContext context) {
        return get(context, "i16");
    }

    public static Type getInt32(final IRContext context) {
        return get(context, "i32");
    }

    public static Type getInt64(final IRContext context) {
        return get(context, "i64");
    }

    public static Type getFlt32(final IRContext context) {
        return get(context, "f32");
    }

    public static Type getFlt64(final IRContext context) {
        return get(context, "f64");
    }

    public static Type getVoidPtr(final IRContext context) {
        return PointerType.get(getVoid(context));
    }

    public static Type getInt8Ptr(final IRContext context) {
        return PointerType.get(getInt8(context));
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

    public static Type getNative(final IRContext context, final Class<?> clazz) {
        if (clazz.isArray()) {
            final var base = getNative(context, clazz.getComponentType());
            return PointerType.get(base);
        }

        if (clazz == Void.class || clazz == void.class)
            return Type.getVoid(context);
        if (clazz == Boolean.class || clazz == boolean.class)
            return Type.getInt1(context);
        if (clazz == Byte.class || clazz == byte.class)
            return Type.getInt8(context);
        if (clazz == Short.class || clazz == short.class)
            return Type.getInt16(context);
        if (clazz == Integer.class || clazz == int.class)
            return Type.getInt32(context);
        if (clazz == Long.class || clazz == long.class)
            return Type.getInt64(context);
        if (clazz == Float.class || clazz == float.class)
            return Type.getFlt32(context);
        if (clazz == Double.class || clazz == double.class)
            return Type.getFlt64(context);

        return Type.get(context, clazz.getSimpleName());
    }

    private final IRContext context;
    private final String id;
    private final int flags;
    private final int size;

    public Type(final IRContext context, final String id, final int flags, final int size) {
        context.getType(id, () -> this);
        this.context = context;
        this.id = id;
        this.flags = flags;
        this.size = size;
    }

    @Override
    public String toString() {
        return id;
    }

    public IRContext getContext() {
        return context;
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
        return (flags & IS_FLT) != 0;
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
        return (flags & IS_PTR) != 0;
    }

    public boolean isFun() {
        return (flags & IS_FUN) != 0;
    }
}

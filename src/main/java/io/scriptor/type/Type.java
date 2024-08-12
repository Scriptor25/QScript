package io.scriptor.type;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.StackFrame;
import io.scriptor.util.QScriptException;

public class Type {

    public static final int IS_VOID = 1;
    public static final int IS_INTEGER = 2;
    public static final int IS_FLOAT = 4;
    public static final int IS_POINTER = 8;
    public static final int IS_FUNCTION = 16;
    public static final int IS_STRUCT = 32;
    public static final int IS_ARRAY = 64;

    public static void useAs(final StackFrame frame, final String id, final Type type) {
        frame.putType(id, type);
    }

    public static boolean exists(final StackFrame frame, final String id) {
        return frame.existsType(id);
    }

    public static <T extends Type> T get(final SourceLocation sl, final StackFrame frame, final String id) {
        return frame.getType(sl, id);
    }

    public static Type getVoid(final StackFrame frame) {
        return get(null, frame, "void");
    }

    public static Type getIntN(final SourceLocation sl, final StackFrame frame, final int size) {
        return get(sl, frame, switch (size) {
            case 1 -> "i1";
            case 8 -> "i8";
            case 16 -> "i16";
            case 32 -> "i32";
            case 64 -> "i64";
            default -> null;
        });
    }

    public static Type getFltN(final SourceLocation sl, final StackFrame frame, final int size) {
        return get(sl, frame, switch (size) {
            case 32 -> "f32";
            case 64 -> "f64";
            default -> null;
        });
    }

    public static Type getInt1(final StackFrame frame) {
        return get(null, frame, "i1");
    }

    public static Type getInt8(final StackFrame frame) {
        return get(null, frame, "i8");
    }

    public static Type getInt16(final StackFrame frame) {
        return get(null, frame, "i16");
    }

    public static Type getInt32(final StackFrame frame) {
        return get(null, frame, "i32");
    }

    public static Type getInt64(final StackFrame frame) {
        return get(null, frame, "i64");
    }

    public static Type getFlt32(final StackFrame frame) {
        return get(null, frame, "f32");
    }

    public static Type getFlt64(final StackFrame frame) {
        return get(null, frame, "f64");
    }

    public static Type getVoidPtr(final StackFrame frame) {
        return PointerType.get(getVoid(frame));
    }

    public static Type getInt8Ptr(final StackFrame frame) {
        return PointerType.get(getInt8(frame));
    }

    public static Type getHigherOrder(final SourceLocation sl, final Type a, final Type b) {
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

            if (b.isPointer())
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

            if (b.isPointer())
                return a;
        }

        if (a.isPointer()) {
            if (b.isInt())
                return b;

            if (b.isFlt())
                return b;
        }

        throw new QScriptException(sl, "cannot determine higher order type from %s and %s", a, b);
    }

    public static Type getNative(final SourceLocation sl, final StackFrame frame, final Class<?> clazz) {
        if (clazz.isArray()) {
            final var base = getNative(sl, frame, clazz.getComponentType());
            return PointerType.get(base);
        }

        if (clazz == Void.class || clazz == void.class)
            return Type.getVoid(frame);
        if (clazz == Boolean.class || clazz == boolean.class)
            return Type.getInt1(frame);
        if (clazz == Byte.class || clazz == byte.class)
            return Type.getInt8(frame);
        if (clazz == Short.class || clazz == short.class)
            return Type.getInt16(frame);
        if (clazz == Integer.class || clazz == int.class)
            return Type.getInt32(frame);
        if (clazz == Long.class || clazz == long.class)
            return Type.getInt64(frame);
        if (clazz == Float.class || clazz == float.class)
            return Type.getFlt32(frame);
        if (clazz == Double.class || clazz == double.class)
            return Type.getFlt64(frame);

        if (CharSequence.class.isAssignableFrom(clazz))
            return Type.getInt8Ptr(frame);

        return Type.get(sl, frame, clazz.getSimpleName());
    }

    private final StackFrame frame;
    private final String id;
    private final int flags;
    private final long size;

    public Type(final StackFrame frame, final String id, final int flags, final long size) {
        frame.putType(id, this);
        this.frame = frame;
        this.id = id;
        this.flags = flags;
        this.size = size;
    }

    @Override
    public String toString() {
        return id;
    }

    public StackFrame getFrame() {
        return frame;
    }

    public String getId() {
        return id;
    }

    public int getFlags() {
        return flags;
    }

    public long getSize() {
        return size;
    }

    public boolean isVoid() {
        return (flags & IS_VOID) != 0;
    }

    public boolean isInt() {
        return (flags & IS_INTEGER) != 0;
    }

    public boolean isInt(final long size) {
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

    public boolean isPointer() {
        return (flags & IS_POINTER) != 0;
    }

    public boolean isFunction() {
        return (flags & IS_FUNCTION) != 0;
    }

    public boolean isStruct() {
        return (flags & IS_STRUCT) != 0;
    }

    public boolean isArray() {
        return (flags & IS_ARRAY) != 0;
    }

    public PointerType asPointer() {
        if (!isPointer())
            return null;
        return (PointerType) this;
    }

    public FunctionType asFunction() {
        if (!isFunction())
            return null;
        return (FunctionType) this;
    }

    public StructType asStruct() {
        if (!isStruct())
            return null;
        return (StructType) this;
    }

    public ArrayType asArray() {
        if (!isArray())
            return null;
        return (ArrayType) this;
    }
}

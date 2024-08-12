package io.scriptor.type;

import java.util.Optional;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.frontend.StackFrame;
import io.scriptor.util.QScriptError;

public class Type {

    public static final int IS_VOID = 1;
    public static final int IS_INTEGER = 2;
    public static final int IS_FLOAT = 4;
    public static final int IS_POINTER = 8;
    public static final int IS_FUNCTION = 16;
    public static final int IS_STRUCT = 32;
    public static final int IS_ARRAY = 64;

    public static void useAs(final StackFrame frame, final SourceLocation sl, final String id, final Type type) {
        frame.putType(sl, id, type);
    }

    public static boolean exists(final StackFrame frame, final String id) {
        return frame.existsType(id);
    }

    public static <T extends Type> Optional<T> get(final SourceLocation sl, final StackFrame frame, final String name) {
        return frame.getType(sl, name);
    }

    public static Type getVoid(final StackFrame frame) {
        return get(null, frame, "void").get();
    }

    public static Optional<Type> getIntN(final SourceLocation sl, final StackFrame frame, final int size) {
        final var name = switch (size) {
            case 1 -> "i1";
            case 8 -> "i8";
            case 16 -> "i16";
            case 32 -> "i32";
            case 64 -> "i64";
            default -> null;
        };
        if (name == null)
            return Optional.empty();
        return get(sl, frame, name);
    }

    public static Optional<Type> getFltN(final SourceLocation sl, final StackFrame frame, final int size) {
        final var name = switch (size) {
            case 32 -> "f32";
            case 64 -> "f64";
            default -> null;
        };
        if (name == null)
            return Optional.empty();
        return get(sl, frame, name);
    }

    public static Type getInt1(final StackFrame frame) {
        return get(null, frame, "i1").get();
    }

    public static Type getInt8(final StackFrame frame) {
        return get(null, frame, "i8").get();
    }

    public static Type getInt16(final StackFrame frame) {
        return get(null, frame, "i16").get();
    }

    public static Type getInt32(final StackFrame frame) {
        return get(null, frame, "i32").get();
    }

    public static Type getInt64(final StackFrame frame) {
        return get(null, frame, "i64").get();
    }

    public static Type getFlt32(final StackFrame frame) {
        return get(null, frame, "f32").get();
    }

    public static Type getFlt64(final StackFrame frame) {
        return get(null, frame, "f64").get();
    }

    public static Type getVoidPtr(final StackFrame frame) {
        return PointerType.get(getVoid(frame));
    }

    public static Type getInt8Ptr(final StackFrame frame) {
        return PointerType.get(getInt8(frame));
    }

    public static Optional<Type> getHigherOrder(final SourceLocation sl, final Type a, final Type b) {
        if (a == b)
            return Optional.of(a);

        if (a.isInt()) {
            if (b.isInt()) {
                if (a.getSize() >= b.getSize())
                    return Optional.of(a);
                return Optional.of(b);
            }

            if (b.isFlt())
                return Optional.of(b);

            if (b.isPointer())
                return Optional.of(a);
        }

        if (a.isFlt()) {
            if (b.isInt())
                return Optional.of(a);

            if (b.isFlt()) {
                if (a.getSize() >= b.getSize())
                    return Optional.of(a);
                return Optional.of(b);
            }

            if (b.isPointer())
                return Optional.of(a);
        }

        if (a.isPointer()) {
            if (b.isInt())
                return Optional.of(b);

            if (b.isFlt())
                return Optional.of(b);
        }

        QScriptError.print(sl, "cannot determine higher order type from %s and %s", a, b);
        return Optional.empty();
    }

    public static Optional<Type> getNative(final SourceLocation sl, final StackFrame frame, final Class<?> clazz) {
        if (clazz.isArray()) {
            final var base = getNative(sl, frame, clazz.getComponentType());
            if (base.isPresent())
                return Optional.of(PointerType.get(base.get()));
            return Optional.empty();
        }

        if (clazz == Void.class || clazz == void.class)
            return Optional.of(Type.getVoid(frame));
        if (clazz == Boolean.class || clazz == boolean.class)
            return Optional.of(Type.getInt1(frame));
        if (clazz == Byte.class || clazz == byte.class)
            return Optional.of(Type.getInt8(frame));
        if (clazz == Short.class || clazz == short.class)
            return Optional.of(Type.getInt16(frame));
        if (clazz == Integer.class || clazz == int.class)
            return Optional.of(Type.getInt32(frame));
        if (clazz == Long.class || clazz == long.class)
            return Optional.of(Type.getInt64(frame));
        if (clazz == Float.class || clazz == float.class)
            return Optional.of(Type.getFlt32(frame));
        if (clazz == Double.class || clazz == double.class)
            return Optional.of(Type.getFlt64(frame));

        if (CharSequence.class.isAssignableFrom(clazz))
            return Optional.of(Type.getInt8Ptr(frame));

        return Type.get(sl, frame, clazz.getSimpleName());
    }

    private final StackFrame frame;
    private final String id;
    private final int flags;
    private final long size;

    public Type(final StackFrame frame, final SourceLocation sl, final String id, final int flags, final long size) {
        frame.putType(sl, id, this);
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

    public Optional<PointerType> asPointer() {
        if (!isPointer())
            return Optional.empty();
        return Optional.of((PointerType) this);
    }

    public Optional<FunctionType> asFunction() {
        if (!isFunction())
            return Optional.empty();
        return Optional.of((FunctionType) this);
    }

    public Optional<StructType> asStruct() {
        if (!isStruct())
            return Optional.empty();
        return Optional.of((StructType) this);
    }

    public Optional<ArrayType> asArray() {
        if (!isArray())
            return Optional.empty();
        return Optional.of((ArrayType) this);
    }

    public Optional<Type> getPointerBase() {
        final var ty = asPointer();
        if (ty.isEmpty())
            return Optional.empty();
        return Optional.of(ty.get().getBase());
    }

    public Optional<Type> getArrayBase() {
        final var ty = asArray();
        if (ty.isEmpty())
            return Optional.empty();
        return Optional.of(ty.get().getBase());
    }

    public long getArrayLength() {
        final var ty = asArray();
        if (ty.isEmpty())
            return -1;
        return ty.get().getLength();
    }
}

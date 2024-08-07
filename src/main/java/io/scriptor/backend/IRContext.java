package io.scriptor.backend;

import static io.scriptor.type.Type.IS_FLOAT;
import static io.scriptor.type.Type.IS_INTEGER;
import static io.scriptor.type.Type.IS_VOID;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class IRContext {

    private final Map<String, Type> types = new HashMap<>();

    public IRContext() {
        new Type(this, "void", IS_VOID, 0);
        new Type(this, "i1", IS_INTEGER, 1);
        new Type(this, "i8", IS_INTEGER, 8);
        new Type(this, "i16", IS_INTEGER, 16);
        new Type(this, "i32", IS_INTEGER, 32);
        new Type(this, "i64", IS_INTEGER, 64);
        new Type(this, "f32", IS_FLOAT, 32);
        new Type(this, "f64", IS_FLOAT, 64);
    }

    /**
     * @param id the type id
     * @return true if a type with the given id does exist
     */
    public boolean existsType(final String id) {
        return types.containsKey(id);
    }

    /**
     * @param <T>
     * @param id  the type id
     * @return the type with the given id
     * @throws QScriptException if no type with the given id exists
     */
    @SuppressWarnings("unchecked")
    public <T extends Type> T getType(final String id) {
        if (!types.containsKey(id))
            throw new QScriptException("no type with id '%s'", id);
        return (T) types.get(id);
    }

    /**
     * @param <T>
     * @param id   the type id
     * @param ctor the type generator
     * @return the type with the given id
     */
    @SuppressWarnings("unchecked")
    public <T extends Type> T getType(final String id, final Supplier<T> ctor) {
        return (T) types.computeIfAbsent(id, key -> ctor.get());
    }
}

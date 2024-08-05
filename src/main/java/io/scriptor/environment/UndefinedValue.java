package io.scriptor.environment;

import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class UndefinedValue extends Value {

    public UndefinedValue(final Type type) {
        super(type);
    }

    @Override
    public boolean getBoolean(final SourceLocation location) {
        return false;
    }

    @Override
    public <T> T getJava() {
        throw null;
    }

    @Override
    public String toString() {
        return "undefined";
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (o instanceof UndefinedValue)
            return true;
        return false;
    }
}

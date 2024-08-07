package io.scriptor.frontend;

import io.scriptor.type.Type;

public record Arg(Type type, String name) {

    @Override
    public String toString() {
        if (name == null)
            return type.toString();
        return "%s %s".formatted(type, name);
    }
}

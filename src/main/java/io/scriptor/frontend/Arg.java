package io.scriptor.frontend;

import io.scriptor.type.Type;

public record Arg(Type ty, String name) {

    @Override
    public String toString() {
        if (name == null)
            return ty.toString();
        return "%s %s".formatted(ty, name);
    }
}

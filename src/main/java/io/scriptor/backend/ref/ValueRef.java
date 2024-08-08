package io.scriptor.backend.ref;

import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;

public abstract class ValueRef {

    public abstract Type getType();

    public abstract Value get();
}

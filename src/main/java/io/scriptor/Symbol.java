package io.scriptor;

import io.scriptor.backend.value.ConstValue;
import io.scriptor.type.Type;

public record Symbol(String id, Type type, ConstValue init) {
}

package io.scriptor.frontend;

import static io.scriptor.util.Util.unescape;

import io.scriptor.util.QScriptException;

public record Token(SourceLocation location, TokenType type, String value) {

    @Override
    public String toString() {
        return "%s: '%s' (%s)".formatted(location, unescape(value), type);
    }

    public long longValue() {
        return switch (type) {
            case BININT -> Long.parseLong(value, 2);
            case OCTINT -> Long.parseLong(value, 8);
            case DECINT -> Long.parseLong(value, 10);
            case HEXINT -> Long.parseLong(value, 16);
            default -> throw new QScriptException(
                    location,
                    "trying to get integer value from non-integer token: '%s' (%s)",
                    value,
                    type);
        };
    }

    public double doubleValue() {
        return Double.parseDouble(value);
    }

    public char charValue() {
        return value.charAt(0);
    }
}

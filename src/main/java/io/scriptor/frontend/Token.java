package io.scriptor.frontend;

import static io.scriptor.util.Util.unescape;

import io.scriptor.util.QScriptException;

public record Token(SourceLocation location, TokenType type, String value) {

    @Override
    public String toString() {
        return "%s: '%s' (%s)".formatted(location, unescape(value), type);
    }

    public long longValue() {
        var val = value;
        final var u = val.endsWith("u");
        if (u)
            val = val.substring(0, val.length() - 1);

        final var radix = switch (type) {
            case BININT -> 2;
            case OCTINT -> 8;
            case DECINT -> 10;
            case HEXINT -> 16;
            default -> throw new QScriptException(
                    location,
                    "trying to get integer value from non-integer token: '%s' (%s)",
                    value,
                    type);
        };

        return u
                ? Long.parseUnsignedLong(val, radix)
                : Long.parseLong(value, radix);
    }

    public double doubleValue() {
        return Double.parseDouble(value);
    }

    public char charValue() {
        return value.charAt(0);
    }
}

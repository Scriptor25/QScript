package io.scriptor.frontend;

import static io.scriptor.util.Util.unescape;

import io.scriptor.util.QScriptException;

public record Token(SourceLocation sl, TokenType ty, String val) {

    @Override
    public String toString() {
        return "%s: '%s' (%s)".formatted(sl, unescape(val), ty);
    }

    public long asLong() {
        final var u = val.endsWith("u");
        final var value = u
                ? val.substring(0, val.length() - 1)
                : val;

        final var radix = switch (ty) {
            case BININT -> 2;
            case OCTINT -> 8;
            case DECINT -> 10;
            case HEXINT -> 16;
            default -> throw new QScriptException(
                    sl,
                    "trying to get integer value from non-integer token: '%s' (%s)",
                    val,
                    ty);
        };

        return u
                ? Long.parseUnsignedLong(value, radix)
                : Long.parseLong(value, radix);
    }

    public double asDouble() {
        return Double.parseDouble(val);
    }

    public char asChar() {
        return val.charAt(0);
    }
}

package io.scriptor.frontend;

import static io.scriptor.util.Util.unescape;

public record Token(SourceLocation location, TokenType type, String value) {

    @Override
    public String toString() {
        return "%s: '%s' (%s)".formatted(location, unescape(value), type);
    }
}

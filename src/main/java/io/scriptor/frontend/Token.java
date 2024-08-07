package io.scriptor.frontend;

public record Token(SourceLocation location, TokenType type, String value) {

    public static String unescape(final String value) {
        final var builder = new StringBuilder();
        for (int i = 0; i < value.length(); ++i) {
            final var c = value.charAt(i);
            if (c >= 0x20)
                builder.append(c);
            else
                builder.append(switch (c) {
                    case 0x07 -> "\\a";
                    case 0x08 -> "\\b";
                    case 0x09 -> "\\t";
                    case 0x0A -> "\\n";
                    case 0x0B -> "\\v";
                    case 0x0C -> "\\f";
                    case 0x0D -> "\\r";
                    default -> "\\x" + Integer.toString(c, 16);
                });
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "%s: '%s' (%s)".formatted(location, unescape(value), type);
    }
}

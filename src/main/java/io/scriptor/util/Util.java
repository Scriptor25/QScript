package io.scriptor.util;

import io.scriptor.type.Type;

public class Util {

    public static boolean isOctDigit(final int c) {
        return 0x30 <= c && c <= 0x37;
    }

    public static boolean isDecDigit(final int c) {
        return 0x30 <= c && c <= 0x39;
    }

    public static boolean isHexDigit(final int c) {
        return (0x30 <= c && c <= 0x39) || (0x41 <= c && c <= 0x46) || (0x61 <= c && c <= 0x66);
    }

    public static boolean isAlpha(final int c) {
        return (0x41 <= c && c <= 0x5A) || (0x61 <= c && c <= 0x7A);
    }

    public static boolean isAlnum(final int c) {
        return isDecDigit(c) || isAlpha(c);
    }

    public static boolean isID(final int c) {
        return isAlnum(c) || c == '_';
    }

    public static boolean isOP(final int c) {
        return c == '+'
                || c == '-'
                || c == '*'
                || c == '/'
                || c == '%'
                || c == '&'
                || c == '|'
                || c == '^'
                || c == '='
                || c == '<'
                || c == '>'
                || c == '!'
                || c == '~';
    }

    public static boolean isCompOP(final int c) {
        return c == '+'
                || c == '-'
                || c == '&'
                || c == '|'
                || c == '='
                || c == '<'
                || c == '>';
    }

    public static CharSequence unescape(final CharSequence value) {
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

    private Util() {
    }

    public static Type getUnOpResult(final String operator, final Type operand) {
        return switch (operator) {
            case "++", "--", "-", "~" -> operand;
            case "!" -> Type.getInt1(operand.getContext());
            default -> null;
        };
    }
}

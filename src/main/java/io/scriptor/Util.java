package io.scriptor;

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

    private Util() {
    }
}

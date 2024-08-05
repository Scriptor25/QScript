package io.scriptor.std;

import io.scriptor.annotations.NativeType;

public class stdio {

    public static class FILE {
    }

    public static class fpos_t {
    }

    public static class va_list {
    }

    public static @NativeType("i32") int remove(@NativeType("i8*") long filename) {
    }

    public static @NativeType("i32") int rename(@NativeType("i8*") long oldname, @NativeType("i8*") long newname) {
    }

    public static @NativeType("FILE*") long tmpfile() {
    }

    public static @NativeType("i8*") long tmpname(@NativeType("i8*") long str) {
    }
}

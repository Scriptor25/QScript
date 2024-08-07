package io.scriptor.std;

import io.scriptor.annotations.NativeStruct;
import io.scriptor.annotations.NativeType;

public class stdio {

    @NativeStruct
    public static class FILE {
    }

    @NativeStruct
    public static class fpos_t {
    }

    @NativeStruct
    public static class va_list {
    }

    public static int remove(@NativeType("i8*") long filename) {
        throw new UnsupportedOperationException();
    }

    public static int rename(
            @NativeType("i8*") long oldname,
            @NativeType("i8*") long newname) {
        throw new UnsupportedOperationException();
    }

    public static @NativeType("FILE*") long tmpfile() {
        throw new UnsupportedOperationException();
    }

    public static @NativeType("i8*") long tmpname(@NativeType("i8*") long str) {
        throw new UnsupportedOperationException();
    }

    public static int fclose(@NativeType("FILE*") long stream) {
        throw new UnsupportedOperationException();
    }

    public static int fflush(@NativeType("FILE*") long stream) {
        throw new UnsupportedOperationException();
    }

    public static @NativeType("FILE*") long fopen(
            @NativeType("i8*") long filename,
            @NativeType("i8*") long mode) {
        throw new UnsupportedOperationException();
    }

    public static @NativeType("FILE*") long freopen(
            @NativeType("i8*") long filename,
            @NativeType("i8*") long mode,
            @NativeType("FILE*") long stream) {
        throw new UnsupportedOperationException();
    }

    public static void setbuf(
            @NativeType("FILE*") long stream,
            @NativeType("i8*") long buffer) {
        throw new UnsupportedOperationException();
    }

    public static int setvbuf(
            @NativeType("FILE*") long stream,
            @NativeType("i8*") long buffer,
            int mode,
            long size) {
        throw new UnsupportedOperationException();
    }
}

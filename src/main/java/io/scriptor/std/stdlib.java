package io.scriptor.std;

import io.scriptor.annotations.NativeType;
import io.scriptor.memory.Heap;

public class stdlib {

    private static Heap heap;

    public static @NativeType("void*") long calloc(long num, long size) {
        final var ptr = heap.malloc(num * size);
        heap.put(ptr, 0, new byte[(int) (num * size)]);
        return ptr;
    }

    public static void free(@NativeType("void*") long ptr) {
        heap.free(ptr);
    }

    public static @NativeType("void*") long malloc(long size) {
        return heap.malloc(size);
    }

    public static @NativeType("void*") long realloc(
            @NativeType("void*") long ptr,
            long size) {
        heap.free(ptr);
        return heap.malloc(size);
    }
}

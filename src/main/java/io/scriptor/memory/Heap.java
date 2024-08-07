package io.scriptor.memory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Heap {

    private static final byte STATE_FREE = 0;
    private static final byte STATE_USED = 1;

    private static final long HEADER_SIZE = 32;

    private final ByteBuffer buffer;

    public Heap(final int capacity) {
        this.buffer = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
        setChunk(1, buffer.capacity(), STATE_FREE, 0, 0);
    }

    private void setChunk(final long beg, final long end, final byte state, final long next, final long previous) {
        buffer.position((int) beg)
                .put(state)
                .putLong(end)
                .putLong(next)
                .putLong(previous);
    }

    private byte getState(final long chunk) {
        return buffer.get((int) chunk);
    }

    private void setState(final long chunk, final byte state) {
        buffer.put((int) chunk, state);
    }

    private long getEnd(final long chunk) {
        return buffer.getLong((int) chunk + 1);
    }

    private long getNext(final long chunk) {
        return buffer.getLong((int) chunk + 9);
    }

    private long getPrevious(final long chunk) {
        return buffer.getLong((int) chunk + 17);
    }

    private void setPrevious(final long chunk, final long previous) {
        buffer.putLong((int) chunk + 17, previous);
    }

    private long findFree(final long size) {
        long chunk = 1;
        while (chunk != 0) {
            if (getState(chunk) == STATE_FREE && getEnd(chunk) >= size)
                return chunk;
            chunk = getNext(chunk);
        }
        return 0;
    }

    private void split(final long chunk, final long mid) {
        final var end = getEnd(chunk);
        if (mid > end || mid <= chunk - HEADER_SIZE)
            return;

        final var next = getNext(chunk);
        final var previous = getPrevious(chunk);

        setChunk(chunk, mid, STATE_FREE, mid, previous);
        setChunk(mid, end, STATE_FREE, next, chunk);

        if (next != 0)
            setPrevious(next, mid);
    }

    private void mergeNext(final long chunk) {
        final var previous = getPrevious(chunk);
        final var other = getNext(chunk);
        final var next = getNext(other);
        final var end = getEnd(other);

        setChunk(chunk, end, STATE_FREE, previous, next);

        if (next != 0)
            setPrevious(next, chunk);
    }

    private void mergePrevious(final long chunk) {
        final var next = getNext(chunk);
        final var end = getEnd(chunk);
        final var other = getPrevious(chunk);
        final var previous = getPrevious(other);

        setChunk(other, end, STATE_FREE, previous, next);

        if (next != 0)
            setPrevious(next, other);
    }

    public long malloc(final long size) {
        if (size <= 0)
            return 0;

        final var chunk = findFree(size);
        if (chunk == 0)
            return 0;

        split(chunk, chunk + HEADER_SIZE + size);
        setState(chunk, STATE_USED);
        return chunk + HEADER_SIZE;
    }

    public void free(final long ptr) {
        final var chunk = ptr - HEADER_SIZE;
        setState(chunk, STATE_FREE);

        final var next = getNext(chunk);
        final var previous = getPrevious(chunk);

        if (next != 0 && getState(next) == STATE_FREE) {
            mergeNext(chunk);
        }

        if (previous != 0 && getState(previous) == STATE_FREE) {
            mergePrevious(chunk);
        }
    }

    public void dumpChunks() {
        long chunk = 1;
        while (chunk != 0) {
            final var state = getState(chunk);
            final var size = getEnd(chunk) - chunk;
            final var next = getNext(chunk);
            final var previous = getPrevious(chunk);

            final var state_str = switch (state) {
                case STATE_FREE -> "FREE";
                case STATE_USED -> "USED";
                default -> "WILD";
            };

            System.out.printf("0x%08X: %s, %d, 0x%08X, 0x%08X\n", chunk, state_str, size, next, previous);
            chunk = next;
        }
    }

    public void dumpBuffer(final long beg, final long end, final long bytes) {
        buffer.position((int) (beg < 0 ? 0 : ((beg / bytes) * bytes)));
        buffer.limit((int) ((end / bytes + 1) * bytes));
        while (buffer.hasRemaining()) {
            System.out.printf("%08X:", buffer.position());
            final var pos = buffer.position();
            for (int i = 0; i < bytes; ++i)
                System.out.printf(" %02X", buffer.get());
            System.out.print(" | ");
            buffer.position(pos);
            for (int i = 0; i < bytes; ++i) {
                final var b = buffer.get();
                System.out.printf("%c", b < 0x20 ? '.' : b);
            }
            System.out.println();
        }
        buffer.limit(buffer.capacity());
    }

    public Heap put(final long ptr, final long offset, final byte[] data) {
        buffer
                .limit((int) getEnd(ptr - HEADER_SIZE))
                .position((int) (ptr + offset))
                .put(data);
        return this;
    }
}

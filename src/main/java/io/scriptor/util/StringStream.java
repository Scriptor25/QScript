package io.scriptor.util;

import java.io.IOException;
import java.io.InputStream;

public class StringStream extends InputStream {

    private final String buffer;
    private int position;

    public StringStream(final String buffer) {
        this.buffer = buffer;
        this.position = 0;
    }

    @Override
    public int read() throws IOException {
        if (position >= buffer.length())
            return -1;
        return buffer.charAt(position++);
    }
}

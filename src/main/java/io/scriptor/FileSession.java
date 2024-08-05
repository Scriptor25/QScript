package io.scriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.scriptor.environment.Environment;
import io.scriptor.expression.Expression;
import io.scriptor.parser.Parser;

public class FileSession {

    private final Environment global;
    private final File file;

    public FileSession(final Environment global, final String filename) {
        this.global = global;
        this.file = new File(filename);
    }

    public void run() throws IOException {
        Parser.parse(global, new FileInputStream(file), file, this::callback);
    }

    private void callback(final Expression expression) {
        expression.eval(global);
    }
}

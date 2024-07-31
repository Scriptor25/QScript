package io.scriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.scriptor.environment.Environment;
import io.scriptor.expression.Expression;
import io.scriptor.parser.Parser;

public class FileSession {

    private final Environment env;
    private final File file;

    public FileSession(final Environment env, final String filename) {
        this.env = env;
        this.file = new File(filename);
    }

    public void run() throws IOException {
        Parser.parse(new FileInputStream(file), file, this::callback);
    }

    private void callback(final Expression expression) {
        final var value = expression.eval(env);
        if (value != null)
            System.out.println(value);
    }
}

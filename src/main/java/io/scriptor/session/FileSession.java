package io.scriptor.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.scriptor.frontend.Parser;
import io.scriptor.frontend.ParserConfig;
import io.scriptor.frontend.State;
import io.scriptor.frontend.expression.Expression;

public class FileSession {

    public static void create(final String filename) throws IOException {
        new FileSession(filename);
    }

    private final File file;
    private final State global = new State();

    private FileSession(final String filename) throws IOException {
        this.file = new File(filename);

        Parser.parse(new ParserConfig(
                file,
                this::callback,
                global,
                new FileInputStream(file)));
    }

    private void callback(final Expression expression) {
        System.out.println(expression);
        System.out.println();
    }
}

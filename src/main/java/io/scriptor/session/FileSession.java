package io.scriptor.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.scriptor.backend.IRContext;
import io.scriptor.frontend.Parser;
import io.scriptor.frontend.ParserConfig;
import io.scriptor.frontend.State;
import io.scriptor.frontend.expression.Expression;

public class FileSession {

    private final State global = new State();
    private final IRContext context = new IRContext();
    private final File file;

    public FileSession(final String filename) {
        this.file = new File(filename);
    }

    public void run() throws IOException {
        Parser.parse(new ParserConfig(
                context,
                file,
                this::callback,
                global,
                new FileInputStream(file)));
    }

    private void callback(final Expression expression) {
        System.out.println(expression);
    }
}

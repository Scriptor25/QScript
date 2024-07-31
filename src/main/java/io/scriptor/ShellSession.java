package io.scriptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import io.scriptor.environment.Environment;
import io.scriptor.expression.Expression;
import io.scriptor.parser.Parser;

public class ShellSession implements AutoCloseable {

    private final Environment env;
    private final BufferedReader reader;
    private final PrintStream out;
    private final PrintStream err;

    public ShellSession(final Environment env, final InputStream in, final OutputStream out, final OutputStream err) {
        this(env, in, new PrintStream(out), new PrintStream(err));
    }

    public ShellSession(final Environment env, final InputStream in, final PrintStream out, final PrintStream err) {
        this.env = env;
        reader = new BufferedReader(new InputStreamReader(in));
        this.out = out;
        this.err = err;
    }

    public void run() throws IOException {
        while (true) {
            out.print(">> ");

            final var line = reader.readLine();
            if (line == null)
                break;
            if (line.isBlank())
                continue;

            try {
                Parser.parse(new StringStream(line), null, this::callback);
            } catch (QScriptException e) {
                err.println(e);
            }
        }
    }

    private void callback(final Expression expression) {
        final var value = expression.eval(env);
        if (value != null)
            out.println(value);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}

package io.scriptor;

import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import io.scriptor.environment.Environment;
import io.scriptor.environment.UndefinedValue;
import io.scriptor.expression.Expression;
import io.scriptor.parser.Parser;

public class ShellSession implements AutoCloseable {

    private final Environment global;
    private final Terminal terminal;
    private final LineReader reader;

    public ShellSession(final Environment global)
            throws IOException {
        this.global = global;
        this.terminal = TerminalBuilder.terminal();
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
    }

    public void run() throws IOException {
        while (true) {
            final var line = reader.readLine(">> ");
            if (line == null)
                break;
            if (line.isBlank())
                continue;

            try {
                Parser.parse(global, new StringStream(line), null, this::callback);
            } catch (final QScriptException e) {
                terminal.writer().println(e.getMessage());
            }
        }
    }

    private void callback(final Expression expression) {
        final var value = expression.eval(global);
        if (!(value == null || value instanceof UndefinedValue))
            terminal.writer().println(value);
    }

    @Override
    public void close() throws IOException {
        terminal.close();
    }
}

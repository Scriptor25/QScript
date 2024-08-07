package io.scriptor;

import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import io.scriptor.backend.IRContext;
import io.scriptor.expression.Expression;
import io.scriptor.frontend.Parser;
import io.scriptor.frontend.ParserConfig;
import io.scriptor.frontend.State;

public class ShellSession implements AutoCloseable {

    private final State global = new State();
    private final Terminal terminal;
    private final LineReader reader;

    private final IRContext context = new IRContext();

    public ShellSession() throws IOException {
        this.terminal = TerminalBuilder.terminal();
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
    }

    public ShellSession run() throws IOException {
        while (true) {
            final var line = reader.readLine(">> ");
            if (line == null)
                break;
            if (line.isBlank())
                continue;

            try {
                Parser.parse(new ParserConfig(context, null, this::callback, global, new StringStream(line)));
            } catch (final QScriptException e) {
                terminal.writer().println(e.getMessage());
            }
        }

        return this;
    }

    private void callback(final Expression expression) {
        System.out.println(expression);
    }

    @Override
    public void close() throws IOException {
        terminal.close();
    }
}

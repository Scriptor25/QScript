package io.scriptor;

import java.io.IOException;

import io.scriptor.environment.Environment;

public class Main {

    public static void main(String[] args) throws IOException {
        final var global = new Environment();

        if (args.length == 0) {
            final var session = new ShellSession(global);
            session.run();
            session.close();
        } else {
            for (final var arg : args) {
                final var session = new FileSession(global, arg);
                session.run();
            }
        }

        final int result = global.call(null, "main", 0);
        System.exit(result);
    }
}

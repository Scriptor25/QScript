package io.scriptor;

import java.io.IOException;

public class Main {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            new ShellSession().run().close();
        } else {
            for (final var arg : args) {
                new FileSession(arg).run();
            }
        }
    }
}

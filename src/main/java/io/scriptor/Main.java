package io.scriptor;

import java.io.IOException;

import io.scriptor.session.FileSession;
import io.scriptor.session.ShellSession;

public class Main {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            new ShellSession()
                    .run()
                    .close();
            return;
        }

        for (final var filename : args) {
            FileSession.create(filename);
        }
    }
}

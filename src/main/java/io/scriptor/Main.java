package io.scriptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.scriptor.session.FileSession;
import io.scriptor.session.ShellSession;

public class Main {

    @SuppressWarnings("resource")
    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            new ShellSession()
                    .run()
                    .close();
            return;
        }

        String outfilename = null;
        List<String> infilenames = new ArrayList<>();

        for (int i = 0; i < args.length; ++i) {
            if ("-o".equals(args[i])) {
                outfilename = args[++i];
                continue;
            }

            infilenames.add(args[i]);
        }

        if (infilenames.isEmpty())
            throw new IllegalStateException("no input filename specified");

        if (outfilename == null)
            outfilename = "output.o";

        FileSession.create(infilenames.toArray(String[]::new), outfilename);
    }
}

package io.scriptor;

import java.io.File;
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

        String out = null;
        List<String> in = new ArrayList<>();

        for (int i = 0; i < args.length; ++i) {
            if ("-o".equals(args[i])) {
                out = args[++i];
                continue;
            }

            in.add(args[i]);
        }

        if (in.isEmpty())
            throw new IllegalStateException("no input filename specified");

        if (in.size() == 1) {
            if (out == null)
                out = "output.o";
            FileSession.create(in.get(0), out);
            return;
        }

        if (out == null)
            out = "output";

        new File(out).mkdirs();

        for (final var infilename : in) {
            var name = new File(infilename).getName();
            name = name.substring(0, name.lastIndexOf('.'));
            final var outfilename = out + "/" + name + ".o";
            FileSession.create(infilename, outfilename);
        }
    }
}

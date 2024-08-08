package io.scriptor.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.scriptor.backend.IRBuilder;
import io.scriptor.backend.IRContext;
import io.scriptor.backend.IRModule;
import io.scriptor.frontend.Parser;
import io.scriptor.frontend.ParserConfig;
import io.scriptor.frontend.State;
import io.scriptor.frontend.expression.Expression;

public class FileSession {

    private final IRContext context = new IRContext();
    private final IRBuilder builder = new IRBuilder(context);
    private final IRModule module;

    private final File file;
    private final State global = new State();

    public FileSession(final String filename) throws IOException {
        this.module = new IRModule(filename, context);
        this.file = new File(filename);

        Parser.parse(new ParserConfig(
                context,
                file,
                this::callback,
                global,
                new FileInputStream(file)));

        System.out.println("IR Module:");
        module.dump();
    }

    private void callback(final Expression expression) {
        System.out.println(expression);
        expression.genIR(builder, module);
    }
}

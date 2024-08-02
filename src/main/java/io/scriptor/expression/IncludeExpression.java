package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import io.scriptor.environment.EnvState;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.Parser;
import io.scriptor.parser.SourceLocation;

public class IncludeExpression extends Expression {

    public static IncludeExpression create(final SourceLocation location, final String filename) {
        rtassert(location != null);
        rtassert(filename != null);
        return new IncludeExpression(location, filename);
    }

    private final String filename;

    private IncludeExpression(final SourceLocation location, final String filename) {
        super(location, null);
        this.filename = filename;
    }

    public void use(final EnvState state, final List<File> parsed, final File parent, final Parser.ICallback callback)
            throws IOException {
        var file = new File(filename);
        if (!file.isAbsolute())
            file = new File(parent, filename);
        Parser.parse(state, parsed, new FileInputStream(file), file, callback);
    }

    @Override
    public Value eval(final Environment env) {
        return null;
    }

    @Override
    public String toString() {
        return "include \"%s\"".formatted(filename);
    }
}

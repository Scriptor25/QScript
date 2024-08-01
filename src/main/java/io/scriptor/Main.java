package io.scriptor;

import java.io.IOException;
import java.util.Arrays;

import io.scriptor.environment.Environment;
import io.scriptor.environment.FunctionValue;
import io.scriptor.environment.UndefinedValue;
import io.scriptor.environment.Value;
import io.scriptor.type.FunctionType;
import io.scriptor.type.PointerType;
import io.scriptor.type.Type;

public class Main {

    public static Value printf(final Environment global, final Value... args) {
        final String format = args[0].getJava();
        final var vargs = Arrays.copyOfRange(args, 1, args.length);
        final var objects = Arrays.stream(vargs).map(Value::getJava).toArray();
        System.out.printf(format, objects);
        return new UndefinedValue(Type.get("void"));
    }

    public static void main(String[] args) throws IOException {
        final var global = new Environment();

        final var type = FunctionType.get(Type.get("void"), true, PointerType.get(Type.get("i8")));
        global.defineSymbol(type, "printf", new FunctionValue(type, Main::printf));

        if (args.length == 0) {
            final var session = new ShellSession(global, System.in, System.out, System.err);
            session.run();
            session.close();
        } else {
            final var session = new FileSession(global, args[0]);
            session.run();
        }

        final int result = global.call("main", 0);
        System.exit(result);
    }
}

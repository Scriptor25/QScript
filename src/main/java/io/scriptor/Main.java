package io.scriptor;

import java.io.IOException;
import java.util.Arrays;

import io.scriptor.environment.ConstValue;
import io.scriptor.environment.Environment;
import io.scriptor.environment.FunctionValue;
import io.scriptor.environment.UndefinedValue;
import io.scriptor.environment.Value;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;

public class Main {

    public static Value printf(final Environment global, final Value... args) {
        final String format = args[0].getJava();
        final var vargs = Arrays.copyOfRange(args, 1, args.length);
        final var objects = Arrays.stream(vargs).map(Value::getJava).toArray();
        System.out.printf(format, objects);
        return new UndefinedValue(Type.getVoid());
    }

    public static Value puts(final Environment global, final Value... args) {
        final String str = args[0].getJava();
        System.out.println(str);
        return new UndefinedValue(Type.getVoid());
    }

    public static Value format(final Environment global, final Value... args) {
        final String format = args[0].getJava();
        final var vargs = Arrays.copyOfRange(args, 1, args.length);
        final var objects = Arrays.stream(vargs).map(Value::getJava).toArray();
        final var result = String.format(format, objects);
        return new ConstValue<>(Type.getInt8Ptr(), result);
    }

    public static Value exit(final Environment global, final Value... args) {
        final int status = args[0].getJava();
        System.exit(status);
        return new UndefinedValue(Type.getVoid());
    }

    public static void main(String[] args) throws IOException {
        final var global = new Environment();
        {
            final var type = FunctionType.get(Type.getVoid(), true, Type.getInt8Ptr());
            global.defineSymbol(type, "printf", new FunctionValue(type, Main::printf));
        }
        {
            final var type = FunctionType.get(Type.getVoid(), false, Type.getInt8Ptr());
            global.defineSymbol(type, "puts", new FunctionValue(type, Main::puts));
        }
        {
            final var type = FunctionType.get(Type.getInt8Ptr(), true, Type.getInt8Ptr());
            global.defineSymbol(type, "format", new FunctionValue(type, Main::format));
        }
        {
            final var type = FunctionType.get(Type.getVoid(), false, Type.getInt32());
            global.defineSymbol(type, "exit", new FunctionValue(type, Main::exit));
        }

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

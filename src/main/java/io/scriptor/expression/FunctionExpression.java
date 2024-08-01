package io.scriptor.expression;

import java.util.Arrays;

import io.scriptor.QScriptException;
import io.scriptor.environment.Environment;
import io.scriptor.environment.FunctionValue;
import io.scriptor.environment.UndefinedValue;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;

public class FunctionExpression extends Expression {

    private final String[] argnames;
    private final CompoundExpression compound;

    public FunctionExpression(
            final SourceLocation location,
            final Type promise,
            final String[] argnames,
            final CompoundExpression compound) {
        super(location, promise);

        if (promise == null)
            throw new QScriptException(location, "function expression must have a promise type");

        this.argnames = argnames;
        this.compound = compound;
    }

    @Override
    public Value eval(final Environment env) {
        final var functionType = (FunctionType) getType();
        return new FunctionValue(functionType, (global, args) -> {
            final var subenv = new Environment(env);
            for (int i = 0; i < argnames.length; ++i)
                subenv.defineSymbol(args[i].getType(), argnames[i], args[i]);
            subenv.setVarargs(Arrays.copyOfRange(args, argnames.length, args.length));
            for (final var expression : compound) {
                final var value = expression.eval(subenv);
                if (value != null && value.isReturn())
                    return value.setReturn(false);
            }
            return new UndefinedValue(functionType.getResult());
        });
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        for (int i = 0; i < argnames.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(argnames[i]);
        }
        return "$(%s) %s".formatted(builder, compound);
    }
}

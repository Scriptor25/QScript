package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

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

    public static FunctionExpression create(
            final SourceLocation location,
            final Type type,
            final String[] argnames,
            final Expression[] expressions) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(type != null, () -> new QScriptException(location, "type is null"));
        rtassert(argnames != null, () -> new QScriptException(location, "argnames is null"));
        rtassert(expressions != null, () -> new QScriptException(location, "expressions is null"));
        return new FunctionExpression(location, type, argnames, expressions);
    }

    private final String[] argnames;
    private final Expression[] expressions;

    private FunctionExpression(
            final SourceLocation location,
            final Type type,
            final String[] argnames,
            final Expression[] expressions) {
        super(location, type);

        if (type == null)
            throw new QScriptException(location, "function expression must have a promise type");

        this.argnames = argnames;
        this.expressions = expressions;
    }

    @Override
    public Value eval(final Environment env) {
        final var functionType = (FunctionType) getType();
        return new FunctionValue(this, functionType, (global, args) -> {
            final var subenv = new Environment(env);
            for (int i = 0; i < argnames.length; ++i)
                subenv.defineSymbol(getLocation(), args[i].getType(), argnames[i], args[i]);
            subenv.setVarargs(Arrays.copyOfRange(args, argnames.length, args.length));
            for (final var expression : expressions) {
                final var value = expression.eval(subenv);
                if (value != null && value.isReturn())
                    return value.setReturn(false);
            }
            return new UndefinedValue(functionType.getResult());
        });
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder()
                .append("$(");
        for (int i = 0; i < argnames.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(argnames[i]);
        }
        builder.append(") {");

        if (expressions.length == 0)
            return builder
                    .append("}")
                    .toString();
        builder.append('\n');

        final var indent = CompoundExpression.indent();
        for (final var expression : expressions)
            builder
                    .append(indent)
                    .append(expression)
                    .append('\n');

        return builder
                .append(CompoundExpression.unindent())
                .append("}")
                .toString();
    }
}

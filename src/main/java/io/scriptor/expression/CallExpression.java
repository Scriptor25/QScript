package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.FunctionType;
import io.scriptor.type.Type;

public class CallExpression extends Expression {

    public static CallExpression create(
            final SourceLocation location,
            final Type result,
            final Expression callee,
            final Expression[] args) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(result != null, () -> new QScriptException(location, "result is null"));
        rtassert(callee != null, () -> new QScriptException(location, "callee is null"));
        rtassert(args != null, () -> new QScriptException(location, "args is null"));
        final var type = (FunctionType) callee.getType();
        rtassert(type != null, () -> new QScriptException(location, "type is null"));
        rtassert((type.hasVararg() && type.getArgCount() <= args.length) || type.getArgCount() == args.length,
                () -> new QScriptException(location, "incorrect number of args"));
        return new CallExpression(location, result, callee, args);
    }

    private final Expression callee;
    private final Expression[] args;

    private CallExpression(
            final SourceLocation location,
            final Type result,
            final Expression callee,
            final Expression[] args) {
        super(location, result);
        this.callee = callee;
        this.args = args;
    }

    @Override
    public Value eval(final Environment env) {
        final var callee = this.callee.eval(env);
        final var args = new Value[this.args.length];
        for (int i = 0; i < args.length; ++i)
            args[i] = this.args[i].eval(env);
        return env.call(getLocation(), callee, args);
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }
        return "%s(%s)".formatted(callee, builder);
    }
}

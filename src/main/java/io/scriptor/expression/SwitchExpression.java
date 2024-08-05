package io.scriptor.expression;

import static io.scriptor.QScriptException.rtassert;

import java.util.Arrays;
import java.util.Map;

import io.scriptor.QScriptException;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Operation;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class SwitchExpression extends Expression {

    public static SwitchExpression create(
            final SourceLocation location,
            final Type type,
            final Expression switcher,
            final Map<Expression, Expression> cases,
            final Expression defaultCase) {
        rtassert(location != null, () -> new QScriptException(null, "location is null"));
        rtassert(type != null, () -> new QScriptException(location, "type is null"));
        rtassert(switcher != null, () -> new QScriptException(location, "switcher is null"));
        rtassert(cases != null, () -> new QScriptException(location, "cases is null"));
        rtassert(defaultCase != null, () -> new QScriptException(location, "defaultCase is null"));
        return new SwitchExpression(location, type, switcher, cases, defaultCase);
    }

    private static record Case(Value index, Expression value) {
    }

    private final Expression switcher;
    private final Case[] cases;
    private final Expression defaultCase;

    private SwitchExpression(
            final SourceLocation location,
            final Type type,
            final Expression switcher,
            final Map<Expression, Expression> cases,
            final Expression defaultCase) {
        super(location, type);
        this.switcher = switcher;
        this.cases = new Case[cases.size()];
        int i = 0;
        for (final var entry : cases.entrySet())
            this.cases[i++] = new Case(
                    entry.getKey().eval(null),
                    entry.getValue());
        this.defaultCase = defaultCase;
    }

    @Override
    public Value eval(final Environment env) {
        final var s = switcher.eval(env);
        final var opt = Arrays.stream(cases)
                .filter(c -> c.index().getNumber().longValue() == s.getNumber().longValue())
                .findFirst();
        if (opt.isEmpty())
            return defaultCase.eval(env);
        return Operation.cast(
                getLocation(),
                opt
                        .get()
                        .value()
                        .eval(env),
                getType());
    }

    @Override
    public String toString() {
        final var indent = CompoundExpression.indent();

        final var builder = new StringBuilder();
        for (final var c : cases)
            builder.append(indent).append(c.index()).append(": ").append(c.value()).append('\n');

        CompoundExpression.unindent();
        return "switch %s%n%s%sdefault: %s".formatted(switcher, builder, indent, defaultCase);
    }
}

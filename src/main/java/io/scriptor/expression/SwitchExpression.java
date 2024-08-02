package io.scriptor.expression;

import java.util.Arrays;
import java.util.Map;

import io.scriptor.environment.Environment;
import io.scriptor.environment.Operation;
import io.scriptor.environment.Value;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class SwitchExpression extends Expression {

    private static record Case(Value index, Expression value) {
    }

    private final Expression switcheroo;
    private final Case[] caseroos;
    private final Expression defaulteroo;

    public SwitchExpression(
            final SourceLocation location,
            final Type type,
            final Expression switcheroo,
            final Map<Expression, Expression> caseroos,
            final Expression defaulteroo) {
        super(location, type);
        this.switcheroo = switcheroo;
        this.caseroos = new Case[caseroos.size()];
        int i = 0;
        for (final var entry : caseroos.entrySet())
            this.caseroos[i++] = new Case(
                    entry.getKey().eval(null),
                    entry.getValue());
        this.defaulteroo = defaulteroo;
    }

    @Override
    public Value eval(final Environment env) {
        final var switcheroo = this.switcheroo.eval(env);
        final var caseroo = Arrays.stream(caseroos)
                .filter(c -> c.index().getNumber().longValue() == switcheroo.getNumber().longValue())
                .findFirst();
        if (caseroo.isEmpty())
            return defaulteroo.eval(env);
        return Operation.cast(
                caseroo
                        .get()
                        .value()
                        .eval(env),
                getType());
    }

    @Override
    public String toString() {
        final var indent = CompoundExpression.indent();

        final var builder = new StringBuilder();
        for (final var entry : caseroos)
            builder.append(indent).append(entry.index()).append(": ").append(entry.value()).append('\n');

        CompoundExpression.unindent();
        return "switch %s%n%s%sdefault: %s".formatted(switcheroo, builder, indent, defaulteroo);
    }
}

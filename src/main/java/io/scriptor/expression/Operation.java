package io.scriptor.expression;

import io.scriptor.QScriptException;
import io.scriptor.environment.ConstValue;
import io.scriptor.environment.Environment;
import io.scriptor.environment.Value;
import io.scriptor.type.Type;

public class Operation {

    public static void check(final Value lhs, final Value rhs) {
        if (lhs == null)
            throw new QScriptException();
        if (rhs == null)
            throw new QScriptException();
        if (lhs.getType() != rhs.getType())
            throw new QScriptException();
    }

    public static Value assign(final Environment env, final Expression assignee, final Value value) {
        if (assignee instanceof IDExpression e)
            return env.getSymbol(e.getId()).setValue(value);

        throw new QScriptException();
    }

    public static Value lt(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() < rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() < rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() < rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() < rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() < rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() < rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value gt(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() > rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() > rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() > rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() > rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() > rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() > rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value le(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() <= rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() <= rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() <= rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() <= rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() <= rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() <= rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value ge(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() >= rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() >= rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() >= rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() >= rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() >= rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() >= rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value eq(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() == rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() == rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() == rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() == rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() == rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() == rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value ne(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() != rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() != rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() != rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() != rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() != rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() != rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value add(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() + rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() + rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() + rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() + rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() + rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() + rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value sub(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() - rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() - rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() - rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() - rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() - rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() - rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value mul(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() * rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() * rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() * rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() * rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() * rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() * rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value div(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() / rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() / rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() / rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() / rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() / rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() / rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value rem(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.get("i8"))
            return new ConstValue<>(type, lhs.getNumber().byteValue() % rhs.getNumber().byteValue());
        if (type == Type.get("i16"))
            return new ConstValue<>(type, lhs.getNumber().shortValue() % rhs.getNumber().shortValue());
        if (type == Type.get("i32"))
            return new ConstValue<>(type, lhs.getNumber().intValue() % rhs.getNumber().intValue());
        if (type == Type.get("i64"))
            return new ConstValue<>(type, lhs.getNumber().longValue() % rhs.getNumber().byteValue());
        if (type == Type.get("f32"))
            return new ConstValue<>(type, lhs.getNumber().floatValue() % rhs.getNumber().floatValue());
        if (type == Type.get("f64"))
            return new ConstValue<>(type, lhs.getNumber().doubleValue() % rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    private Operation() {
    }
}

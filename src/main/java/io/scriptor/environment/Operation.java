package io.scriptor.environment;

import io.scriptor.QScriptException;
import io.scriptor.expression.Expression;
import io.scriptor.expression.IDExpression;
import io.scriptor.type.Type;

public class Operation {

    public static Value cast(final Value value, final Type type) {
        final var vtype = value.getType();
        if (vtype == type)
            return value;
        if (type.isInt())
            return toicast(value, type);
        if (type.isFloat())
            return tofcast(value, type);
        if (type.isPointer())
            return topcast(value, type);
        throw new QScriptException();
    }

    public static Value toicast(final Value value, final Type type) {
        if (type == Type.getInt1())
            return new ConstValue<>(type, value.getBoolean());
        if (type == Type.getInt8())
            return new ConstValue<>(type, value.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, value.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, value.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, value.getNumber().longValue());
        throw new QScriptException();
    }

    public static Value tofcast(final Value value, final Type type) {
        if (type == Type.getFlt32())
            return new ConstValue<>(type, value.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, value.getNumber().doubleValue());
        throw new QScriptException();
    }

    public static Value topcast(final Value value, final Type type) {
        throw new QScriptException();
    }

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
            return env.getSymbol(e.toString()).setValue(value);

        throw new QScriptException();
    }

    public static Value lt(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() < rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() < rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() < rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() < rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() < rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() < rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value gt(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() > rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() > rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() > rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() > rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() > rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() > rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value le(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() <= rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() <= rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() <= rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() <= rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() <= rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() <= rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value ge(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() >= rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() >= rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() >= rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() >= rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() >= rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() >= rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value eq(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() == rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() == rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() == rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() == rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() == rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() == rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value ne(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() != rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() != rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() != rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() != rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() != rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() != rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value add(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() + rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() + rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() + rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() + rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() + rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() + rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value sub(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() - rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() - rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() - rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() - rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() - rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() - rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value mul(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() * rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() * rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() * rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() * rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() * rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() * rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value div(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() / rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() / rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() / rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() / rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() / rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() / rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    public static Value rem(final Value lhs, final Value rhs) {
        check(lhs, rhs);
        final var type = lhs.getType();

        if (type == Type.getInt8())
            return new ConstValue<>(type, lhs.getNumber().byteValue() % rhs.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, lhs.getNumber().shortValue() % rhs.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, lhs.getNumber().intValue() % rhs.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, lhs.getNumber().longValue() % rhs.getNumber().byteValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, lhs.getNumber().floatValue() % rhs.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, lhs.getNumber().doubleValue() % rhs.getNumber().doubleValue());

        throw new QScriptException();
    }

    private Operation() {
    }
}

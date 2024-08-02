package io.scriptor.environment;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
import io.scriptor.expression.Expression;
import io.scriptor.expression.IDExpression;
import io.scriptor.type.Type;

public class Operation {

    public static Value cast(final Value value, final Type type) {
        rtassert(value != null);
        final var vtype = value.getType();
        rtassert(vtype != null);
        if (vtype == type)
            return value;
        if (type.isInt())
            return toicast(value, type);
        if (type.isFloat())
            return tofcast(value, type);
        if (type.isPointer())
            return topcast(value, type);
        throw new QScriptException("no cast from %s to %s", vtype, type);
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
        throw new QScriptException("no cast from %s type to any int type", type);
    }

    public static Value tofcast(final Value value, final Type type) {
        if (type == Type.getFlt32())
            return new ConstValue<>(type, value.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, value.getNumber().doubleValue());
        throw new QScriptException("no cast from %s type to any float type", type);
    }

    public static Value topcast(final Value value, final Type type) {
        return new ConstValue<>(type, value.getNumber().longValue());
    }

    public static void check(final Value lhs, final Value rhs) {
        rtassert(lhs != null);
        rtassert(rhs != null);
        rtassert(lhs.getType() != null);
        rtassert(rhs.getType() != null);
        rtassert(lhs.getType() == rhs.getType());
    }

    public static Value assign(final Environment env, final Expression assignee, final Value value) {
        if (assignee instanceof IDExpression e)
            return env.getSymbol(e.toString()).setValue(value);
        throw new QScriptException("cannot assign to non-id expression");
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

        return null;
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

        return null;
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

        return null;
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

        return null;
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

        return null;
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

        return null;
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

        return null;
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

        return null;
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

        return null;
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

        return null;
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

        return null;
    }

    public static Value inc(final Value value) {
        rtassert(value != null);
        final var type = value.getType();
        rtassert(type != null);

        if (type.isInt() || type.isFloat())
            return add(value, new ConstValue<>(type, 1));

        return null;
    }

    public static Value dec(final Value value) {
        rtassert(value != null);
        final var type = value.getType();
        rtassert(type != null);

        if (type.isInt() || type.isFloat())
            return sub(value, new ConstValue<>(type, 1));

        return null;
    }

    public static Value neg(final Value value) {
        rtassert(value != null);
        final var type = value.getType();
        rtassert(type != null);

        if (type == Type.getInt8())
            return new ConstValue<>(type, -value.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, -value.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, -value.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, -value.getNumber().longValue());
        if (type == Type.getFlt32())
            return new ConstValue<>(type, -value.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, -value.getNumber().doubleValue());

        return null;
    }

    private Operation() {
    }
}

package io.scriptor.environment;

import static io.scriptor.QScriptException.rtassert;

import io.scriptor.QScriptException;
import io.scriptor.expression.Expression;
import io.scriptor.expression.IDExpression;
import io.scriptor.parser.SourceLocation;
import io.scriptor.type.Type;

public class Operation {

    public static boolean isAssigning(final String op) {
        if (op == null || !op.contains("="))
            return false;
        return switch (op) {
            case "==", "!=", "<=", ">=" -> false;
            default -> true;
        };
    }

    public static Value cast(final SourceLocation location, final Value value, final Type type) {
        rtassert(value != null, () -> new QScriptException(location, "value is null"));
        rtassert(type != null, () -> new QScriptException(location, "type is null"));
        final var vtype = value.getType();
        rtassert(vtype != null, () -> new QScriptException(location, "vtype is null"));
        if (vtype == type)
            return value;
        if (type.isInt())
            return toicast(location, value, type);
        if (type.isFloat())
            return tofcast(location, value, type);
        if (type.isPointer())
            return topcast(location, value, type);
        throw new QScriptException(location, "no cast from %s to %s", vtype, type);
    }

    public static Value toicast(final SourceLocation location, final Value value, final Type type) {
        if (type == Type.getInt1())
            return new ConstValue<>(type, value.getBoolean(location));
        if (type == Type.getInt8())
            return new ConstValue<>(type, value.getNumber().byteValue());
        if (type == Type.getInt16())
            return new ConstValue<>(type, value.getNumber().shortValue());
        if (type == Type.getInt32())
            return new ConstValue<>(type, value.getNumber().intValue());
        if (type == Type.getInt64())
            return new ConstValue<>(type, value.getNumber().longValue());
        throw new QScriptException(location, "no cast from %s type to any int type", type);
    }

    public static Value tofcast(final SourceLocation location, final Value value, final Type type) {
        if (type == Type.getFlt32())
            return new ConstValue<>(type, value.getNumber().floatValue());
        if (type == Type.getFlt64())
            return new ConstValue<>(type, value.getNumber().doubleValue());
        throw new QScriptException(location, "no cast from %s type to any float type", type);
    }

    public static Value topcast(final SourceLocation location, final Value value, final Type type) {
        return new ConstValue<>(type, value.getNumber().longValue());
    }

    public static void check(final SourceLocation location, final Value lhs, final Value rhs) {
        rtassert(lhs != null, () -> new QScriptException(location, "lhs is null"));
        rtassert(rhs != null, () -> new QScriptException(location, "rhs is null"));
        rtassert(lhs.getType() != null, () -> new QScriptException(location, "lhs type is null"));
        rtassert(rhs.getType() != null, () -> new QScriptException(location, "rhs type is null"));
        rtassert(lhs.getType() == rhs.getType(),
                () -> new QScriptException(location, "lhs type is not equal to rhs type"));
    }

    public static void checkUnary(final SourceLocation location, final Value value) {
        rtassert(value != null, () -> new QScriptException(location, "value is null"));
        rtassert(value.getType() != null, () -> new QScriptException(location, "value type is null"));
    }

    public static Value assign(
            final SourceLocation location,
            final Environment env,
            final Expression assignee,
            final Value value) {
        if (assignee instanceof IDExpression e)
            return env.getSymbol(location, e.toString()).setValue(location, value);
        throw new QScriptException(location, "cannot assign to non-id expression");
    }

    public static Value lt(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value gt(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value le(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value ge(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value eq(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value ne(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value add(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value sub(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value mul(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value div(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value rem(final SourceLocation location, final Value lhs, final Value rhs) {
        check(location, lhs, rhs);
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

    public static Value inc(final SourceLocation location, final Value value) {
        checkUnary(location, value);
        final var type = value.getType();

        if (type.isInt() || type.isFloat())
            return add(location, value, new ConstValue<>(type, 1));

        return null;
    }

    public static Value dec(final SourceLocation location, final Value value) {
        checkUnary(location, value);
        final var type = value.getType();

        if (type.isInt() || type.isFloat())
            return sub(location, value, new ConstValue<>(type, 1));

        return null;
    }

    public static Value neg(final SourceLocation location, final Value value) {
        checkUnary(location, value);
        final var type = value.getType();

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

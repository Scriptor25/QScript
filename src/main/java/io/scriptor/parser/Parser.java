package io.scriptor.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.scriptor.QScriptException;
import io.scriptor.expression.BinaryExpression;
import io.scriptor.expression.CallExpression;
import io.scriptor.expression.CompoundExpression;
import io.scriptor.expression.DefineExpression;
import io.scriptor.expression.Expression;
import io.scriptor.expression.FunctionExpression;
import io.scriptor.expression.IDExpression;
import io.scriptor.expression.IntExpression;
import io.scriptor.expression.ReturnExpression;
import io.scriptor.expression.StringExpression;
import io.scriptor.expression.UnaryExpression;
import io.scriptor.expression.WhileExpression;
import io.scriptor.type.FunctionType;
import io.scriptor.type.PointerType;
import io.scriptor.type.Type;

public class Parser {

    @FunctionalInterface
    public static interface ICallback {

        void call(final Expression expression);
    }

    private static final Map<String, Integer> PRECEDENCES = new HashMap<>();
    static {
        PRECEDENCES.clear();
        PRECEDENCES.put("=", 0);
        PRECEDENCES.put("<<=", 0);
        PRECEDENCES.put(">>=", 0);
        PRECEDENCES.put(">>>=", 0);
        PRECEDENCES.put("+=", 0);
        PRECEDENCES.put("-=", 0);
        PRECEDENCES.put("*=", 0);
        PRECEDENCES.put("/=", 0);
        PRECEDENCES.put("%=", 0);
        PRECEDENCES.put("&=", 0);
        PRECEDENCES.put("|=", 0);
        PRECEDENCES.put("^=", 0);
        PRECEDENCES.put("&&", 1);
        PRECEDENCES.put("||", 1);
        PRECEDENCES.put("<", 2);
        PRECEDENCES.put(">", 2);
        PRECEDENCES.put("<=", 2);
        PRECEDENCES.put(">=", 2);
        PRECEDENCES.put("==", 2);
        PRECEDENCES.put("&", 3);
        PRECEDENCES.put("|", 3);
        PRECEDENCES.put("^", 3);
        PRECEDENCES.put("<<", 4);
        PRECEDENCES.put(">>", 4);
        PRECEDENCES.put(">>>", 4);
        PRECEDENCES.put("+", 5);
        PRECEDENCES.put("-", 5);
        PRECEDENCES.put("*", 6);
        PRECEDENCES.put("/", 6);
        PRECEDENCES.put("%", 6);
    }

    private static boolean isOctDigit(final int c) {
        return 0x30 <= c && c <= 0x37;
    }

    private static boolean isDecDigit(final int c) {
        return 0x30 <= c && c <= 0x39;
    }

    // private static boolean isHexDigit(final int c) {
    // return (0x30 <= c && c <= 0x39) || (0x41 <= c && c <= 0x46) || (0x61 <= c &&
    // c <= 0x66);
    // }

    private static boolean isAlpha(final int c) {
        return (0x41 <= c && c <= 0x5A) || (0x61 <= c && c <= 0x7A);
    }

    private static boolean isAlnum(final int c) {
        return isDecDigit(c) || isAlpha(c);
    }

    private static boolean isID(final int c) {
        return isAlnum(c) || c == '_';
    }

    private static boolean isOP(final int c) {
        return c == '+'
                || c == '-'
                || c == '*'
                || c == '/'
                || c == '%'
                || c == '&'
                || c == '|'
                || c == '^'
                || c == '='
                || c == '<'
                || c == '>'
                || c == '!'
                || c == '~';
    }

    private static boolean isCompOP(final int c) {
        return c == '+'
                || c == '-'
                || c == '&'
                || c == '|'
                || c == '='
                || c == '<'
                || c == '>';
    }

    public static void parse(
            final InputStream stream,
            final File file,
            final ICallback callback)
            throws IOException {
        final var parser = new Parser(stream, file);

        parser.next();
        while (!parser.atEOF()) {
            final var expression = parser.nextExpression(null);
            callback.call(expression);
        }

        stream.close();
    }

    private final InputStream stream;
    private final File file;

    private Token token;
    private int chr = -1;
    private int row = 1;
    private int column = 0;

    private Type currentResult;

    private Parser(final InputStream stream, final File file) {
        this.stream = stream;
        this.file = file;
    }

    private int get() throws IOException {
        ++column;
        return stream.read();
    }

    private void escape() throws IOException {
        if (chr != '\\')
            return;

        chr = get();
        switch (chr) {
            case 'a' -> chr = 0x07;
            case 'b' -> chr = 0x08;
            case 't' -> chr = 0x09;
            case 'n' -> chr = 0x0A;
            case 'v' -> chr = 0x0B;
            case 'f' -> chr = 0x0C;
            case 'r' -> chr = 0x0D;
            case 'x' -> {
                chr = get();
                String value = "";
                value += (char) chr;
                chr = get();
                value += (char) chr;
                chr = Integer.parseInt(value, 16);
            }
            default -> {
                if (isOctDigit(chr)) {
                    String value = "";
                    value += (char) chr;
                    chr = get();
                    value += (char) chr;
                    chr = get();
                    value += (char) chr;
                    chr = Integer.parseInt(value, 8);
                }
            }
        }
    }

    private void newline() {
        column = 0;
        ++row;
    }

    private Token next() throws IOException {
        token = nextToken();
        return token;
    }

    private Token nextToken() throws IOException {
        if (chr < 0)
            chr = get();

        while (0x00 <= chr && chr <= 0x20) {
            if (chr == '\n')
                newline();
            chr = get();
        }

        var mode = ParserMode.NORMAL;
        var value = new StringBuilder();
        var isfloat = false;
        SourceLocation loc = null;

        while (chr >= 0 || mode != ParserMode.NORMAL) {
            switch (mode) {
                case NORMAL:
                    switch (chr) {
                        case '#':
                            mode = ParserMode.COMMENT;
                            break;

                        case '"':
                            loc = new SourceLocation(file, row, column);
                            mode = ParserMode.STRING;
                            break;

                        case '\'':
                            loc = new SourceLocation(file, row, column);
                            mode = ParserMode.CHAR;
                            break;

                        default:
                            if (chr <= 0x20) {
                                if (chr == '\n')
                                    newline();
                                break;
                            }

                            if (isDecDigit(chr)) {
                                loc = new SourceLocation(file, row, column);
                                mode = ParserMode.NUMBER;
                                value.append((char) chr);
                                break;
                            }

                            if (isID(chr)) {
                                loc = new SourceLocation(file, row, column);
                                mode = ParserMode.ID;
                                value.append((char) chr);
                                break;
                            }

                            if (isOP(chr)) {
                                loc = new SourceLocation(file, row, column);
                                mode = ParserMode.OPERATOR;
                                value.append((char) chr);
                                break;
                            }

                            loc = new SourceLocation(file, row, column);
                            value.append((char) chr);
                            chr = get();
                            return new Token(loc, TokenType.OTHER, value.toString());
                    }
                    break;

                case COMMENT:
                    if (chr == '#')
                        mode = ParserMode.NORMAL;
                    break;

                case STRING:
                    if (chr == '"') {
                        chr = get();
                        return new Token(loc, TokenType.STRING, value.toString());
                    }
                    if (chr == '\\')
                        escape();
                    value.append((char) chr);
                    break;

                case CHAR:
                    if (chr == '\'') {
                        chr = get();
                        return new Token(loc, TokenType.CHAR, value.toString());
                    }
                    if (chr == '\\')
                        escape();
                    value.append((char) chr);
                    break;

                case NUMBER:
                    if (chr == '.') {
                        isfloat = true;
                        value.append((char) chr);
                        break;
                    }
                    if (!isDecDigit(chr))
                        return new Token(loc, isfloat ? TokenType.FLOAT : TokenType.INT, value.toString());
                    value.append((char) chr);
                    break;

                case ID:
                    if (!isID(chr))
                        return new Token(loc, TokenType.ID, value.toString());
                    value.append((char) chr);
                    break;

                case OPERATOR:
                    if (!isCompOP(chr))
                        return new Token(loc, TokenType.OPERATOR, value.toString());
                    value.append((char) chr);
                    break;

                default:
                    break;
            }

            chr = get();
        }

        return null;
    }

    private boolean atEOF() {
        return token == null;
    }

    private boolean at(final TokenType type) {
        return token != null && token.type() == type;
    }

    private boolean at(final String value) {
        return token != null && token.value().equals(value);
    }

    private Token expect(final TokenType type) throws IOException {
        if (at(type))
            return skip();
        if (atEOF())
            throw new QScriptException(new SourceLocation(file, row, column), "expected %s, at EOF", type);
        throw new QScriptException(
                token.location(),
                "expected %s, at '%s' (%s)",
                type,
                token.value(),
                token.type());
    }

    private Token expect(final String value) throws IOException {
        if (at(value))
            return skip();
        if (atEOF())
            throw new QScriptException(new SourceLocation(file, row, column), "expected '%s', at EOF", value);
        throw new QScriptException(
                token.location(),
                "expected '%s', at '%s' (%s)",
                value,
                token.value(),
                token.type());
    }

    private Token skip() throws IOException {
        final var tk = token;
        next();
        return tk;
    }

    private boolean nextIfAt(final String value) throws IOException {
        if (at(value)) {
            next();
            return true;
        }
        return false;
    }

    private Type nextType() throws IOException {
        final var base = expect(TokenType.ID).value();
        return nextType(Type.get(base));
    }

    private Type nextType(final Type base) throws IOException {
        if (nextIfAt("*"))
            return nextType(PointerType.get(base));

        if (nextIfAt("(")) {
            var vararg = false;
            final List<Type> args = new ArrayList<>();
            while (!nextIfAt(")")) {
                if (nextIfAt("?")) {
                    vararg = true;
                    expect(")");
                    break;
                }

                final var arg = nextType();
                args.add(arg);
                if (!at(")"))
                    expect(",");
            }
            return nextType(FunctionType.get(base, vararg, args.toArray(Type[]::new)));
        }

        return base;
    }

    private Expression nextExpression(final Type promise) throws IOException {
        if (at("def"))
            return nextDefine();

        if (at("while"))
            return nextWhile();

        if (at("return"))
            return nextReturn();

        if (at("{"))
            return nextCompound();

        return nextBinary(promise);
    }

    private DefineExpression nextDefine() throws IOException {
        final var loc = expect("def").location();

        final var type = nextType();
        final var id = expect(TokenType.ID).value();

        if (!nextIfAt("="))
            return new DefineExpression(loc, type, id);

        currentResult = type.isFunction() ? ((FunctionType) type).getResult() : null;
        final var init = nextExpression(type);
        currentResult = null;

        return new DefineExpression(loc, type, id, init);
    }

    private Expression nextWhile() throws IOException {
        final var loc = expect("while").location();

        final var condition = nextExpression(null);
        final var loop = nextExpression(null);

        return new WhileExpression(loc, condition, loop);
    }

    private ReturnExpression nextReturn() throws IOException {
        final var loc = expect("return").location();

        final var expression = nextExpression(currentResult);
        return new ReturnExpression(loc, expression);
    }

    private CompoundExpression nextCompound() throws IOException {
        final var loc = expect("{").location();

        final List<Expression> expressions = new ArrayList<>();
        while (!nextIfAt("}")) {
            final var expression = nextExpression(null);
            expressions.add(expression);
        }
        return new CompoundExpression(loc, expressions.toArray(Expression[]::new));
    }

    private Expression nextBinary(final Type promise) throws IOException {
        return nextBinary(promise, nextCall(promise), 0);
    }

    private Expression nextBinary(final Type promise, Expression lhs, final int minPrecedence) throws IOException {
        while (at(TokenType.OPERATOR) && PRECEDENCES.get(token.value()) >= minPrecedence) {
            final var tk = skip();
            final var loc = tk.location();
            final var operator = tk.value();
            final var precedence = PRECEDENCES.get(operator);
            var rhs = nextCall(promise);
            while (at(TokenType.OPERATOR) && PRECEDENCES.get(token.value()) > precedence) {
                final var laPrecedence = PRECEDENCES.get(token.value());
                rhs = nextBinary(promise, rhs, precedence + (laPrecedence > precedence ? 1 : 0));
            }
            lhs = new BinaryExpression(loc, operator, lhs, rhs);
        }
        return lhs;
    }

    private Expression nextCall(final Type promise) throws IOException {
        var expr = nextUnary(promise);

        while (at("(")) {
            final var loc = skip().location();

            final List<Expression> args = new ArrayList<>();
            while (!nextIfAt(")")) {
                final var arg = nextExpression(null);
                args.add(arg);
                if (!at(")"))
                    expect(",");
            }

            expr = new CallExpression(loc, promise, expr, args.toArray(Expression[]::new));
        }

        return expr;
    }

    private Expression nextUnary(final Type promise) throws IOException {
        var expr = nextPrimary(promise);

        if (at("++") || at("--")) {
            final var tk = skip();
            final var loc = tk.location();
            final var operator = tk.value();
            expr = new UnaryExpression(loc, operator, expr);
        }

        return expr;
    }

    private Expression nextPrimary(final Type promise) throws IOException {
        if (atEOF())
            throw new QScriptException(new SourceLocation(file, row, column), "reached eof");

        final var loc = token.location();

        if (nextIfAt("$")) {
            expect("(");
            final List<String> argnames = new ArrayList<>();
            while (!nextIfAt(")")) {
                final var argname = expect(TokenType.ID).value();
                argnames.add(argname);
                if (!at(")"))
                    expect(",");
            }
            final var compound = nextCompound();
            return new FunctionExpression(loc, promise, argnames.toArray(String[]::new), compound);
        }

        if (nextIfAt("(")) {
            final var expression = nextExpression(promise);
            expect(")");
            return expression;
        }

        if (at(TokenType.ID))
            return new IDExpression(loc, promise, skip().value());

        if (at(TokenType.INT))
            return new IntExpression(loc, promise, Integer.valueOf(skip().value()));

        if (at(TokenType.STRING))
            return new StringExpression(loc, promise, skip().value());

        throw new QScriptException(loc, "unhandled token '%s' (%s)", token.value(), token.type());
    }
}

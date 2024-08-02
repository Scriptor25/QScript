package io.scriptor.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import io.scriptor.QScriptException;
import io.scriptor.environment.EnvState;
import io.scriptor.environment.Environment;
import io.scriptor.expression.BinaryExpression;
import io.scriptor.expression.CallExpression;
import io.scriptor.expression.CompoundExpression;
import io.scriptor.expression.DefineExpression;
import io.scriptor.expression.Expression;
import io.scriptor.expression.FloatExpression;
import io.scriptor.expression.FunctionExpression;
import io.scriptor.expression.IDExpression;
import io.scriptor.expression.IncludeExpression;
import io.scriptor.expression.IntExpression;
import io.scriptor.expression.ReturnExpression;
import io.scriptor.expression.StringExpression;
import io.scriptor.expression.SwitchExpression;
import io.scriptor.expression.UnaryExpression;
import io.scriptor.expression.UseExpression;
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
            final Environment global,
            final InputStream stream,
            final File file,
            final ICallback callback)
            throws IOException {
        parse(global.getState(), new Vector<>(), stream, file, callback);
    }

    public static void parse(
            final EnvState state,
            final List<File> parsed,
            final InputStream stream,
            final File file,
            final ICallback callback)
            throws IOException {
        if (parsed.contains(file))
            return;
        parsed.add(file);

        final var parser = new Parser(state, parsed, stream, file, callback);

        parser.next();
        while (!parser.atEOF()) {
            final var expression = parser.nextExpression(null);
            callback.call(expression);
        }

        stream.close();
    }

    private final List<File> parsed;
    private final InputStream stream;
    private final File file;
    private final ICallback callback;

    private Token token;
    private int chr = -1;
    private int row = 1;
    private int column = 0;

    private final Stack<EnvState> stack = new Stack<>();
    private Type currentResult;

    private Parser(
            final EnvState state,
            final List<File> parsed,
            final InputStream stream,
            final File file,
            final ICallback callback) {
        this.parsed = parsed;
        this.stream = stream;
        this.file = file;
        this.callback = callback;
        stack.push(state);
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

    private Expression nextExpression(final Type expected) throws IOException {
        if (at("use"))
            return nextUse();

        if (at("include"))
            return nextInclude();

        if (at("def"))
            return nextDefine();

        if (at("while"))
            return nextWhile();

        if (at("return"))
            return nextReturn();

        if (at("switch"))
            return nextSwitch(expected);

        if (at("{"))
            return nextCompound();

        return nextBinary(expected);
    }

    private DefineExpression nextDefine() throws IOException {
        final var loc = expect("def").location();

        final var type = nextType();
        final var id = expect(TokenType.ID).value();

        stack.peek().declareSymbol(type, id);

        if (!nextIfAt("="))
            return DefineExpression.create(loc, stack.peek(), type, id);

        final var init = nextExpression(type);
        return DefineExpression.create(loc, stack.peek(), type, id, init);
    }

    private UseExpression nextUse() throws IOException {
        final var loc = expect("use").location();
        final var id = expect(TokenType.ID).value();
        expect("as");
        final var type = nextType();
        final var expr = UseExpression.create(loc, id, type);
        expr.use();
        return expr;
    }

    private IncludeExpression nextInclude() throws IOException {
        final var loc = expect("include").location();
        final var filename = expect(TokenType.STRING).value();
        final var expr = IncludeExpression.create(loc, filename);
        expr.use(stack.peek(), parsed, file == null ? null : file.getParentFile(), callback);
        return expr;
    }

    private Expression nextWhile() throws IOException {
        final var loc = expect("while").location();

        final var condition = nextExpression(Type.getInt1());
        final var loop = nextExpression(null);

        return WhileExpression.create(loc, condition, loop);
    }

    private ReturnExpression nextReturn() throws IOException {
        final var loc = expect("return").location();

        if (nextIfAt("void"))
            return ReturnExpression.create(loc, currentResult);

        final var expression = nextExpression(currentResult);
        return ReturnExpression.create(loc, currentResult, expression);
    }

    private SwitchExpression nextSwitch(final Type expected) throws IOException {
        final var loc = expect("switch").location();

        final var switcher = nextExpression(Type.getInt64());
        if (!switcher.getType().isInt())
            throw new QScriptException(
                    switcher.getLocation(),
                    "switch must be of type integer, but is of type %s",
                    switcher.getType());

        final Map<Expression, Expression> cases = new HashMap<>();
        while (!nextIfAt("default")) {
            final var c = nextExpression(switcher.getType());
            if (!c.getType().isInt())
                throw new QScriptException(
                        c.getLocation(),
                        "case must be of type integer, but is of type %s",
                        c.getType());
            expect(":");
            final var expression = nextExpression(expected);
            cases.put(c, expression);
        }

        expect(":");
        final var defaultCase = nextExpression(expected);

        return SwitchExpression.create(loc, expected, switcher, cases, defaultCase);
    }

    private CompoundExpression nextCompound() throws IOException {
        final var loc = expect("{").location();
        final List<Expression> expressions = new ArrayList<>();

        stack.push(new EnvState(stack.peek()));
        while (!nextIfAt("}")) {
            final var expression = nextExpression(null);
            expressions.add(expression);
        }
        stack.pop();

        return CompoundExpression.create(loc, expressions.toArray(Expression[]::new));
    }

    private Expression nextBinary(final Type expected) throws IOException {
        return nextBinary(expected, nextCall(expected), 0);
    }

    private Expression nextBinary(final Type expected, Expression lhs, final int minPrecedence) throws IOException {
        while (at(TokenType.OPERATOR) && PRECEDENCES.get(token.value()) >= minPrecedence) {
            final var tk = skip();
            final var loc = tk.location();
            final var operator = tk.value();
            final var precedence = PRECEDENCES.get(operator);
            var rhs = nextCall(expected);
            while (at(TokenType.OPERATOR) && PRECEDENCES.get(token.value()) > precedence) {
                final var laPrecedence = PRECEDENCES.get(token.value());
                rhs = nextBinary(expected, rhs, precedence + (laPrecedence > precedence ? 1 : 0));
            }
            lhs = BinaryExpression.create(loc, operator, lhs, rhs);
        }
        return lhs;
    }

    private Expression nextCall(final Type expected) throws IOException {
        var expr = nextUnary(expected);

        while (at("(")) {
            final var loc = skip().location();

            final FunctionType calleeType = (FunctionType) expr.getType();

            final List<Expression> args = new ArrayList<>();
            while (!nextIfAt(")")) {
                final var arg = nextExpression(calleeType.getArg(args.size()));
                args.add(arg);
                if (!at(")"))
                    expect(",");
            }

            expr = CallExpression.create(loc, calleeType.getResult(), expr, args.toArray(Expression[]::new));
        }

        return expr;
    }

    private Expression nextUnary(final Type expected) throws IOException {
        var expr = nextPrimary(expected);

        if (at("++") || at("--")) {
            final var tk = skip();
            final var loc = tk.location();
            final var operator = tk.value();
            expr = UnaryExpression.createR(loc, operator, expr);
        }

        return expr;
    }

    private Expression nextPrimary(final Type expected) throws IOException {
        if (atEOF())
            throw new QScriptException(new SourceLocation(file, row, column), "reached eof");

        final var loc = token.location();

        if (nextIfAt("$")) {
            expect("(");
            final var type = (FunctionType) expected;

            stack.push(new EnvState(stack.peek()));

            final List<String> argnames = new ArrayList<>();
            while (!nextIfAt(")")) {
                final var argname = expect(TokenType.ID).value();
                stack.peek().declareSymbol(type.getArg(argnames.size()), argname);
                argnames.add(argname);
                if (!at(")"))
                    expect(",");
            }

            final List<Expression> expressions = new ArrayList<>();
            final var bkpResult = currentResult;
            currentResult = type.getResult();
            expect("{");
            while (!nextIfAt("}")) {
                final var expression = nextExpression(null);
                expressions.add(expression);
            }
            currentResult = bkpResult;

            stack.pop();

            return FunctionExpression.create(
                    loc,
                    type,
                    argnames.toArray(String[]::new),
                    expressions.toArray(Expression[]::new));
        }

        if (nextIfAt("(")) {
            final var expression = nextExpression(expected);
            expect(")");
            return expression;
        }

        if (at(TokenType.ID)) {
            final var id = skip().value();
            return IDExpression.create(loc, stack.peek(), id);
        }

        if (at(TokenType.INT))
            return IntExpression.create(loc, Long.valueOf(skip().value()));

        if (at(TokenType.FLOAT))
            return FloatExpression.create(loc, Double.valueOf(skip().value()));

        if (at(TokenType.STRING))
            return StringExpression.create(loc, skip().value());

        if (at(TokenType.OPERATOR)) {
            final var operator = skip().value();
            final var operand = nextExpression(expected);
            return UnaryExpression.createL(loc, operator, operand);
        }

        throw new QScriptException(loc, "unhandled token '%s' (%s)", token.value(), token.type());
    }
}

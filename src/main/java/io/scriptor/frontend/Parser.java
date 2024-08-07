package io.scriptor.frontend;

import static io.scriptor.util.Util.isCompOP;
import static io.scriptor.util.Util.isDecDigit;
import static io.scriptor.util.Util.isID;
import static io.scriptor.util.Util.isOP;
import static io.scriptor.util.Util.isOctDigit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import io.scriptor.frontend.expression.BinaryExpression;
import io.scriptor.frontend.expression.CallExpression;
import io.scriptor.frontend.expression.CompoundExpression;
import io.scriptor.frontend.expression.DefineExpression;
import io.scriptor.frontend.expression.Expression;
import io.scriptor.frontend.expression.FloatExpression;
import io.scriptor.frontend.expression.FunctionExpression;
import io.scriptor.frontend.expression.IDExpression;
import io.scriptor.frontend.expression.IntExpression;
import io.scriptor.frontend.expression.ReturnExpression;
import io.scriptor.frontend.expression.StringExpression;
import io.scriptor.frontend.expression.UnaryExpression;
import io.scriptor.frontend.expression.WhileExpression;
import io.scriptor.type.FunctionType;
import io.scriptor.type.PointerType;
import io.scriptor.type.StructType;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class Parser {

    private static final Map<String, Integer> precedences = new HashMap<>();
    static {
        precedences.clear();
        precedences.put("=", 0);
        precedences.put("<<=", 0);
        precedences.put(">>=", 0);
        precedences.put(">>>=", 0);
        precedences.put("+=", 0);
        precedences.put("-=", 0);
        precedences.put("*=", 0);
        precedences.put("/=", 0);
        precedences.put("%=", 0);
        precedences.put("&=", 0);
        precedences.put("|=", 0);
        precedences.put("^=", 0);
        precedences.put("&&", 1);
        precedences.put("||", 1);
        precedences.put("<", 2);
        precedences.put(">", 2);
        precedences.put("<=", 2);
        precedences.put(">=", 2);
        precedences.put("==", 2);
        precedences.put("&", 3);
        precedences.put("|", 3);
        precedences.put("^", 3);
        precedences.put("<<", 4);
        precedences.put(">>", 4);
        precedences.put(">>>", 4);
        precedences.put("+", 5);
        precedences.put("-", 5);
        precedences.put("*", 6);
        precedences.put("/", 6);
        precedences.put("%", 6);
    }

    public static void parse(final ParserConfig config) throws IOException {
        parse(config, new ArrayList<>());
    }

    public static void parse(final ParserConfig config, final List<File> parsed) throws IOException {
        if (parsed.contains(config.file()))
            return;
        parsed.add(config.file());

        final var parser = new Parser(config, parsed);

        parser.next();
        while (!parser.atEOF()) {
            final var expression = parser.nextExpression(null);
            if (expression != null)
                config.callback().accept(expression);
        }

        config.stream().close();
    }

    private final ParserConfig config;
    private final List<File> parsed;

    private Token token;
    private int chr = -1;
    private int row = 1;
    private int column = 0;

    private final Stack<State> stack = new Stack<>();
    private Type currentResult;

    private Parser(final ParserConfig config, final List<File> parsed) {
        this.config = config;
        this.parsed = parsed;
        stack.push(config.global());
    }

    private int get() throws IOException {
        ++column;
        return config.stream().read();
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
                            loc = new SourceLocation(config.file(), row, column);
                            mode = ParserMode.STRING;
                            break;

                        case '\'':
                            loc = new SourceLocation(config.file(), row, column);
                            mode = ParserMode.CHAR;
                            break;

                        default:
                            if (chr <= 0x20) {
                                if (chr == '\n')
                                    newline();
                                break;
                            }

                            if (isDecDigit(chr)) {
                                loc = new SourceLocation(config.file(), row, column);
                                mode = ParserMode.NUMBER;
                                value.append((char) chr);
                                break;
                            }

                            if (isID(chr)) {
                                loc = new SourceLocation(config.file(), row, column);
                                mode = ParserMode.ID;
                                value.append((char) chr);
                                break;
                            }

                            if (isOP(chr)) {
                                loc = new SourceLocation(config.file(), row, column);
                                mode = ParserMode.OPERATOR;
                                value.append((char) chr);
                                break;
                            }

                            loc = new SourceLocation(config.file(), row, column);
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
            throw new QScriptException(new SourceLocation(config.file(), row, column), "expected %s, at EOF", type);
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
            throw new QScriptException(new SourceLocation(config.file(), row, column), "expected '%s', at EOF", value);
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
        if (nextIfAt("struct")) {
            if (!nextIfAt("{"))
                return nextType(StructType.getOpaque(config.context()));
            final List<Type> elements = new ArrayList<>();
            while (!nextIfAt("}")) {
                final var type = nextType();
                elements.add(type);
                if (!at("}"))
                    expect(",");
            }
            return nextType(StructType.get(config.context(), elements.toArray(Type[]::new)));
        }

        final var base = expect(TokenType.ID).value();
        return nextType(Type.get(config.context(), base));
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
        if (at("use")) {
            nextUse();
            return null;
        }

        if (at("include")) {
            nextInclude();
            return null;
        }

        if (at("def"))
            return nextDefine();

        if (at("while"))
            return nextWhile();

        if (at("return"))
            return nextReturn();

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
            return DefineExpression.create(loc, type, id);

        final var init = nextExpression(type);
        return DefineExpression.create(loc, type, id, init);
    }

    private void nextUse() throws IOException {
        expect("use");
        final var id = expect(TokenType.ID).value();
        expect("as");
        final var type = nextType();

        config.context().getType(id, () -> type);
    }

    private void nextInclude() throws IOException {
        expect("include");
        final var filename = expect(TokenType.STRING).value();

        var file = new File(filename);
        if (!file.isAbsolute())
            file = new File(config.file().getParentFile(), filename);

        parse(new ParserConfig(config, file, new FileInputStream(file)), parsed);
    }

    private Expression nextWhile() throws IOException {
        final var loc = expect("while").location();

        final var condition = nextExpression(Type.getInt1(config.context()));
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

    private CompoundExpression nextCompound() throws IOException {
        final var loc = expect("{").location();
        final List<Expression> expressions = new ArrayList<>();

        stack.push(new State(stack.peek()));
        while (!nextIfAt("}")) {
            final var expression = nextExpression(null);
            expressions.add(expression);
        }
        stack.pop();

        return CompoundExpression.create(loc, expressions.toArray(Expression[]::new));
    }

    private Expression nextBinary(final Type expected) throws IOException {
        return nextBinary(nextCall(expected), 0);
    }

    private Expression nextBinary(Expression lhs, final int minPrecedence) throws IOException {
        while (at(TokenType.OPERATOR) && precedences.get(token.value()) >= minPrecedence) {
            final var tk = skip();
            final var loc = tk.location();
            final var operator = tk.value();
            final var precedence = precedences.get(operator);
            final var expected = lhs.getType();
            var rhs = nextCall(expected);
            while (at(TokenType.OPERATOR) && precedences.get(token.value()) > precedence) {
                final var laPrecedence = precedences.get(token.value());
                rhs = nextBinary(rhs, precedence + (laPrecedence > precedence ? 1 : 0));
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
            throw new QScriptException(new SourceLocation(config.file(), row, column), "reached eof");

        final var loc = token.location();

        if (nextIfAt("$")) {
            expect("(");
            final var type = (FunctionType) expected;

            stack.push(new State(stack.peek()));

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
            return IntExpression.create(loc, Type.getInt64(config.context()), Long.valueOf(skip().value()));

        if (at(TokenType.FLOAT))
            return FloatExpression.create(loc, Type.getFlt64(config.context()), Double.valueOf(skip().value()));

        if (at(TokenType.STRING))
            return StringExpression.create(loc, Type.getInt8Ptr(config.context()), skip().value());

        if (at(TokenType.OPERATOR)) {
            final var operator = skip().value();
            final var operand = nextExpression(expected);
            return UnaryExpression.createL(loc, operator, operand);
        }

        throw new QScriptException(loc, "unhandled token '%s' (%s)", token.value(), token.type());
    }
}

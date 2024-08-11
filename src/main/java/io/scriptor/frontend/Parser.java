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
import java.util.function.Function;

import io.scriptor.frontend.expression.BinaryExpression;
import io.scriptor.frontend.expression.CallExpression;
import io.scriptor.frontend.expression.CharExpression;
import io.scriptor.frontend.expression.Expression;
import io.scriptor.frontend.expression.FloatExpression;
import io.scriptor.frontend.expression.FunctionExpression;
import io.scriptor.frontend.expression.IndexExpression;
import io.scriptor.frontend.expression.InitListExpression;
import io.scriptor.frontend.expression.IntExpression;
import io.scriptor.frontend.expression.StringExpression;
import io.scriptor.frontend.expression.SymbolExpression;
import io.scriptor.frontend.expression.UnaryExpression;
import io.scriptor.frontend.statement.CompoundStatement;
import io.scriptor.frontend.statement.DefFunStatement;
import io.scriptor.frontend.statement.DefVarStatement;
import io.scriptor.frontend.statement.IfStatement;
import io.scriptor.frontend.statement.ReturnStatement;
import io.scriptor.frontend.statement.Statement;
import io.scriptor.frontend.statement.WhileStatement;
import io.scriptor.type.ArrayType;
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
        precedences.put("!=", 2);
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
            final var stmt = parser.nextStatement();
            if (stmt != null)
                config.callback().accept(stmt);
        }

        config.stream().close();
    }

    private final ParserConfig config;
    private final List<File> parsed;

    private Token token;
    private int chr = -1;
    private int row = 1;
    private int column = 0;

    private final Stack<Context> stack = new Stack<>();
    private Type currentResult;

    private Parser(final ParserConfig config, final List<File> parsed) {
        this.config = config;
        this.parsed = parsed;
        stack.push(config.ctx());
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

                        case '0':
                            loc = new SourceLocation(config.file(), row, column);
                            mode = ParserMode.RADIX;
                            break;

                        default:
                            if (chr <= 0x20) {
                                if (chr == '\n')
                                    newline();
                                break;
                            }

                            if (isDecDigit(chr)) {
                                loc = new SourceLocation(config.file(), row, column);
                                mode = ParserMode.NUMDEC;
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
                    else if (chr == '\n')
                        newline();
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

                case RADIX:
                    if (chr == 'b' || chr == 'B') {
                        mode = ParserMode.NUMBIN;
                        break;
                    }
                    if (chr == 'x' || chr == 'X') {
                        mode = ParserMode.NUMHEX;
                        break;
                    }
                    if (chr == '.') {
                        mode = ParserMode.NUMDEC;
                        isfloat = true;
                        value.append("0.");
                        break;
                    }
                    if (isOctDigit(chr)) {
                        mode = ParserMode.NUMOCT;
                        value.append((char) chr);
                        break;
                    }
                    return new Token(loc, TokenType.DECINT, "0");

                case NUMBIN:
                    break;

                case NUMOCT:
                    break;

                case NUMDEC:
                    if (chr == '.') {
                        isfloat = true;
                        value.append((char) chr);
                        break;
                    }
                    if (isDecDigit(chr)) {
                        value.append((char) chr);
                        break;
                    }
                    return new Token(loc, isfloat ? TokenType.FLOAT : TokenType.DECINT, value.toString());

                case NUMHEX:
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

    private boolean at(final TokenType... types) {
        if (token == null)
            return false;
        for (final var type : types)
            if (token.type() == type)
                return true;
        return false;
    }

    private boolean at(final String value) {
        return token != null && token.value().equals(value);
    }

    private Token expectPeek(final TokenType type) throws IOException {
        if (at(type))
            return token;
        if (atEOF())
            throw new QScriptException(new SourceLocation(config.file(), row, column), "expected %s, at EOF", type);
        throw new QScriptException(
                token.location(),
                "expected %s, at '%s' (%s)",
                type,
                token.value(),
                token.type());
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
        return nextType(false);
    }

    private Type nextType(final boolean unsafe) throws IOException {
        if (nextIfAt("struct")) {
            if (!nextIfAt("{"))
                return nextType(StructType.get(stack.peek()));
            final List<Type> elements = new ArrayList<>();
            while (!nextIfAt("}")) {
                final var type = nextType();
                elements.add(type);
                if (!at("}"))
                    expect(",");
            }
            return nextType(StructType.get(stack.peek(), elements.toArray(Type[]::new)));
        }

        final var base = expectPeek(TokenType.ID).value();
        if (unsafe && !Type.exists(stack.peek(), base))
            return null;

        skip();
        return nextType(Type.get(stack.peek(), base));
    }

    private Type nextType(final Type base) throws IOException {
        if (nextIfAt("*"))
            return nextType(PointerType.get(base));

        if (nextIfAt("[")) {
            if (nextIfAt("]"))
                return nextType(ArrayType.get(base, -1));
            final var length = skip().longValue();
            expect("]");
            return nextType(ArrayType.get(base, length));
        }

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

    private Statement nextStatement() throws IOException {
        if (at("include")) {
            nextInclude();
            return null;
        }
        if (at("macro")) {
            nextMacro();
            return null;
        }
        if (at("use")) {
            nextUse();
            return null;
        }

        if (at("{"))
            return nextCompound();
        if (at("def"))
            return nextDefine();
        if (at("if"))
            return nextIf();
        if (at("return"))
            return nextReturn();
        if (at("while"))
            return nextWhile();

        if (at(TokenType.ID)) {
            final var name = token.value();
            if (stack.peek().existsMacro(name)) {
                skip();
                return stack.peek().getMacro(name);
            }
        }

        return nextExpr(null);
    }

    private void nextInclude() throws IOException {
        final var loc = expect("include").location();
        final var filename = expect(TokenType.STRING).value();

        var file = new File(filename);
        if (!file.isAbsolute())
            file = new File(config.file().getParentFile(), filename);

        for (int i = 0; i < config.includeDirs().length && !file.exists(); ++i) {
            file = new File(config.includeDirs()[i], filename);
        }

        if (!file.exists()) {
            throw new QScriptException(loc, "missing file '%s'", filename);
        }

        parse(new ParserConfig(config, file, new FileInputStream(file)), parsed);
    }

    private void nextMacro() throws IOException {
        expect("macro");
        final var name = expect(TokenType.ID).value();
        final var stmt = nextStatement();

        stack.peek().putMacro(name, stmt);
    }

    private void nextUse() throws IOException {
        expect("use");
        final var id = expect(TokenType.ID).value();
        expect("as");
        final var type = nextType();

        Type.useAs(stack.peek(), id, type);
    }

    private CompoundStatement nextCompound() throws IOException {
        final var loc = expect("{").location();
        final List<Statement> body = new ArrayList<>();

        stack.push(new Context(stack.peek()));
        while (!nextIfAt("}")) {
            final var stmt = nextStatement();
            body.add(stmt);
        }
        stack.pop();

        return CompoundStatement.create(loc, body.toArray(Statement[]::new));
    }

    private Statement nextDefine() throws IOException {
        final var loc = expect("def").location();

        var type = nextType(true);
        final String name;
        if (!at(TokenType.ID)) {
            name = type.getId();
            type = null;
        } else {
            name = skip().value();
        }

        if (nextIfAt("=")) {
            final var init = nextExpr(type);
            if (type == null)
                type = init.getType();
            stack.peek().declareSymbol(type, name);
            return DefVarStatement.create(loc, type, name, init);
        }

        if (nextIfAt("(")) {
            final List<Arg> args = new ArrayList<>();
            var vararg = false;
            while (!nextIfAt(")")) {
                if (nextIfAt("?")) {
                    vararg = true;
                    expect(")");
                    break;
                }
                final var argtype = nextType();
                final String argname;
                if (at(TokenType.ID)) {
                    argname = skip().value();
                } else {
                    argname = null;
                }
                args.add(new Arg(argtype, argname));
                if (!at(")"))
                    expect(",");
            }

            final var funtype = FunctionType.get(
                    type,
                    vararg,
                    args.stream()
                            .map(Arg::type)
                            .toArray(Type[]::new));
            stack.peek().declareSymbol(funtype, name);

            if (at("{")) {
                stack.push(new Context(stack.peek()));

                for (final var arg : args)
                    stack.peek().declareSymbol(arg.type(), arg.name());

                final var bkpResult = currentResult;
                currentResult = type;
                final var body = nextCompound();
                currentResult = bkpResult;

                stack.pop();

                return DefFunStatement.create(loc, type, name, args.toArray(Arg[]::new), vararg, body);
            }

            return DefFunStatement.create(loc, type, name, args.toArray(Arg[]::new), vararg);
        }

        stack.peek().declareSymbol(type, name);
        return DefVarStatement.create(loc, type, name);
    }

    private IfStatement nextIf() throws IOException {
        final var loc = expect("if").location();

        final var condition = nextExpr(Type.getInt1(stack.peek()));
        final var then = nextStatement();

        if (nextIfAt("else")) {
            final var else_ = nextStatement();
            return IfStatement.create(loc, condition, then, else_);
        }

        return IfStatement.create(loc, condition, then);
    }

    private ReturnStatement nextReturn() throws IOException {
        final var loc = expect("return").location();

        if (nextIfAt("void"))
            return ReturnStatement.create(loc, currentResult);

        final var expression = nextExpr(currentResult);
        return ReturnStatement.create(loc, currentResult, expression);
    }

    private WhileStatement nextWhile() throws IOException {
        final var loc = expect("while").location();

        final var condition = nextExpr(Type.getInt1(stack.peek()));
        final var loop = nextStatement();

        return WhileStatement.create(loc, condition, loop);
    }

    private Expression nextExpr(final Type expected) throws IOException {
        return nextBinary(expected);
    }

    private Expression nextBinary(final Type expected) throws IOException {
        return nextBinary(nextCall(expected), 0);
    }

    private Expression nextBinary(Expression lhs, final int minPrecedence) throws IOException {
        while (at(TokenType.OPERATOR) && precedences.getOrDefault(token.value(), -1) >= minPrecedence) {
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
        var expr = nextIndex(expected);

        while (at("(")) {
            final var loc = skip().location();

            final FunctionType calleeType = (FunctionType) expr.getType();

            final List<Expression> args = new ArrayList<>();
            while (!nextIfAt(")")) {
                final var arg = nextBinary(calleeType.getArg(args.size()));
                args.add(arg);
                if (!at(")"))
                    expect(",");
            }

            expr = CallExpression.create(loc, calleeType.getResult(), expr, args.toArray(Expression[]::new));
        }

        return expr;
    }

    private Expression nextIndex(final Type expected) throws IOException {
        var expr = nextUnary(expected);

        while (at("[")) {
            final var loc = skip().location();
            final var index = nextBinary(Type.getInt64(stack.peek()));
            expect("]");

            expr = IndexExpression.create(loc, expr, index);
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
            final var fntype = (FunctionType) ((PointerType) expected).getBase();

            stack.push(new Context(stack.peek()));

            final List<String> argnames = new ArrayList<>();
            while (!nextIfAt(")")) {
                final var argname = expect(TokenType.ID).value();
                stack.peek().declareSymbol(fntype.getArg(argnames.size()), argname);
                argnames.add(argname);
                if (!at(")"))
                    expect(",");
            }

            final var bkpResult = currentResult;
            currentResult = fntype.getResult();
            final var body = nextCompound();
            currentResult = bkpResult;

            stack.pop();

            return FunctionExpression.create(
                    loc,
                    fntype,
                    argnames.toArray(String[]::new),
                    body);
        }

        if (nextIfAt("(")) {
            final var expression = nextBinary(expected);
            expect(")");
            return expression;
        }

        if (nextIfAt("{")) {

            final Function<Integer, Type> element = (i) -> {
                if (expected instanceof ArrayType type)
                    return type.getBase();
                if (expected instanceof StructType type)
                    return type.getElement(i);
                throw new QScriptException();
            };

            final List<Expression> args = new ArrayList<>();
            while (!nextIfAt("}")) {
                final var i = args.size();
                final var arg = nextBinary(element.apply(i));
                args.add(arg);
                if (!at("}"))
                    expect(",");
            }

            return InitListExpression.create(loc, expected, args.toArray(Expression[]::new));
        }

        if (at(TokenType.ID)) {
            final var name = skip().value();
            if (stack.peek().existsMacro(name)) {
                return stack.peek().getMacro(name);
            }

            if (stack.peek().existsSymbol(name)) {
                final var sym = stack.peek().getSymbol(name);
                return SymbolExpression.create(loc, sym.type(), sym.name());
            }

            throw new QScriptException(loc, "no such macro or symbol with name '%s'", name);
        }

        if (at(TokenType.BININT, TokenType.OCTINT, TokenType.DECINT, TokenType.HEXINT))
            return IntExpression.create(loc, Type.getInt64(stack.peek()), skip().longValue());

        if (at(TokenType.FLOAT))
            return FloatExpression.create(loc, Type.getFlt64(stack.peek()), skip().doubleValue());

        if (at(TokenType.CHAR))
            return CharExpression.create(loc, Type.getInt8(stack.peek()), skip().charValue());

        if (at(TokenType.STRING))
            return StringExpression.create(loc, Type.getInt8Ptr(stack.peek()), skip().value());

        if (at(TokenType.OPERATOR)) {
            final var operator = skip().value();
            final var operand = nextCall(expected);
            return UnaryExpression.createL(loc, operator, operand);
        }

        throw new QScriptException(loc, "unhandled token '%s' (%s)", token.value(), token.type());
    }
}

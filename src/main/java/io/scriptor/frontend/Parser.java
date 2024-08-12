package io.scriptor.frontend;

import static io.scriptor.util.Util.isBinDigit;
import static io.scriptor.util.Util.isCompOP;
import static io.scriptor.util.Util.isDecDigit;
import static io.scriptor.util.Util.isHexDigit;
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
import java.util.function.Function;

import io.scriptor.frontend.expr.BinaryExpr;
import io.scriptor.frontend.expr.CallExpr;
import io.scriptor.frontend.expr.CharExpr;
import io.scriptor.frontend.expr.Expr;
import io.scriptor.frontend.expr.FloatExpr;
import io.scriptor.frontend.expr.FunctionExpr;
import io.scriptor.frontend.expr.IndexExpr;
import io.scriptor.frontend.expr.InitializerExpr;
import io.scriptor.frontend.expr.IntExpr;
import io.scriptor.frontend.expr.StringExpr;
import io.scriptor.frontend.expr.SymbolExpr;
import io.scriptor.frontend.expr.UnaryExpr;
import io.scriptor.frontend.stmt.CompoundStmt;
import io.scriptor.frontend.stmt.DefFunctionStmt;
import io.scriptor.frontend.stmt.DefVariableStmt;
import io.scriptor.frontend.stmt.IfStmt;
import io.scriptor.frontend.stmt.ReturnStmt;
import io.scriptor.frontend.stmt.Stmt;
import io.scriptor.frontend.stmt.WhileStmt;
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

    private StackFrame stack;
    private Type currentResult;

    private Parser(final ParserConfig config, final List<File> parsed) {
        this.config = config;
        this.parsed = parsed;
        stack = config.frame();
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
                    if (isBinDigit(chr) || chr == 'u') {
                        value.append((char) chr);
                        break;
                    }
                    return new Token(loc, TokenType.BININT, value.toString());

                case NUMOCT:
                    if (isOctDigit(chr) || chr == 'u') {
                        value.append((char) chr);
                        break;
                    }
                    return new Token(loc, TokenType.OCTINT, value.toString());

                case NUMDEC:
                    if (chr == '.') {
                        isfloat = true;
                        value.append((char) chr);
                        break;
                    }
                    if (isDecDigit(chr) || chr == 'u') {
                        value.append((char) chr);
                        break;
                    }
                    return new Token(loc, isfloat ? TokenType.FLOAT : TokenType.DECINT, value.toString());

                case NUMHEX:
                    if (isHexDigit(chr) || chr == 'u') {
                        value.append((char) chr);
                        break;
                    }
                    return new Token(loc, TokenType.HEXINT, value.toString());

                case ID:
                    if (!isID(chr))
                        return new Token(loc, TokenType.NAME, value.toString());
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
            if (token.ty() == type)
                return true;
        return false;
    }

    private boolean at(final String value) {
        return token != null && token.val().equals(value);
    }

    private Token expectPeek(final TokenType type) throws IOException {
        if (at(type))
            return token;
        if (atEOF())
            throw new QScriptException(new SourceLocation(config.file(), row, column), "expected %s, at EOF", type);
        throw new QScriptException(
                token.sl(),
                "expected %s, at '%s' (%s)",
                type,
                token.val(),
                token.ty());
    }

    private Token expect(final TokenType type) throws IOException {
        if (at(type))
            return skip();
        if (atEOF())
            throw new QScriptException(new SourceLocation(config.file(), row, column), "expected %s, at EOF", type);
        throw new QScriptException(
                token.sl(),
                "expected %s, at '%s' (%s)",
                type,
                token.val(),
                token.ty());
    }

    private Token expect(final String value) throws IOException {
        if (at(value))
            return skip();
        if (atEOF())
            throw new QScriptException(new SourceLocation(config.file(), row, column), "expected '%s', at EOF", value);
        throw new QScriptException(
                token.sl(),
                "expected '%s', at '%s' (%s)",
                value,
                token.val(),
                token.ty());
    }

    private Token skip() throws IOException {
        final var tk = token;
        next();
        return tk;
    }

    private boolean nextIfAt(final TokenType type) throws IOException {
        if (at(type)) {
            next();
            return true;
        }
        return false;
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
                return nextType(StructType.get(stack));
            final List<Type> elements = new ArrayList<>();
            while (!nextIfAt("}")) {
                final var type = nextType();
                nextIfAt(TokenType.NAME);
                elements.add(type);
                if (!at("}"))
                    expect(",");
            }
            return nextType(StructType.get(stack, elements.toArray(Type[]::new)));
        }

        final var base = expectPeek(TokenType.NAME).val();
        if (unsafe && !Type.exists(stack, base))
            return null;

        final var loc = skip().sl();
        return nextType(Type.get(loc, stack, base));
    }

    private Type nextType(final Type base) throws IOException {
        if (nextIfAt("*"))
            return nextType(PointerType.get(base));

        if (nextIfAt("[")) {
            if (nextIfAt("]"))
                return nextType(ArrayType.get(base, -1));
            final var length = skip().asLong();
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
                nextIfAt(TokenType.NAME);
                args.add(arg);
                if (!at(")"))
                    expect(",");
            }
            return nextType(FunctionType.get(base, vararg, args.toArray(Type[]::new)));
        }

        return base;
    }

    private Stmt nextStatement() throws IOException {
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

        if (at(TokenType.NAME)) {
            final var name = token.val();
            if (stack.existsMacro(name)) {
                final var loc = skip().sl();
                return stack.getMacro(loc, name);
            }
        }

        return nextExpr(null);
    }

    private void nextInclude() throws IOException {
        final var loc = expect("include").sl();
        final var filename = expect(TokenType.STRING).val();

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
        final var name = expect(TokenType.NAME).val();
        final var stmt = nextStatement();

        stack.putMacro(name, stmt);
    }

    private void nextUse() throws IOException {
        expect("use");
        final var id = expect(TokenType.NAME).val();
        expect("as");
        final var type = nextType();

        Type.useAs(stack, id, type);
    }

    private CompoundStmt nextCompound() throws IOException {
        final var loc = expect("{").sl();
        final List<Stmt> body = new ArrayList<>();

        stack = new StackFrame(stack);
        while (!nextIfAt("}")) {
            final var stmt = nextStatement();
            body.add(stmt);
        }
        stack = stack.getParent();

        return CompoundStmt.create(loc, body.toArray(Stmt[]::new));
    }

    private Stmt nextDefine() throws IOException {
        final var loc = expect("def").sl();

        var type = nextType(true);
        final String name;
        if (!at(TokenType.NAME)) {
            name = type.getId();
            type = null;
        } else {
            name = skip().val();
        }

        if (nextIfAt("=")) {
            final var init = nextExpr(type);
            if (type == null)
                type = init.getTy();
            stack.declareSymbol(name, type);
            return DefVariableStmt.create(loc, type, name, init);
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
                if (at(TokenType.NAME)) {
                    argname = skip().val();
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
                            .map(Arg::ty)
                            .toArray(Type[]::new));
            stack.declareSymbol(name, funtype);

            if (at("{")) {
                stack = new StackFrame(stack);

                for (final var arg : args)
                    stack.declareSymbol(arg.name(), arg.ty());

                final var bkpResult = currentResult;
                currentResult = type;
                final var body = nextCompound();
                currentResult = bkpResult;

                stack = stack.getParent();

                return DefFunctionStmt.create(loc, type, name, args.toArray(Arg[]::new), vararg, body);
            }

            return DefFunctionStmt.create(loc, type, name, args.toArray(Arg[]::new), vararg);
        }

        stack.declareSymbol(name, type);
        return DefVariableStmt.create(loc, type, name);
    }

    private IfStmt nextIf() throws IOException {
        final var loc = expect("if").sl();

        final var condition = nextExpr(Type.getInt1(stack));
        final var then = nextStatement();

        if (nextIfAt("else")) {
            final var else_ = nextStatement();
            return IfStmt.create(loc, condition, then, else_);
        }

        return IfStmt.create(loc, condition, then);
    }

    private ReturnStmt nextReturn() throws IOException {
        final var loc = expect("return").sl();

        if (nextIfAt("void"))
            return ReturnStmt.create(loc, currentResult);

        final var expression = nextExpr(currentResult);
        return ReturnStmt.create(loc, currentResult, expression);
    }

    private WhileStmt nextWhile() throws IOException {
        final var loc = expect("while").sl();

        final var condition = nextExpr(Type.getInt1(stack));
        final var loop = nextStatement();

        return WhileStmt.create(loc, condition, loop);
    }

    private Expr nextExpr(final Type expected) throws IOException {
        return nextBinary(expected);
    }

    private Expr nextBinary(final Type expected) throws IOException {
        return nextBinary(nextCall(expected), 0);
    }

    private Expr nextBinary(Expr lhs, final int minPrecedence) throws IOException {
        while (at(TokenType.OPERATOR) && precedences.getOrDefault(token.val(), -1) >= minPrecedence) {
            final var tk = skip();
            final var loc = tk.sl();
            final var operator = tk.val();
            final var precedence = precedences.get(operator);
            final var expected = lhs.getTy();
            var rhs = nextCall(expected);
            while (at(TokenType.OPERATOR) && precedences.get(token.val()) > precedence) {
                final var laPrecedence = precedences.get(token.val());
                rhs = nextBinary(rhs, precedence + (laPrecedence > precedence ? 1 : 0));
            }
            lhs = BinaryExpr.create(loc, operator, lhs, rhs);
        }
        return lhs;
    }

    private Expr nextCall(final Type expected) throws IOException {
        var expr = nextIndex(expected);

        while (at("(")) {
            final var loc = skip().sl();

            final FunctionType calleeType = expr.getTy().asFunction();

            final List<Expr> args = new ArrayList<>();
            while (!nextIfAt(")")) {
                final var arg = nextBinary(calleeType.getArg(args.size()));
                args.add(arg);
                if (!at(")"))
                    expect(",");
            }

            expr = CallExpr.create(loc, calleeType.getResult(), expr, args.toArray(Expr[]::new));
        }

        return expr;
    }

    private Expr nextIndex(final Type expected) throws IOException {
        var expr = nextUnary(expected);

        while (at("[")) {
            final var sl = skip().sl();
            final var idx = nextBinary(Type.getInt64(stack));
            expect("]");

            expr = IndexExpr.create(sl, expr, idx);
        }

        return expr;
    }

    private Expr nextUnary(final Type expected) throws IOException {
        var expr = nextPrimary(expected);

        if (at("++") || at("--")) {
            final var tk = skip();
            final var loc = tk.sl();
            final var operator = tk.val();
            expr = UnaryExpr.createR(loc, operator, expr);
        }

        return expr;
    }

    private Expr nextPrimary(final Type expected) throws IOException {
        if (atEOF())
            throw new QScriptException(new SourceLocation(config.file(), row, column), "reached eof");

        final var loc = token.sl();

        if (nextIfAt("$")) {
            expect("(");
            final var ft = expected
                    .asPointer()
                    .getBase()
                    .asFunction();

            stack = new StackFrame(stack);

            final List<String> args = new ArrayList<>();
            while (!nextIfAt(")")) {
                final var arg = expect(TokenType.NAME).val();
                stack.declareSymbol(arg, ft.getArg(args.size()));
                args.add(arg);
                if (!at(")"))
                    expect(",");
            }

            final var bkpResult = currentResult;
            currentResult = ft.getResult();
            final var body = nextCompound();
            currentResult = bkpResult;

            stack = stack.getParent();

            return FunctionExpr.create(
                    loc,
                    ft,
                    args.toArray(String[]::new),
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
                throw new QScriptException(loc, "not a suitable type");
            };

            final List<Expr> args = new ArrayList<>();
            while (!nextIfAt("}")) {
                final var i = args.size();
                final var arg = nextBinary(element.apply(i));
                args.add(arg);
                if (!at("}"))
                    expect(",");
            }

            return InitializerExpr.create(loc, expected, args.toArray(Expr[]::new));
        }

        if (at(TokenType.NAME)) {
            final var name = skip().val();
            if (stack.existsMacro(name)) {
                return stack.getMacro(loc, name);
            }

            if (stack.existsSymbol(name)) {
                final var type = stack.getSymbol(loc, name);
                return SymbolExpr.create(loc, type, name);
            }

            throw new QScriptException(loc, "no such macro or symbol with name '%s'", name);
        }

        if (at(TokenType.BININT, TokenType.OCTINT, TokenType.DECINT, TokenType.HEXINT))
            return IntExpr.create(loc, Type.getInt64(stack), skip().asLong());

        if (at(TokenType.FLOAT))
            return FloatExpr.create(loc, Type.getFlt64(stack), skip().asDouble());

        if (at(TokenType.CHAR))
            return CharExpr.create(loc, Type.getInt8(stack), skip().asChar());

        if (at(TokenType.STRING))
            return StringExpr.create(loc, Type.getInt8Ptr(stack), skip().val());

        if (at(TokenType.OPERATOR)) {
            final var operator = skip().val();
            final var operand = nextCall(expected);
            return UnaryExpr.createL(loc, operator, operand);
        }

        throw new QScriptException(loc, "unhandled token '%s' (%s)", token.val(), token.ty());
    }
}

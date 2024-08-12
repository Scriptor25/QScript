package io.scriptor.frontend;

import static io.scriptor.util.Util.isBinDigit;
import static io.scriptor.util.Util.isCompOP;
import static io.scriptor.util.Util.isDecDigit;
import static io.scriptor.util.Util.isHexDigit;
import static io.scriptor.util.Util.isID;
import static io.scriptor.util.Util.isOP;
import static io.scriptor.util.Util.isOctDigit;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import io.scriptor.util.QScriptError;

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
            if (stmt.isPresent())
                config.callback().accept(stmt.get());
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
        SourceLocation sl = null;

        while (chr >= 0 || mode != ParserMode.NORMAL) {
            switch (mode) {
                case NORMAL:
                    switch (chr) {
                        case '#':
                            mode = ParserMode.COMMENT;
                            break;

                        case '"':
                            sl = new SourceLocation(config.file(), row, column);
                            mode = ParserMode.STRING;
                            break;

                        case '\'':
                            sl = new SourceLocation(config.file(), row, column);
                            mode = ParserMode.CHAR;
                            break;

                        case '0':
                            sl = new SourceLocation(config.file(), row, column);
                            mode = ParserMode.RADIX;
                            break;

                        default:
                            if (chr <= 0x20) {
                                if (chr == '\n')
                                    newline();
                                break;
                            }

                            if (isDecDigit(chr)) {
                                sl = new SourceLocation(config.file(), row, column);
                                mode = ParserMode.NUMDEC;
                                value.append((char) chr);
                                break;
                            }

                            if (isID(chr)) {
                                sl = new SourceLocation(config.file(), row, column);
                                mode = ParserMode.ID;
                                value.append((char) chr);
                                break;
                            }

                            if (isOP(chr)) {
                                sl = new SourceLocation(config.file(), row, column);
                                mode = ParserMode.OPERATOR;
                                value.append((char) chr);
                                break;
                            }

                            sl = new SourceLocation(config.file(), row, column);
                            value.append((char) chr);
                            chr = get();
                            return new Token(sl, TokenType.OTHER, value.toString());
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
                        return new Token(sl, TokenType.STRING, value.toString());
                    }
                    if (chr == '\\')
                        escape();
                    value.append((char) chr);
                    break;

                case CHAR:
                    if (chr == '\'') {
                        chr = get();
                        return new Token(sl, TokenType.CHAR, value.toString());
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
                    return new Token(sl, TokenType.DECINT, "0");

                case NUMBIN:
                    if (isBinDigit(chr) || chr == 'u') {
                        value.append((char) chr);
                        break;
                    }
                    return new Token(sl, TokenType.BININT, value.toString());

                case NUMOCT:
                    if (isOctDigit(chr) || chr == 'u') {
                        value.append((char) chr);
                        break;
                    }
                    return new Token(sl, TokenType.OCTINT, value.toString());

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
                    return new Token(sl, isfloat ? TokenType.FLOAT : TokenType.DECINT, value.toString());

                case NUMHEX:
                    if (isHexDigit(chr) || chr == 'u') {
                        value.append((char) chr);
                        break;
                    }
                    return new Token(sl, TokenType.HEXINT, value.toString());

                case ID:
                    if (!isID(chr))
                        return new Token(sl, TokenType.NAME, value.toString());
                    value.append((char) chr);
                    break;

                case OPERATOR:
                    if (!isCompOP(chr))
                        return new Token(sl, TokenType.OPERATOR, value.toString());
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

    private Optional<Token> expectPeek(final TokenType type) throws IOException {
        if (at(type))
            return of(token);
        if (atEOF()) {
            QScriptError.print(SourceLocation.UNKNOWN, "expected %s, at EOF", type);
            return empty();
        }
        QScriptError.print(token.sl(), "expected %s, at '%s' (%s)", type, token.val(), token.ty());
        return empty();
    }

    private Optional<Token> expect(final TokenType type) throws IOException {
        if (at(type))
            return of(skip());
        if (atEOF()) {
            QScriptError.print(SourceLocation.UNKNOWN, "expected %s, at EOF", type);
            return empty();
        }
        QScriptError.print(token.sl(), "expected %s, at '%s' (%s)", type, token.val(), token.ty());
        skip();
        return empty();
    }

    private Optional<Token> expect(final String value) throws IOException {
        if (at(value))
            return of(skip());
        if (atEOF()) {
            QScriptError.print(SourceLocation.UNKNOWN, "expected '%s', at EOF", value);
            return empty();
        }
        QScriptError.print(token.sl(), "expected '%s', at '%s' (%s)", value, token.val(), token.ty());
        skip();
        return empty();
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

    private Optional<Type> nextType() throws IOException {
        return nextType(false);
    }

    private Optional<Type> nextType(final boolean unsafe) throws IOException {
        if (nextIfAt("struct")) {
            if (!nextIfAt("{"))
                return of(nextType(StructType.get(stack)));
            final List<Type> elements = new ArrayList<>();
            while (!nextIfAt("}")) {
                final var type = nextType().get();
                nextIfAt(TokenType.NAME);
                elements.add(type);
                if (!at("}"))
                    expect(",");
            }
            return of(nextType(StructType.get(stack, elements.toArray(Type[]::new))));
        }

        final var base = expectPeek(TokenType.NAME).get().val();
        if (unsafe && !Type.exists(stack, base))
            return empty();

        final var sl = skip().sl();
        final var ty = Type.get(sl, stack, base);
        if (ty.isEmpty())
            return ty;

        return of(nextType(ty.get()));
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

                final var arg = nextType().get();
                nextIfAt(TokenType.NAME);
                args.add(arg);
                if (!at(")"))
                    expect(",");
            }
            return nextType(FunctionType.get(base, vararg, args.toArray(Type[]::new)));
        }

        return base;
    }

    private Optional<Stmt> nextStatement() throws IOException {
        if (at("include")) {
            nextInclude();
            return empty();
        }
        if (at("macro")) {
            nextMacro();
            return empty();
        }
        if (at("use")) {
            nextUse();
            return empty();
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
                final var sl = skip().sl();
                return stack.getMacro(sl, name);
            }
        }

        final var optExpr = nextExpr(null);
        if (optExpr.isEmpty())
            return empty();

        return of(optExpr.get());
    }

    private void nextInclude() throws IOException {
        final var optInclude = expect("include");
        if (optInclude.isEmpty())
            return;

        final var sl = optInclude.get().sl();

        final var optFilename = expect(TokenType.STRING);
        if (optFilename.isEmpty())
            return;

        final var filename = optFilename.get().val();

        var file = new File(filename);
        if (!file.isAbsolute())
            file = new File(config.file().getParentFile(), filename);

        for (int i = 0; i < config.includeDirs().length && !file.exists(); ++i) {
            file = new File(config.includeDirs()[i], filename);
        }

        if (!file.exists()) {
            QScriptError.print(sl, "missing file '%s'", filename);
            return;
        }

        parse(new ParserConfig(config, file, new FileInputStream(file)), parsed);
    }

    private void nextMacro() throws IOException {
        if (expect("macro").isEmpty())
            return;

        final var optName = expect(TokenType.NAME);
        if (optName.isEmpty())
            return;

        final var optStmt = nextStatement();
        if (optStmt.isEmpty())
            return;

        stack.putMacro(optName.get().val(), optStmt.get());
    }

    private void nextUse() throws IOException {
        final var optUse = expect("use");
        if (optUse.isEmpty())
            return;

        final var optName = expect(TokenType.NAME);
        if (optName.isEmpty())
            return;

        if (expect("as").isEmpty())
            return;

        final var optType = nextType();
        if (optType.isEmpty())
            return;

        Type.useAs(stack, optUse.get().sl(), optName.get().val(), optType.get());
    }

    private Optional<Stmt> nextCompound() throws IOException {
        final var optBrace = expect("{");
        if (optBrace.isEmpty())
            return empty();

        final var sl = optBrace.get().sl();
        final List<Stmt> body = new ArrayList<>();

        stack = new StackFrame(stack);
        while (!nextIfAt("}")) {
            final var optStmt = nextStatement();
            if (optStmt.isPresent())
                body.add(optStmt.get());
        }
        stack = stack.getParent();

        return of(CompoundStmt.create(sl, body.toArray(Stmt[]::new)));
    }

    private Optional<Stmt> nextDefine() throws IOException {
        final var optDef = expect("def");
        if (optDef.isEmpty())
            return empty();

        final var sl = optDef.get().sl();
        final var optType = nextType(true);

        Type type = null;
        final String name;
        if (!at(TokenType.NAME)) {
            name = optType.get().getId();
        } else {
            if (optType.isPresent())
                type = optType.get();
            name = skip().val();
        }

        if (nextIfAt("=")) {
            final var optExpr = nextExpr(type);
            if (optExpr.isEmpty()) {
                if (type == null)
                    return empty();
                return of(DefVariableStmt.create(sl, type, name));
            }

            if (type == null)
                type = optExpr.get().getTy();

            stack.declareSymbol(name, type);
            return of(DefVariableStmt.create(sl, type, name, optExpr.get()));
        }

        if (nextIfAt("(")) {
            final List<Arg> args = new ArrayList<>();
            var vararg = false;
            while (!nextIfAt(")")) {
                if (nextIfAt("?")) {
                    vararg = true;
                    if (expect(")").isEmpty())
                        return empty();
                    break;
                }

                final var optArgType = nextType();
                if (optArgType.isEmpty())
                    return empty();

                final String argName;
                if (at(TokenType.NAME)) {
                    argName = skip().val();
                } else {
                    argName = null;
                }

                args.add(new Arg(optArgType.get(), argName));

                if (!at(")"))
                    if (expect(",").isEmpty())
                        return empty();
            }

            final var ft = FunctionType.get(type, vararg, args.stream().map(Arg::ty).toArray(Type[]::new));
            stack.declareSymbol(name, ft);

            if (at("{")) {
                stack = new StackFrame(stack);
                for (final var arg : args)
                    stack.declareSymbol(arg.name(), arg.ty());

                final var bkp = currentResult;
                currentResult = type;
                final var optBody = nextCompound();
                currentResult = bkp;
                stack = stack.getParent();

                if (optBody.isEmpty())
                    return of(
                            DefFunctionStmt.create(
                                    sl,
                                    type,
                                    name,
                                    args.toArray(Arg[]::new),
                                    vararg));

                return of(
                        DefFunctionStmt.create(
                                sl,
                                type,
                                name,
                                args.toArray(Arg[]::new),
                                vararg,
                                optBody.get()));
            }

            return of(DefFunctionStmt.create(sl, type, name, args.toArray(Arg[]::new), vararg));
        }

        stack.declareSymbol(name, type);
        return of(DefVariableStmt.create(sl, type, name));
    }

    private Optional<Stmt> nextIf() throws IOException {
        final var optIf = expect("if");
        if (optIf.isEmpty())
            return empty();

        final var sl = optIf.get().sl();

        final var optC = nextExpr(Type.getInt1(stack));
        if (optC.isEmpty())
            return empty();

        final var optT = nextStatement();
        if (optT.isEmpty())
            return empty();

        if (nextIfAt("else")) {
            final var optE = nextStatement();
            if (optE.isEmpty())
                return empty();

            return of(IfStmt.create(sl, optC.get(), optT.get(), optE.get()));
        }

        return of(IfStmt.create(sl, optC.get(), optT.get()));
    }

    private Optional<Stmt> nextReturn() throws IOException {
        final var optReturn = expect("return");
        if (optReturn.isEmpty())
            return empty();

        final var sl = optReturn.get().sl();

        if (nextIfAt("void"))
            return of(ReturnStmt.create(sl, currentResult));

        final var optVal = nextExpr(currentResult);
        if (optVal.isEmpty())
            return empty();

        return of(ReturnStmt.create(sl, currentResult, optVal.get()));
    }

    private Optional<Stmt> nextWhile() throws IOException {
        final var optWhile = expect("while");
        if (optWhile.isEmpty())
            return empty();

        final var sl = optWhile.get().sl();

        final var optC = nextExpr(Type.getInt1(stack));
        if (optC.isEmpty())
            return empty();

        final var optL = nextStatement();
        if (optL.isEmpty())
            return empty();

        return of(WhileStmt.create(sl, optC.get(), optL.get()));
    }

    private Optional<Expr> nextExpr(final Type expected) throws IOException {
        return nextBinary(expected);
    }

    private Optional<Expr> nextBinary(final Type expected) throws IOException {
        final var optCall = nextCall(expected);
        if (optCall.isEmpty())
            return empty();
        return nextBinary(optCall.get(), 0);
    }

    private Optional<Expr> nextBinary(final Expr lhs, final int minPrecedence) throws IOException {
        var opt = of(lhs);

        while (opt.isPresent()
                && at(TokenType.OPERATOR)
                && precedences.getOrDefault(token.val(), -1) >= minPrecedence) {
            final var tk = skip();
            final var sl = tk.sl();
            final var op = tk.val();
            final var prec = precedences.get(op);
            final var ty = opt.get().getTy();
            var rhs = nextCall(ty);
            while (rhs.isPresent()
                    && at(TokenType.OPERATOR)
                    && precedences.get(token.val()) > prec) {
                final var laprec = precedences.get(token.val());
                rhs = nextBinary(rhs.get(), prec + (laprec > prec ? 1 : 0));
            }

            opt = BinaryExpr.create(sl, op, opt.get(), rhs.get());
        }

        return opt;
    }

    private Optional<Expr> nextCall(final Type expected) throws IOException {
        var opt = nextIndex(expected);

        while (opt.isPresent() && at("(")) {
            final var sl = skip().sl();

            final var optType = opt.get().getTy().asFunction();
            if (optType.isEmpty()) {
                while (!nextIfAt(")"))
                    skip();
                return empty();
            }

            final List<Expr> args = new ArrayList<>();
            while (!nextIfAt(")")) {
                final var optArgType = optType.get().getArg(args.size());
                final var optArg = nextBinary(optArgType.isPresent() ? optArgType.get() : null);
                if (optArg.isEmpty())
                    return empty();

                args.add(optArg.get());
                if (!at(")"))
                    if (expect(",").isEmpty())
                        return empty();
            }

            opt = of(CallExpr.create(sl, optType.get().getResult(), opt.get(), args.toArray(Expr[]::new)));
        }

        return opt;
    }

    private Optional<Expr> nextIndex(final Type expected) throws IOException {
        var opt = nextUnary(expected);

        while (opt.isPresent() && at("[")) {
            final var sl = skip().sl();
            final var optIdx = nextBinary(Type.getInt64(stack));
            if (expect("]").isEmpty())
                return empty();

            opt = IndexExpr.create(sl, opt.get(), optIdx.get());
        }

        return opt;
    }

    private Optional<Expr> nextUnary(final Type expected) throws IOException {
        var opt = nextPrimary(expected);

        if (opt.isPresent() && (at("++") || at("--"))) {
            final var tk = skip();
            final var sl = tk.sl();
            final var op = tk.val();
            opt = of(UnaryExpr.createR(sl, op, opt.get()));
        }

        return opt;
    }

    private Optional<Expr> nextPrimary(final Type expected) throws IOException {
        if (atEOF()) {
            QScriptError.print(SourceLocation.UNKNOWN, "reached eof");
            return empty();
        }

        final var sl = token.sl();

        if (nextIfAt("$")) {
            expect("(");

            final var optBase = expected.getPointerBase();
            final Optional<FunctionType> optFt = optBase.isPresent() ? optBase.get().asFunction() : empty();

            if (optFt.isEmpty()) {
                while (!nextIfAt(")"))
                    skip();
                expect("{");
                while (!nextIfAt("}"))
                    skip();
                return empty();
            }

            final var ft = optFt.get();
            final List<String> args = new ArrayList<>();

            stack = new StackFrame(stack);
            while (!nextIfAt(")")) {
                final var arg = expect(TokenType.NAME).get().val();
                final var argty = ft.getArg(args.size());
                if (argty.isEmpty())
                    return empty();
                stack.declareSymbol(arg, argty.get());
                args.add(arg);
                if (!at(")"))
                    expect(",");
            }

            final var bkpResult = currentResult;
            currentResult = ft.getResult();
            final var body = (CompoundStmt) nextCompound().get();
            currentResult = bkpResult;
            stack = stack.getParent();

            return of(FunctionExpr.create(sl, ft, args.toArray(String[]::new), body));
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
                QScriptError.print(sl, "not a suitable type");
                return null;
            };

            final List<Expr> args = new ArrayList<>();
            while (!nextIfAt("}")) {
                final var i = args.size();
                final var optArg = nextBinary(element.apply(i));
                if (optArg.isPresent())
                    args.add(optArg.get());
                if (!at("}"))
                    expect(",");
            }

            return of(InitializerExpr.create(sl, expected, args.toArray(Expr[]::new)));
        }

        if (at(TokenType.NAME)) {
            final var name = skip().val();
            if (stack.existsMacro(name)) {
                return stack.getMacro(sl, name);
            }

            if (stack.existsSymbol(name)) {
                final var type = stack.getSymbol(sl, name).get();
                return of(SymbolExpr.create(sl, type, name));
            }

            QScriptError.print(sl, "no such macro or symbol with name '%s'", name);
            return empty();
        }

        if (at(TokenType.BININT, TokenType.OCTINT, TokenType.DECINT, TokenType.HEXINT))
            return of(IntExpr.create(sl, Type.getInt64(stack), skip().asLong()));

        if (at(TokenType.FLOAT))
            return of(FloatExpr.create(sl, Type.getFlt64(stack), skip().asDouble()));

        if (at(TokenType.CHAR))
            return of(CharExpr.create(sl, Type.getInt8(stack), skip().asChar()));

        if (at(TokenType.STRING))
            return of(StringExpr.create(sl, Type.getInt8Ptr(stack), skip().val()));

        if (at(TokenType.OPERATOR)) {
            final var operator = skip().val();
            final var operand = nextCall(expected).get();
            return of(UnaryExpr.createL(sl, operator, operand));
        }

        QScriptError.print(sl, "unhandled token '%s' (%s)", token.val(), token.ty());
        skip();
        return empty();
    }
}

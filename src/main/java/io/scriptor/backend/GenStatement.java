package io.scriptor.backend;

import static io.scriptor.backend.GenCast.genCast;
import static io.scriptor.backend.GenExpression.genExpr;
import static io.scriptor.backend.GenType.genType;
import static io.scriptor.backend.LValue.allocaL;
import static io.scriptor.backend.LValue.copyL;
import static io.scriptor.backend.LValue.directL;
import static io.scriptor.backend.RValue.createR;
import static org.bytedeco.llvm.global.LLVM.LLVMAddFunction;
import static org.bytedeco.llvm.global.LLVM.LLVMAddGlobal;
import static org.bytedeco.llvm.global.LLVM.LLVMAppendBasicBlockInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildBr;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildCondBr;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildRet;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildRetVoid;
import static org.bytedeco.llvm.global.LLVM.LLVMClearInsertionPosition;
import static org.bytedeco.llvm.global.LLVM.LLVMCountBasicBlocks;
import static org.bytedeco.llvm.global.LLVM.LLVMGetBasicBlockParent;
import static org.bytedeco.llvm.global.LLVM.LLVMGetBasicBlockTerminator;
import static org.bytedeco.llvm.global.LLVM.LLVMGetFirstBasicBlock;
import static org.bytedeco.llvm.global.LLVM.LLVMGetInsertBlock;
import static org.bytedeco.llvm.global.LLVM.LLVMGetNamedFunction;
import static org.bytedeco.llvm.global.LLVM.LLVMGetNextBasicBlock;
import static org.bytedeco.llvm.global.LLVM.LLVMGetParam;
import static org.bytedeco.llvm.global.LLVM.LLVMPositionBuilderAtEnd;
import static org.bytedeco.llvm.global.LLVM.LLVMPrintMessageAction;
import static org.bytedeco.llvm.global.LLVM.LLVMSetInitializer;
import static org.bytedeco.llvm.global.LLVM.LLVMSetValueName;
import static org.bytedeco.llvm.global.LLVM.LLVMVerifyFunction;

import io.scriptor.frontend.expression.Expression;
import io.scriptor.frontend.statement.CompoundStatement;
import io.scriptor.frontend.statement.DefFunStatement;
import io.scriptor.frontend.statement.DefVarStatement;
import io.scriptor.frontend.statement.IfStatement;
import io.scriptor.frontend.statement.ReturnStatement;
import io.scriptor.frontend.statement.Statement;
import io.scriptor.frontend.statement.WhileStatement;
import io.scriptor.type.PointerType;
import io.scriptor.util.QScriptException;

public class GenStatement {

    public static void genStmt(final Builder b, final Statement stmt) {
        if (stmt instanceof Expression e) {
            genExpr(b, e);
            return;
        }
        if (stmt instanceof CompoundStatement s) {
            genStmt(b, s);
            return;
        }
        if (stmt instanceof DefFunStatement s) {
            genStmt(b, s);
            return;
        }
        if (stmt instanceof DefVarStatement s) {
            genStmt(b, s);
            return;
        }
        if (stmt instanceof IfStatement s) {
            genStmt(b, s);
            return;
        }
        if (stmt instanceof ReturnStatement s) {
            genStmt(b, s);
            return;
        }
        if (stmt instanceof WhileStatement s) {
            genStmt(b, s);
            return;
        }

        throw new QScriptException(stmt.getSl(), "no genIR for class '%s':\n%s", stmt.getClass(), stmt);
    }

    public static void genStmt(final Builder b, final CompoundStatement stmt) {
        b.push();

        for (int i = 0; i < stmt.getCount(); ++i)
            genStmt(b, stmt.get(i));

        b.pop();
    }

    public static void genStmt(final Builder b, final DefFunStatement stmt) {
        final var sl = stmt.getSl();
        final var ft = genType(sl, stmt.getFunctionType());

        var f = LLVMGetNamedFunction(b.getModule(), stmt.getName());
        if (f == null) {
            f = LLVMAddFunction(b.getModule(), stmt.getName(), ft);
            if (f == null)
                throw new QScriptException(
                        sl,
                        "failed to create function '%s':\n%s",
                        stmt.getName(),
                        stmt);
        }

        b.put(
                stmt.getName(),
                createR(b, sl, PointerType.get(stmt.getFunctionType()), f));

        if (stmt.getBody() == null)
            return;

        if (LLVMCountBasicBlocks(f) != 0)
            throw new QScriptException(sl, "cannot redefine function '%s'", stmt.getName());

        final var entry = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "entry");
        LLVMPositionBuilderAtEnd(b.getBuilder(), entry);

        b.push();
        for (int i = 0; i < stmt.getArgCount(); ++i) {
            final var arg = stmt.getArg(i);

            final var llvmarg = LLVMGetParam(f, i);
            LLVMSetValueName(llvmarg, arg.name());

            final var value = allocaL(b, sl, arg.ty(), llvmarg);
            b.put(arg.name(), value);
        }

        genStmt(b, stmt.getBody());

        LLVMClearInsertionPosition(b.getBuilder());
        b.pop();

        for (var bb = LLVMGetFirstBasicBlock(f); bb != null; bb = LLVMGetNextBasicBlock(bb)) {
            if (LLVMGetBasicBlockTerminator(bb) != null)
                continue;

            if (stmt.getRes().isVoid()) {
                LLVMPositionBuilderAtEnd(b.getBuilder(), bb);
                LLVMBuildRetVoid(b.getBuilder());
                LLVMClearInsertionPosition(b.getBuilder());
                continue;
            }

            throw new QScriptException(sl, "not all paths return a value");
        }

        if (LLVMVerifyFunction(f, LLVMPrintMessageAction) != 0) {
            throw new QScriptException(sl, "failed to verify function");
        }
    }

    public static void genStmt(final Builder b, final DefVarStatement stmt) {
        final var sl = stmt.getSl();
        final var name = stmt.getName();

        if (b.isGlobal()) {
            final var vt = genType(sl, stmt.getTy());

            final var ptr = LLVMAddGlobal(b.getModule(), vt, name);
            final var value = directL(b, sl, stmt.getTy(), ptr);

            if (stmt.hasInit()) {
                if (stmt.getInit().isConst()) {
                    final var init = genExpr(b, stmt.getInit());
                    LLVMSetInitializer(ptr, init.get());
                } else {
                    throw new QScriptException(sl, "global initializer must be constant");
                }
            }

            b.put(name, value);
            return;
        }

        final Value value;
        if (stmt.hasInit()) {
            final var init = genCast(b, sl, genExpr(b, stmt.getInit()), stmt.getTy());
            value = copyL(b, sl, init);
        } else {
            value = allocaL(b, sl, stmt.getTy());
        }

        b.put(name, value);
    }

    public static void genStmt(final Builder b, final IfStatement stmt) {

        final var f = LLVMGetBasicBlockParent(LLVMGetInsertBlock(b.getBuilder()));
        final var headbb = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "head");
        final var thenbb = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "then");
        final var elsebb = LLVMAppendBasicBlockInContext(Builder.getContext(), f, stmt.hasE() ? "else" : "end");
        final var endbb = stmt.hasE() ? LLVMAppendBasicBlockInContext(Builder.getContext(), f, "end") : elsebb;

        LLVMBuildBr(b.getBuilder(), headbb);

        LLVMPositionBuilderAtEnd(b.getBuilder(), headbb);
        final var condition = genExpr(b, stmt.getC());
        LLVMBuildCondBr(b.getBuilder(), condition.get(), thenbb, elsebb);

        LLVMPositionBuilderAtEnd(b.getBuilder(), thenbb);
        genStmt(b, stmt.getT());
        if (LLVMGetBasicBlockTerminator(LLVMGetInsertBlock(b.getBuilder())) == null)
            LLVMBuildBr(b.getBuilder(), endbb);

        if (stmt.hasE()) {
            LLVMPositionBuilderAtEnd(b.getBuilder(), elsebb);
            genStmt(b, stmt.getE());
            if (LLVMGetBasicBlockTerminator(LLVMGetInsertBlock(b.getBuilder())) == null)
                LLVMBuildBr(b.getBuilder(), endbb);
        }

        LLVMPositionBuilderAtEnd(b.getBuilder(), endbb);
    }

    public static void genStmt(final Builder b, final ReturnStatement stmt) {
        if (!stmt.hasVal()) {
            LLVMBuildRetVoid(b.getBuilder());
            return;
        }

        final var value = genCast(b, stmt.getSl(), genExpr(b, stmt.getVal()), stmt.getRes());
        LLVMBuildRet(b.getBuilder(), value.get());
        return;
    }

    public static void genStmt(final Builder b, final WhileStatement stmt) {

        final var f = LLVMGetBasicBlockParent(LLVMGetInsertBlock(b.getBuilder()));
        final var head = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "head");
        final var loop = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "loop");
        final var end = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "end");

        LLVMBuildBr(b.getBuilder(), head);

        LLVMPositionBuilderAtEnd(b.getBuilder(), head);
        final var condition = genExpr(b, stmt.getC());
        LLVMBuildCondBr(b.getBuilder(), condition.get(), loop, end);

        LLVMPositionBuilderAtEnd(b.getBuilder(), loop);
        genStmt(b, stmt.getL());
        LLVMBuildBr(b.getBuilder(), head);

        LLVMPositionBuilderAtEnd(b.getBuilder(), end);
    }

    private GenStatement() {
    }
}

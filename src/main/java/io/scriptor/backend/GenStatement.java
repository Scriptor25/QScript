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
import static org.bytedeco.llvm.global.LLVM.LLVMDeleteBasicBlock;
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

import java.util.Optional;

import io.scriptor.frontend.expr.Expr;
import io.scriptor.frontend.stmt.CompoundStmt;
import io.scriptor.frontend.stmt.DefFunctionStmt;
import io.scriptor.frontend.stmt.DefVariableStmt;
import io.scriptor.frontend.stmt.IfStmt;
import io.scriptor.frontend.stmt.ReturnStmt;
import io.scriptor.frontend.stmt.Stmt;
import io.scriptor.frontend.stmt.WhileStmt;
import io.scriptor.type.PointerType;
import io.scriptor.util.QScriptError;

public class GenStatement {

    public static void genStmt(final Builder b, final Stmt stmt) {
        if (stmt instanceof Expr e) {
            genExpr(b, e);
            return;
        }
        if (stmt instanceof CompoundStmt s) {
            genStmt(b, s);
            return;
        }
        if (stmt instanceof DefFunctionStmt s) {
            genStmt(b, s);
            return;
        }
        if (stmt instanceof DefVariableStmt s) {
            genStmt(b, s);
            return;
        }
        if (stmt instanceof IfStmt s) {
            genStmt(b, s);
            return;
        }
        if (stmt instanceof ReturnStmt s) {
            genStmt(b, s);
            return;
        }
        if (stmt instanceof WhileStmt s) {
            genStmt(b, s);
            return;
        }

        QScriptError.print(stmt.getSl(), "no genIR for class '%s':\n%s", stmt.getClass(), stmt);
    }

    public static void genStmt(final Builder b, final CompoundStmt stmt) {
        b.push();

        for (int i = 0; i < stmt.getCount(); ++i)
            genStmt(b, stmt.get(i));

        b.pop();
    }

    public static void genStmt(final Builder b, final DefFunctionStmt stmt) {
        final var sl = stmt.getSl();
        final var ft = genType(sl, stmt.getFunctionType()).get();

        var f = LLVMGetNamedFunction(b.getModule(), stmt.getName());
        if (f == null) {
            f = LLVMAddFunction(b.getModule(), stmt.getName(), ft);
            if (f == null) {
                QScriptError.print(sl, "failed to create function '%s':\n%s", stmt.getName(), stmt);
                return;
            }
        }

        b.put(stmt.getName(), createR(b, sl, PointerType.get(stmt.getFunctionType()), f));

        if (stmt.getBody() == null)
            return;

        if (LLVMCountBasicBlocks(f) != 0) {
            QScriptError.print(sl, "cannot redefine function '%s'", stmt.getName());
            return;
        }

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

            QScriptError.print(sl, "not all paths return a value");
            for (var db = LLVMGetFirstBasicBlock(f); db != null; db = LLVMGetNextBasicBlock(db))
                LLVMDeleteBasicBlock(db);
            return;
        }

        if (LLVMVerifyFunction(f, LLVMPrintMessageAction) != 0) {
            QScriptError.print(sl, "failed to verify function");
            for (var db = LLVMGetFirstBasicBlock(f); db != null; db = LLVMGetNextBasicBlock(db))
                LLVMDeleteBasicBlock(db);
            return;
        }
    }

    public static void genStmt(final Builder b, final DefVariableStmt stmt) {
        final var sl = stmt.getSl();
        final var name = stmt.getName();

        if (b.isGlobal()) {
            final var vt = genType(sl, stmt.getTy()).get();

            final var ptr = LLVMAddGlobal(b.getModule(), vt, name);
            final var value = directL(b, sl, stmt.getTy(), ptr);

            if (stmt.hasInit()) {
                if (stmt.getInit().isConst()) {
                    final var init = genExpr(b, stmt.getInit());
                    if (init.isPresent())
                        LLVMSetInitializer(ptr, init.get().get());
                } else {
                    QScriptError.print(sl, "global initializer must be constant");
                    return;
                }
            }

            b.put(name, value);
            return;
        }

        final Value value;
        if (stmt.hasInit()) {
            final var init = genExpr(b, stmt.getInit());
            final var cast = init.isPresent() ? genCast(b, sl, init.get(), stmt.getTy()) : Optional.<Value>empty();
            if (init.isPresent() && cast.isPresent()) {
                value = copyL(b, sl, cast.get());
            } else {
                value = allocaL(b, sl, stmt.getTy());
            }
        } else {
            value = allocaL(b, sl, stmt.getTy());
        }

        b.put(name, value);
    }

    public static void genStmt(final Builder b, final IfStmt stmt) {

        final var bb = LLVMGetInsertBlock(b.getBuilder());
        final var f = LLVMGetBasicBlockParent(bb);
        final var headbb = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "head");
        final var thenbb = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "then");
        final var elsebb = LLVMAppendBasicBlockInContext(Builder.getContext(), f, stmt.hasE() ? "else" : "end");
        final var endbb = stmt.hasE() ? LLVMAppendBasicBlockInContext(Builder.getContext(), f, "end") : elsebb;

        LLVMBuildBr(b.getBuilder(), headbb);

        LLVMPositionBuilderAtEnd(b.getBuilder(), headbb);
        final var condition = genExpr(b, stmt.getC());
        if (condition.isEmpty()) {
            LLVMPositionBuilderAtEnd(b.getBuilder(), bb);
            LLVMDeleteBasicBlock(headbb);
            LLVMDeleteBasicBlock(thenbb);
            LLVMDeleteBasicBlock(elsebb);
            if (stmt.hasE())
                LLVMDeleteBasicBlock(endbb);
            return;
        }

        LLVMBuildCondBr(b.getBuilder(), condition.get().get(), thenbb, elsebb);

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

    public static void genStmt(final Builder b, final ReturnStmt stmt) {
        if (!stmt.hasVal()) {
            LLVMBuildRetVoid(b.getBuilder());
            return;
        }

        final var val = genExpr(b, stmt.getVal());
        if (val.isEmpty())
            return;

        final var cast = genCast(b, stmt.getSl(), val.get(), stmt.getRes());
        if (cast.isEmpty())
            return;

        LLVMBuildRet(b.getBuilder(), cast.get().get());
        return;
    }

    public static void genStmt(final Builder b, final WhileStmt stmt) {

        final var bb = LLVMGetInsertBlock(b.getBuilder());
        final var f = LLVMGetBasicBlockParent(bb);
        final var head = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "head");
        final var loop = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "loop");
        final var end = LLVMAppendBasicBlockInContext(Builder.getContext(), f, "end");

        LLVMBuildBr(b.getBuilder(), head);

        LLVMPositionBuilderAtEnd(b.getBuilder(), head);

        final var condition = genExpr(b, stmt.getC());
        if (condition.isEmpty()) {
            LLVMPositionBuilderAtEnd(b.getBuilder(), bb);
            LLVMDeleteBasicBlock(head);
            LLVMDeleteBasicBlock(loop);
            LLVMDeleteBasicBlock(end);
            return;
        }

        LLVMBuildCondBr(b.getBuilder(), condition.get().get(), loop, end);

        LLVMPositionBuilderAtEnd(b.getBuilder(), loop);
        genStmt(b, stmt.getL());
        LLVMBuildBr(b.getBuilder(), head);

        LLVMPositionBuilderAtEnd(b.getBuilder(), end);
    }

    private GenStatement() {
    }
}

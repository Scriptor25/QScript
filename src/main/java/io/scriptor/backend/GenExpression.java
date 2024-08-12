package io.scriptor.backend;

import static io.scriptor.backend.GenCast.genCast;
import static io.scriptor.backend.GenOperation.genAdd;
import static io.scriptor.backend.GenOperation.genAnd;
import static io.scriptor.backend.GenOperation.genDiv;
import static io.scriptor.backend.GenOperation.genEQ;
import static io.scriptor.backend.GenOperation.genGE;
import static io.scriptor.backend.GenOperation.genGT;
import static io.scriptor.backend.GenOperation.genLAnd;
import static io.scriptor.backend.GenOperation.genLE;
import static io.scriptor.backend.GenOperation.genLNeg;
import static io.scriptor.backend.GenOperation.genLOr;
import static io.scriptor.backend.GenOperation.genLT;
import static io.scriptor.backend.GenOperation.genLXor;
import static io.scriptor.backend.GenOperation.genMul;
import static io.scriptor.backend.GenOperation.genNE;
import static io.scriptor.backend.GenOperation.genNeg;
import static io.scriptor.backend.GenOperation.genNot;
import static io.scriptor.backend.GenOperation.genOr;
import static io.scriptor.backend.GenOperation.genRef;
import static io.scriptor.backend.GenOperation.genRem;
import static io.scriptor.backend.GenOperation.genSub;
import static io.scriptor.backend.GenOperation.genXor;
import static io.scriptor.backend.GenType.genType;
import static io.scriptor.backend.LValue.directL;
import static io.scriptor.backend.RValue.createR;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildCall2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildGEP2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildGlobalStringPtr;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildStructGEP2;
import static org.bytedeco.llvm.global.LLVM.LLVMConstInt;
import static org.bytedeco.llvm.global.LLVM.LLVMConstReal;
import static org.bytedeco.llvm.global.LLVM.LLVMInt32TypeInContext;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.expression.BinaryExpression;
import io.scriptor.frontend.expression.CallExpression;
import io.scriptor.frontend.expression.Expression;
import io.scriptor.frontend.expression.FloatExpression;
import io.scriptor.frontend.expression.FunctionExpression;
import io.scriptor.frontend.expression.IndexExpression;
import io.scriptor.frontend.expression.InitListExpression;
import io.scriptor.frontend.expression.IntExpression;
import io.scriptor.frontend.expression.StringExpression;
import io.scriptor.frontend.expression.SymbolExpression;
import io.scriptor.frontend.expression.UnaryExpression;
import io.scriptor.type.ArrayType;
import io.scriptor.type.FunctionType;
import io.scriptor.type.PointerType;
import io.scriptor.type.StructType;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptException;

public class GenExpression {

    public static Value genExpr(final Builder b, final Expression expr) {
        if (expr instanceof BinaryExpression e)
            return genExpr(b, e);
        if (expr instanceof CallExpression e)
            return genExpr(b, e);
        if (expr instanceof FloatExpression e)
            return genExpr(b, e);
        if (expr instanceof FunctionExpression e)
            return genExpr(b, e);
        if (expr instanceof IndexExpression e)
            return genExpr(b, e);
        if (expr instanceof InitListExpression e)
            return genExpr(b, e);
        if (expr instanceof IntExpression e)
            return genExpr(b, e);
        if (expr instanceof StringExpression e)
            return genExpr(b, e);
        if (expr instanceof SymbolExpression e)
            return genExpr(b, e);
        if (expr instanceof UnaryExpression e)
            return genExpr(b, e);

        throw new QScriptException(expr.getSl(), "no genIR for class '%s':\n%s", expr.getClass(), expr);
    }

    public static Value genExpr(final Builder b, final BinaryExpression expr) {

        final var sl = expr.getSl();
        var op = expr.getOp();

        if ("=".equals(op)) {
            final var assignee = (LValue) genExpr(b, expr.getLHS());
            final var value = genCast(b, sl, genExpr(b, expr.getRHS()), assignee.getType());
            assignee.setValue(value.get());
            return assignee;
        }

        var left = genExpr(b, expr.getLHS());
        var right = genExpr(b, expr.getRHS());

        if (left.getType() != right.getType()) {
            final var higher = Type.getHigherOrder(sl, left.getType(), right.getType());
            left = genCast(b, sl, left, higher);
            right = genCast(b, sl, right, higher);
        }

        Value result = switch (op) {
            case "==" -> genEQ(b, sl, left, right);
            case "!=" -> genNE(b, sl, left, right);
            case "<" -> genLT(b, sl, left, right);
            case ">" -> genGT(b, sl, left, right);
            case "<=" -> genLE(b, sl, left, right);
            case ">=" -> genGE(b, sl, left, right);
            case "&&" -> genLAnd(b, sl, left, right);
            case "||" -> genLOr(b, sl, left, right);
            case "^^" -> genLXor(b, sl, left, right);
            default -> null;
        };

        if (result != null)
            return result;

        final var assign = op.contains("=");
        if (assign)
            op = op.replace("=", "");

        result = switch (op) {
            case "+" -> genAdd(b, sl, left, right);
            case "-" -> genSub(b, sl, left, right);
            case "*" -> genMul(b, sl, left, right);
            case "/" -> genDiv(b, sl, left, right);
            case "%" -> genRem(b, sl, left, right);
            case "&" -> genAnd(b, sl, left, right);
            case "|" -> genOr(b, sl, left, right);
            case "^" -> genXor(b, sl, left, right);
            default -> null;
        };

        if (result != null) {
            if (assign) {
                final var assignee = (LValue) genExpr(b, expr.getLHS());
                final var value = genCast(b, sl, result, assignee.getType());
                assignee.setValue(value.get());
                return assignee;
            }
            return result;
        }

        throw new QScriptException(
                sl,
                "no such operator '%s %s %s'",
                left.getType(),
                expr.getOp(),
                right.getType());
    }

    public static Value genExpr(final Builder b, final CallExpression expr) {
        final var sl = expr.getSl();

        final var callee = genExpr(b, expr.getCallee());
        final var fnty = (FunctionType) ((PointerType) callee.getType()).getBase();

        final var args = new PointerPointer<LLVMValueRef>(expr.getArgCount());
        for (int i = 0; i < expr.getArgCount(); ++i)
            args.put(i, genCast(b, sl, genExpr(b, expr.getArg(i)), fnty.getArg(i)).get());

        final var result = LLVMBuildCall2(
                b.getBuilder(),
                genType(sl, fnty),
                callee.get(),
                args,
                expr.getArgCount(),
                "");

        return createR(b, sl, expr.getTy(), result);
    }

    public static Value genExpr(final Builder b, final FloatExpression expr) {
        final var sl = expr.getSl();
        final var type = genType(sl, expr.getTy());
        final var value = LLVMConstReal(type, expr.getVal());
        return createR(b, sl, expr.getTy(), value);
    }

    public static Value genExpr(final Builder b, final FunctionExpression expr) {
        throw new QScriptException(expr.getSl(), "TODO");
    }

    public static Value genExpr(final Builder b, final IndexExpression expr) {

        final var sl = expr.getSl();
        final var ptr = genExpr(b, expr.getPtr());
        final var index = genExpr(b, expr.getIdx());

        final var arraytype = expr.getPtr().getTy();

        if (arraytype instanceof PointerType type) {
            final var llvmbase = genType(sl, type.getBase());

            final var indices = new PointerPointer<LLVMValueRef>(1);
            indices.put(0, index.get());

            final var gep = LLVMBuildGEP2(b.getBuilder(), llvmbase, ptr.get(), indices, 1, "");
            return directL(b, sl, type.getBase(), gep);
        }

        if (arraytype instanceof ArrayType type) {
            final var llvmtype = genType(sl, type);

            final var indices = new PointerPointer<LLVMValueRef>(2);
            indices.put(0, LLVMConstInt(LLVMInt32TypeInContext(Builder.getContext()), 0, 1));
            indices.put(1, index.get());
            final var gep = LLVMBuildGEP2(b.getBuilder(), llvmtype, ((LValue) ptr).getPtr(), indices, 2, "");
            return directL(b, sl, type.getBase(), gep);
        }

        throw new QScriptException(
                sl,
                "type must be an array or pointer type, but is '%s'",
                arraytype);
    }

    public static Value genExpr(final Builder b, final InitListExpression expr) {

        final var sl = expr.getSl();

        if (expr.getTy() instanceof StructType type) {
            final var llvmtype = genType(sl, type);
            final var ptr = b.genAlloca(llvmtype);

            for (int i = 0; i < expr.getArgCount(); ++i) {
                final var val = genCast(b, sl, genExpr(b, expr.getArg(i)), type.getElement(i)).get();
                final var gep = LLVMBuildStructGEP2(b.getBuilder(), llvmtype, ptr, i, "");
                b.genStore(val, gep);
            }

            return directL(b, sl, type, ptr);
        }

        if (expr.getTy() instanceof ArrayType type) {
            final var llvmtype = genType(sl, type);
            final var ptr = b.genAlloca(llvmtype);

            final var indices = new PointerPointer<LLVMValueRef>(2);
            indices.put(0, LLVMConstInt(LLVMInt32TypeInContext(Builder.getContext()), 0, 1));
            for (int i = 0; i < expr.getArgCount(); ++i) {
                final var val = genCast(b, sl, genExpr(b, expr.getArg(i)), type.getBase()).get();
                indices.put(1, LLVMConstInt(LLVMInt32TypeInContext(Builder.getContext()), i, 1));
                final var gep = LLVMBuildGEP2(b.getBuilder(), llvmtype, ptr, indices, 2, "");
                b.genStore(val, gep);
            }

            return directL(b, sl, type, ptr);
        }

        throw new QScriptException(
                sl,
                "type must be an array or struct type, but is '%s'",
                expr.getTy());
    }

    public static Value genExpr(final Builder b, final IntExpression expr) {

        final var sl = expr.getSl();
        final var type = expr.getTy();

        final var llvmtype = genType(sl, type);
        final var value = LLVMConstInt(llvmtype, expr.getVal(), 1);
        return createR(b, sl, type, value);
    }

    public static Value genExpr(final Builder b, final StringExpression expr) {
        final var value = LLVMBuildGlobalStringPtr(b.getBuilder(), expr.getVal(), "");
        return createR(b, expr.getSl(), expr.getTy(), value);
    }

    public static Value genExpr(final Builder b, final SymbolExpression expr) {
        final var name = expr.getName();
        final var value = b.get(name);
        if (value == null)
            throw new QScriptException(expr.getSl(), "undefined symbol '%s'", expr.getName());
        return value;
    }

    public static Value genExpr(final Builder b, final UnaryExpression expr) {

        final var sl = expr.getSl();

        final var op = expr.getOp();
        final var value = genExpr(b, expr.getVal());

        final var llvmtype = value.getLLVMType();

        final boolean assign;
        final Value result;

        if ("++".equals(op)) {
            assign = true;
            result = genAdd(b, sl, value, createR(b, sl, value.getType(), LLVMConstInt(llvmtype, 1, 1)));
        } else if ("--".equals(op)) {
            assign = true;
            result = genSub(b, sl, value, createR(b, sl, value.getType(), LLVMConstInt(llvmtype, 1, 1)));
        } else {
            assign = false;
            result = switch (op) {
                case "!" -> genNot(b, sl, value);
                case "-" -> genNeg(b, sl, value);
                case "~" -> genLNeg(b, sl, value);
                case "&" -> genRef(b, sl, value);
                default -> null;
            };
        }

        if (result != null) {
            final var prev = expr.isRight()
                    ? value.get()
                    : null;
            if (assign)
                ((LValue) value).setValue(result.get());
            if (expr.isRight())
                return createR(b, sl, value.getType(), prev);
            return result;
        }

        throw new QScriptException(
                sl,
                "no such operator '%s%s'",
                op,
                value.getType());
    }

    private GenExpression() {
    }
}

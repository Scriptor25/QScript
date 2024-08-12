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
import static io.scriptor.backend.LValue.directOptL;
import static io.scriptor.backend.RValue.createOptR;
import static io.scriptor.backend.RValue.createR;
import static java.util.Optional.empty;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildCall2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildGEP2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildGlobalStringPtr;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildStructGEP2;
import static org.bytedeco.llvm.global.LLVM.LLVMConstInt;
import static org.bytedeco.llvm.global.LLVM.LLVMConstReal;
import static org.bytedeco.llvm.global.LLVM.LLVMInt32TypeInContext;

import java.util.Optional;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.expr.BinaryExpr;
import io.scriptor.frontend.expr.CallExpr;
import io.scriptor.frontend.expr.Expr;
import io.scriptor.frontend.expr.FloatExpr;
import io.scriptor.frontend.expr.FunctionExpr;
import io.scriptor.frontend.expr.IndexExpr;
import io.scriptor.frontend.expr.InitializerExpr;
import io.scriptor.frontend.expr.IntExpr;
import io.scriptor.frontend.expr.StringExpr;
import io.scriptor.frontend.expr.SymbolExpr;
import io.scriptor.frontend.expr.UnaryExpr;
import io.scriptor.type.ArrayType;
import io.scriptor.type.FunctionType;
import io.scriptor.type.PointerType;
import io.scriptor.type.StructType;
import io.scriptor.type.Type;
import io.scriptor.util.QScriptError;

public class GenExpression {

    public static Optional<Value> genExpr(final Builder b, final Expr expr) {
        if (expr instanceof BinaryExpr e)
            return genExpr(b, e);
        if (expr instanceof CallExpr e)
            return genExpr(b, e);
        if (expr instanceof FloatExpr e)
            return genExpr(b, e);
        if (expr instanceof FunctionExpr e)
            return genExpr(b, e);
        if (expr instanceof IndexExpr e)
            return genExpr(b, e);
        if (expr instanceof InitializerExpr e)
            return genExpr(b, e);
        if (expr instanceof IntExpr e)
            return genExpr(b, e);
        if (expr instanceof StringExpr e)
            return genExpr(b, e);
        if (expr instanceof SymbolExpr e)
            return genExpr(b, e);
        if (expr instanceof UnaryExpr e)
            return genExpr(b, e);

        QScriptError.print(expr.getSl(), "no genIR for class '%s':\n%s", expr.getClass(), expr);
        return Optional.empty();
    }

    public static Optional<Value> genExpr(final Builder b, final BinaryExpr expr) {

        final var sl = expr.getSl();

        final var l = genExpr(b, expr.getLHS());
        final var r = genExpr(b, expr.getRHS());

        if (l.isEmpty() || r.isEmpty())
            return empty();

        var op = expr.getOp();
        var left = l.get();
        var right = r.get();

        if ("=".equals(op)) {
            final var a = (LValue) left;
            final var v = genCast(b, sl, right, a.getType());
            if (v.isEmpty())
                return Optional.empty();
            a.setValue(v.get().get());
            return l;
        }

        if (left.getType() != right.getType()) {
            final var higher = Type.getHigherOrder(sl, left.getType(), right.getType());
            if (higher.isEmpty())
                return Optional.empty();

            final var lc = genCast(b, sl, left, higher.get());
            final var rc = genCast(b, sl, right, higher.get());

            if (lc.isEmpty() || rc.isEmpty())
                return empty();

            left = lc.get();
            right = rc.get();
        }

        Optional<Value> res = switch (op) {
            case "==" -> genEQ(b, sl, left, right);
            case "!=" -> genNE(b, sl, left, right);
            case "<" -> genLT(b, sl, left, right);
            case ">" -> genGT(b, sl, left, right);
            case "<=" -> genLE(b, sl, left, right);
            case ">=" -> genGE(b, sl, left, right);
            case "&&" -> genLAnd(b, sl, left, right);
            case "||" -> genLOr(b, sl, left, right);
            case "^^" -> genLXor(b, sl, left, right);
            default -> empty();
        };

        if (res.isPresent())
            return res;

        final var assign = op.contains("=");
        if (assign)
            op = op.replace("=", "");

        res = switch (op) {
            case "+" -> genAdd(b, sl, left, right);
            case "-" -> genSub(b, sl, left, right);
            case "*" -> genMul(b, sl, left, right);
            case "/" -> genDiv(b, sl, left, right);
            case "%" -> genRem(b, sl, left, right);
            case "&" -> genAnd(b, sl, left, right);
            case "|" -> genOr(b, sl, left, right);
            case "^" -> genXor(b, sl, left, right);
            default -> empty();
        };

        if (res.isPresent()) {
            if (assign) {
                final var a = (LValue) l.get();
                final var v = genCast(b, sl, res.get(), a.getType());
                if (v.isEmpty())
                    return empty();
                a.setValue(v.get().get());
                return l;
            }
            return res;
        }

        QScriptError.print(
                sl,
                "no such operator '%s %s %s'",
                left.getType(),
                expr.getOp(),
                right.getType());
        return empty();
    }

    public static Optional<Value> genExpr(final Builder b, final CallExpr expr) {
        final var sl = expr.getSl();

        final var opt = genExpr(b, expr.getCallee());
        if (opt.isEmpty())
            return empty();

        final var callee = opt.get();

        final var optbase = callee.getType().getPointerBase();
        final Optional<FunctionType> optty = optbase.isPresent() ? optbase.get().asFunction() : empty();
        if (optty.isEmpty())
            return empty();

        final var ty = optty.get();

        final var args = new PointerPointer<LLVMValueRef>(expr.getArgCount());
        for (int i = 0; i < expr.getArgCount(); ++i) {
            final var arg = genExpr(b, expr.getArg(i));
            if (arg.isEmpty()) {
                args.close();
                return empty();
            }

            final var argty = ty.getArg(i);
            final Optional<Value> cast;

            if (argty.isPresent()) {
                cast = genCast(b, sl, arg.get(), argty.get());
                if (cast.isEmpty()) {
                    args.close();
                    return empty();
                }
            } else {
                cast = arg;
            }

            args.put(i, cast.get().get());
        }

        final var result = LLVMBuildCall2(
                b.getBuilder(),
                genType(sl, ty).get(),
                callee.get(),
                args,
                expr.getArgCount(),
                "");

        args.close();
        return createOptR(b, sl, expr.getTy(), result);
    }

    public static Optional<Value> genExpr(final Builder b, final FloatExpr expr) {
        final var sl = expr.getSl();
        final var type = genType(sl, expr.getTy()).get();
        final var value = LLVMConstReal(type, expr.getVal());
        return createOptR(b, sl, expr.getTy(), value);
    }

    public static Optional<Value> genExpr(final Builder b, final FunctionExpr expr) {
        return Optional.empty();
    }

    public static Optional<Value> genExpr(final Builder b, final IndexExpr expr) {

        final var sl = expr.getSl();

        final var optp = genExpr(b, expr.getPtr());
        final var opti = genExpr(b, expr.getIdx());

        if (optp.isEmpty() || opti.isEmpty())
            return empty();

        final var ptr = optp.get();
        final var index = opti.get();

        final var arraytype = expr.getPtr().getTy();

        if (arraytype instanceof PointerType type) {
            final var ty = genType(sl, type.getBase()).get();

            final var indices = new PointerPointer<LLVMValueRef>(1);
            indices.put(0, index.get());

            final var gep = LLVMBuildGEP2(b.getBuilder(), ty, ptr.get(), indices, 1, "");

            indices.close();
            return directOptL(b, sl, type.getBase(), gep);
        }

        if (arraytype instanceof ArrayType type) {
            final var ty = genType(sl, type).get();

            final var indices = new PointerPointer<LLVMValueRef>(2);
            indices.put(0, LLVMConstInt(LLVMInt32TypeInContext(Builder.getContext()), 0, 1));
            indices.put(1, index.get());

            final var gep = LLVMBuildGEP2(b.getBuilder(), ty, ((LValue) ptr).getPtr(), indices, 2, "");

            indices.close();
            return directOptL(b, sl, type.getBase(), gep);
        }

        QScriptError.print(sl, "type must be an array or pointer type, but is '%s'", arraytype);
        return empty();
    }

    public static Optional<Value> genExpr(final Builder b, final InitializerExpr expr) {

        final var sl = expr.getSl();

        if (expr.getTy() instanceof StructType type) {
            final var ty = genType(sl, type).get();
            final var ptr = b.genAlloca(ty);

            for (int i = 0; i < expr.getArgCount(); ++i) {
                final var arg = genExpr(b, expr.getArg(i));
                if (arg.isEmpty())
                    return empty();

                final var cast = genCast(b, sl, arg.get(), type.getElement(i));
                if (cast.isEmpty())
                    return empty();

                final var gep = LLVMBuildStructGEP2(b.getBuilder(), ty, ptr, i, "");
                b.genStore(cast.get().get(), gep);
            }

            return directOptL(b, sl, type, ptr);
        }

        if (expr.getTy() instanceof ArrayType type) {
            final var ty = genType(sl, type).get();
            final var ptr = b.genAlloca(ty);

            final var indices = new PointerPointer<LLVMValueRef>(2);
            indices.put(0, LLVMConstInt(LLVMInt32TypeInContext(Builder.getContext()), 0, 1));
            for (int i = 0; i < expr.getArgCount(); ++i) {
                final var arg = genExpr(b, expr.getArg(i));
                if (arg.isEmpty())
                    return empty();

                final var cast = genCast(b, sl, arg.get(), type.getBase());
                if (cast.isEmpty())
                    return empty();

                indices.put(1, LLVMConstInt(LLVMInt32TypeInContext(Builder.getContext()), i, 1));

                final var gep = LLVMBuildGEP2(b.getBuilder(), ty, ptr, indices, 2, "");
                b.genStore(cast.get().get(), gep);
            }

            return directOptL(b, sl, type, ptr);
        }

        QScriptError.print(sl, "type must be an array or struct type, but is '%s'", expr.getTy());
        return empty();
    }

    public static Optional<Value> genExpr(final Builder b, final IntExpr expr) {

        final var sl = expr.getSl();
        final var type = expr.getTy();

        final var ty = genType(sl, type).get();
        final var value = LLVMConstInt(ty, expr.getVal(), 1);
        return createOptR(b, sl, type, value);
    }

    public static Optional<Value> genExpr(final Builder b, final StringExpr expr) {
        final var value = LLVMBuildGlobalStringPtr(b.getBuilder(), expr.getVal(), "");
        return createOptR(b, expr.getSl(), expr.getTy(), value);
    }

    public static Optional<Value> genExpr(final Builder b, final SymbolExpr expr) {
        return b.get(expr.getSl(), expr.getName());
    }

    public static Optional<Value> genExpr(final Builder b, final UnaryExpr expr) {

        final var sl = expr.getSl();

        final var o = expr.getOp();
        final var opt = genExpr(b, expr.getVal());
        if (opt.isEmpty())
            return empty();

        final var v = opt.get();
        final var t = v.getLLVMType();

        final boolean a;
        final Optional<Value> r;

        if ("++".equals(o)) {
            a = true;
            r = genAdd(b, sl, v, createR(b, sl, v.getType(), LLVMConstInt(t, 1, 1)));
        } else if ("--".equals(o)) {
            a = true;
            r = genSub(b, sl, v, createR(b, sl, v.getType(), LLVMConstInt(t, 1, 1)));
        } else {
            a = false;
            r = switch (o) {
                case "!" -> genNot(b, sl, v);
                case "-" -> genNeg(b, sl, v);
                case "~" -> genLNeg(b, sl, v);
                case "&" -> genRef(b, sl, v);
                default -> Optional.empty();
            };
        }

        if (r.isPresent()) {
            final var p = expr.isRight()
                    ? v.get()
                    : null;
            if (a)
                ((LValue) v).setValue(r.get().get());
            if (expr.isRight())
                return createOptR(b, sl, v.getType(), p);
            return r;
        }

        QScriptError.print(sl, "no such operator '%s%s'", o, v.getType());
        return empty();
    }

    private GenExpression() {
    }
}

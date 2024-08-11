package io.scriptor.backend;

import static org.bytedeco.llvm.global.LLVM.LLVMAddFunction;
import static org.bytedeco.llvm.global.LLVM.LLVMAddGlobal;
import static org.bytedeco.llvm.global.LLVM.LLVMAppendBasicBlockInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMArrayType2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildAdd;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildAlloca;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildBr;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildCall2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildCondBr;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFAdd;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFCmp;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFDiv;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFMul;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFPCast;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFPToSI;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildFSub;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildGEP2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildGlobalStringPtr;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildICmp;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildIntCast2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildIntToPtr;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildLoad2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildMul;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildPointerCast;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildPtrToInt;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildRet;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildRetVoid;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildSDiv;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildSIToFP;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildStore;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildStructGEP2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildSub;
import static org.bytedeco.llvm.global.LLVM.LLVMClearInsertionPosition;
import static org.bytedeco.llvm.global.LLVM.LLVMCodeGenLevelDefault;
import static org.bytedeco.llvm.global.LLVM.LLVMCodeModelDefault;
import static org.bytedeco.llvm.global.LLVM.LLVMConstInt;
import static org.bytedeco.llvm.global.LLVM.LLVMConstReal;
import static org.bytedeco.llvm.global.LLVM.LLVMCountBasicBlocks;
import static org.bytedeco.llvm.global.LLVM.LLVMCreateBuilderInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMCreateFunctionPassManagerForModule;
import static org.bytedeco.llvm.global.LLVM.LLVMCreateTargetMachine;
import static org.bytedeco.llvm.global.LLVM.LLVMDoubleTypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMDumpModule;
import static org.bytedeco.llvm.global.LLVM.LLVMFloatTypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMFunctionType;
import static org.bytedeco.llvm.global.LLVM.LLVMGetBasicBlockParent;
import static org.bytedeco.llvm.global.LLVM.LLVMGetBasicBlockTerminator;
import static org.bytedeco.llvm.global.LLVM.LLVMGetDefaultTargetTriple;
import static org.bytedeco.llvm.global.LLVM.LLVMGetEntryBasicBlock;
import static org.bytedeco.llvm.global.LLVM.LLVMGetFirstInstruction;
import static org.bytedeco.llvm.global.LLVM.LLVMGetInsertBlock;
import static org.bytedeco.llvm.global.LLVM.LLVMGetNamedFunction;
import static org.bytedeco.llvm.global.LLVM.LLVMGetNextInstruction;
import static org.bytedeco.llvm.global.LLVM.LLVMGetParam;
import static org.bytedeco.llvm.global.LLVM.LLVMGetTargetFromTriple;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeAllAsmParsers;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeAllAsmPrinters;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeAllTargetInfos;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeAllTargetMCs;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeAllTargets;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeFunctionPassManager;
import static org.bytedeco.llvm.global.LLVM.LLVMInt16TypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMInt1TypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMInt32TypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMInt64TypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMInt8TypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMIntNE;
import static org.bytedeco.llvm.global.LLVM.LLVMIntSLT;
import static org.bytedeco.llvm.global.LLVM.LLVMIsAAllocaInst;
import static org.bytedeco.llvm.global.LLVM.LLVMLinkModules2;
import static org.bytedeco.llvm.global.LLVM.LLVMModuleCreateWithNameInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMObjectFile;
import static org.bytedeco.llvm.global.LLVM.LLVMPointerType;
import static org.bytedeco.llvm.global.LLVM.LLVMPositionBuilderAtEnd;
import static org.bytedeco.llvm.global.LLVM.LLVMPositionBuilderBefore;
import static org.bytedeco.llvm.global.LLVM.LLVMPrintMessageAction;
import static org.bytedeco.llvm.global.LLVM.LLVMRealOLT;
import static org.bytedeco.llvm.global.LLVM.LLVMRealONE;
import static org.bytedeco.llvm.global.LLVM.LLVMRelocPIC;
import static org.bytedeco.llvm.global.LLVM.LLVMRunFunctionPassManager;
import static org.bytedeco.llvm.global.LLVM.LLVMSetInitializer;
import static org.bytedeco.llvm.global.LLVM.LLVMSetValueName;
import static org.bytedeco.llvm.global.LLVM.LLVMStructTypeInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMTargetMachineEmitToFile;
import static org.bytedeco.llvm.global.LLVM.LLVMVerifyFunction;
import static org.bytedeco.llvm.global.LLVM.LLVMVerifyModule;
import static org.bytedeco.llvm.global.LLVM.LLVMVoidTypeInContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMBuilderRef;
import org.bytedeco.llvm.LLVM.LLVMContextRef;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMPassManagerRef;
import org.bytedeco.llvm.LLVM.LLVMTargetRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.Context;
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

public class Builder {

    public static void mergeAndEmitToFile(final Builder[] builders, final String filename) {
        final var mainModule = builders[0].module;
        final var mainName = builders[0].filename;
        for (int i = 1; i < builders.length; ++i) {
            final var module = builders[i].module;
            final var name = builders[i].filename;
            final var error = new PointerPointer<>();
            if (LLVMVerifyModule(module, LLVMPrintMessageAction, error) != 0)
                throw new QScriptException(
                        "failed to verify module '%s': %s",
                        name,
                        !error.isNull() ? error.getString(0) : "no detail");
            if (LLVMLinkModules2(mainModule, module) != 0)
                throw new QScriptException("failed to link against '%s's", name);
            if (LLVMVerifyModule(mainModule, LLVMPrintMessageAction, error) != 0)
                throw new QScriptException(
                        "failed to verify module '%s': %s",
                        mainName,
                        !error.isNull() ? error.getString(0) : "no detail");
        }
        emitToFile(mainModule, filename);
    }

    private static void emitToFile(final LLVMModuleRef module, final String filename) {
        LLVMInitializeAllTargetInfos();
        LLVMInitializeAllTargets();
        LLVMInitializeAllTargetMCs();
        LLVMInitializeAllAsmParsers();
        LLVMInitializeAllAsmPrinters();

        final var triple = LLVMGetDefaultTargetTriple();

        final var error = new PointerPointer<>();
        final var target = new LLVMTargetRef();
        if (LLVMGetTargetFromTriple(triple, target, error) != 0) {
            throw new QScriptException(
                    "failed to get target from triple '%s': %s",
                    triple.getString(),
                    error.getString(0));
        }

        final var cpu = "generic";
        final var features = "";

        final var machine = LLVMCreateTargetMachine(
                target,
                triple.getString(),
                cpu,
                features,
                LLVMCodeGenLevelDefault,
                LLVMRelocPIC,
                LLVMCodeModelDefault);

        if (LLVMTargetMachineEmitToFile(machine, module, filename, LLVMObjectFile, error.asByteBuffer()) != 0) {
            throw new QScriptException("failed to emit to file '%s': %s", filename, error.getString(0));
        }
    }

    private final String filename;
    private final Context ctx;

    private final Stack<Map<String, Value>> stack = new Stack<>();

    private final LLVMContextRef context;
    private final LLVMBuilderRef builder;
    private final LLVMModuleRef module;
    private final LLVMPassManagerRef fpm;

    public Builder(final Context ctx, final String filename) {
        this.filename = filename;
        this.ctx = ctx;

        stack.push(new HashMap<>());

        this.context = ctx.getLLVM();
        this.builder = LLVMCreateBuilderInContext(context);
        this.module = LLVMModuleCreateWithNameInContext(filename, context);
        this.fpm = LLVMCreateFunctionPassManagerForModule(module);
        // LLVMAddInstructionCombiningPass(fpm);
        // LLVMAddReassociatePass(fpm);
        // LLVMAddGVNPass(fpm);
        // LLVMAddCFGSimplificationPass(fpm);
        LLVMInitializeFunctionPassManager(fpm);
    }

    public void dumpModule() {
        LLVMDumpModule(module);
    }

    public void emitToFile(final String filename) {
        emitToFile(module, filename);
    }

    public boolean isGlobal() {
        final var bb = LLVMGetInsertBlock(builder);
        return bb == null;
    }

    public LLVMValueRef createAlloca(final LLVMTypeRef type) {
        final var bkp = LLVMGetInsertBlock(builder);

        final var f = LLVMGetBasicBlockParent(LLVMGetInsertBlock(builder));
        final var entry = LLVMGetEntryBasicBlock(f);
        LLVMPositionBuilderAtEnd(builder, entry);

        var instr = LLVMGetFirstInstruction(entry);
        while (instr != null && LLVMIsAAllocaInst(instr) != null)
            instr = LLVMGetNextInstruction(instr);

        if (instr != null) {
            LLVMPositionBuilderBefore(builder, instr);
        } else {
            LLVMPositionBuilderAtEnd(builder, entry);
        }

        final var alloca = LLVMBuildAlloca(builder, type, "");

        LLVMPositionBuilderAtEnd(builder, bkp);
        return alloca;
    }

    public LLVMValueRef createLoad(final LLVMTypeRef base, final LLVMValueRef ptr) {
        return LLVMBuildLoad2(builder, base, ptr, "");
    }

    public LLVMValueRef createStore(final LLVMValueRef value, final LLVMValueRef ptr) {
        return LLVMBuildStore(builder, value, ptr);
    }

    public Value createCast(final Value value, final Type type) {
        final var vtype = value.getType();
        if (vtype == type || type == null)
            return value;

        final var llvmvalue = value.get();
        final var llvmtype = genIR(type);

        if (vtype.isInt()) {
            if (type.isInt()) {
                final var result = LLVMBuildIntCast2(builder, llvmvalue, llvmtype, 1, "");
                return RValue.create(this, type, result);
            }

            if (type.isFlt()) {
                final var result = LLVMBuildSIToFP(builder, llvmvalue, llvmtype, "");
                return RValue.create(this, type, result);
            }

            if (type.isPtr()) {
                final var result = LLVMBuildIntToPtr(builder, llvmvalue, llvmtype, "");
                return RValue.create(this, type, result);
            }
        }

        if (vtype.isFlt()) {
            if (type.isInt()) {
                final var result = LLVMBuildFPToSI(builder, llvmvalue, llvmtype, "");
                return RValue.create(this, type, result);
            }

            if (type.isFlt()) {
                final var result = LLVMBuildFPCast(builder, llvmvalue, llvmtype, "");
                return RValue.create(this, type, result);
            }
        }

        if (vtype.isPtr()) {
            if (type.isInt()) {
                final var result = LLVMBuildPtrToInt(builder, llvmvalue, llvmtype, "");
                return RValue.create(this, type, result);
            }

            if (type.isPtr()) {
                final var result = LLVMBuildPointerCast(builder, llvmvalue, llvmtype, "");
                return RValue.create(this, type, result);
            }
        }

        throw new QScriptException("cannot cast from '%s' to '%s'", vtype, type);
    }

    public Value createEQ(final Value left, final Value right) {
        return null;
    }

    public Value createNE(final Value left, final Value right) {
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildICmp(builder, LLVMIntNE, left.get(), right.get(), "");
            return RValue.create(this, Type.getInt1(ctx), result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFCmp(builder, LLVMRealONE, left.get(), right.get(), "");
            return RValue.create(this, Type.getInt1(ctx), result);
        }

        return null;
    }

    public Value createLT(final Value left, final Value right) {
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildICmp(builder, LLVMIntSLT, left.get(), right.get(), "");
            return RValue.create(this, Type.getInt1(ctx), result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFCmp(builder, LLVMRealOLT, left.get(), right.get(), "");
            return RValue.create(this, Type.getInt1(ctx), result);
        }

        return null;
    }

    public Value createGT(final Value left, final Value right) {
        return null;
    }

    public Value createLE(final Value left, final Value right) {
        return null;
    }

    public Value createGE(final Value left, final Value right) {
        return null;
    }

    public Value createLAnd(final Value left, final Value right) {
        return null;
    }

    public Value createLOr(final Value left, final Value right) {
        return null;
    }

    public Value createLXOr(final Value left, final Value right) {
        return null;
    }

    public Value createAdd(final Value left, final Value right) {
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildAdd(builder, left.get(), right.get(), "");
            return RValue.create(this, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFAdd(builder, left.get(), right.get(), "");
            return RValue.create(this, type, result);
        }

        return null;
    }

    public Value createSub(final Value left, final Value right) {
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildSub(builder, left.get(), right.get(), "");
            return RValue.create(this, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFSub(builder, left.get(), right.get(), "");
            return RValue.create(this, type, result);
        }

        return null;
    }

    public Value createMul(final Value left, final Value right) {
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildMul(builder, left.get(), right.get(), "");
            return RValue.create(this, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFMul(builder, left.get(), right.get(), "");
            return RValue.create(this, type, result);
        }

        return null;
    }

    public Value createDiv(final Value left, final Value right) {
        final var type = left.getType();

        if (type.isInt()) {
            final var result = LLVMBuildSDiv(builder, left.get(), right.get(), "");
            return RValue.create(this, type, result);
        }

        if (type.isFlt()) {
            final var result = LLVMBuildFDiv(builder, left.get(), right.get(), "");
            return RValue.create(this, type, result);
        }

        return null;
    }

    public Value createRem(final Value left, final Value right) {
        return null;
    }

    public Value createAnd(final Value left, final Value right) {
        return null;
    }

    public Value createOr(final Value left, final Value right) {
        return null;
    }

    public Value createXOr(final Value left, final Value right) {
        return null;
    }

    public Value createNot(final Value value) {
        return null;
    }

    public Value createNeg(final Value value) {
        return null;
    }

    public Value createLNeg(final Value value) {
        return null;
    }

    public LLVMTypeRef genIR(final Type type) {
        if (type.isFunction()) {
            final var t = (FunctionType) type;
            final var rt = genIR(t.getResult());
            final var pt = new PointerPointer<LLVMTypeRef>(t.getArgCount());
            for (int i = 0; i < t.getArgCount(); ++i)
                pt.put(i, genIR(t.getArg(i)));
            return LLVMFunctionType(rt, pt, t.getArgCount(), t.isVarArg() ? 1 : 0);
        }

        if (type.isStruct()) {
            final var t = (StructType) type;
            final var et = new PointerPointer<LLVMTypeRef>(t.getElementCount());
            for (int i = 0; i < t.getElementCount(); ++i)
                et.put(i, genIR(t.getElement(i)));
            return LLVMStructTypeInContext(context, et, t.getElementCount(), 0);
        }

        if (type.isPtr())
            return LLVMPointerType(genIR(((PointerType) type).getBase()), 0);

        if (type.isArray())
            return LLVMArrayType2(genIR(((ArrayType) type).getBase()), ((ArrayType) type).getLength());

        if (type.isVoid())
            return LLVMVoidTypeInContext(context);

        if (type.isInt1())
            return LLVMInt1TypeInContext(context);
        if (type.isInt8())
            return LLVMInt8TypeInContext(context);
        if (type.isInt16())
            return LLVMInt16TypeInContext(context);
        if (type.isInt32())
            return LLVMInt32TypeInContext(context);
        if (type.isInt64())
            return LLVMInt64TypeInContext(context);

        if (type.isFlt32())
            return LLVMFloatTypeInContext(context);
        if (type.isFlt64())
            return LLVMDoubleTypeInContext(context);

        throw new QScriptException("no genIR for class '%s': %s", type.getClass(), type);
    }

    public void genIR(final Statement stmt) {
        if (stmt instanceof Expression e) {
            genIR(e);
            return;
        }
        if (stmt instanceof CompoundStatement s) {
            genIR(s);
            return;
        }
        if (stmt instanceof DefFunStatement s) {
            genIR(s);
            return;
        }
        if (stmt instanceof DefVarStatement s) {
            genIR(s);
            return;
        }
        if (stmt instanceof IfStatement s) {
            genIR(s);
            return;
        }
        if (stmt instanceof ReturnStatement s) {
            genIR(s);
            return;
        }
        if (stmt instanceof WhileStatement s) {
            genIR(s);
            return;
        }

        throw new QScriptException(stmt.getLocation(), "no genIR for class '%s':\n%s", stmt.getClass(), stmt);
    }

    public Value genIR(final Expression expr) {
        if (expr instanceof BinaryExpression e)
            return genIR(e);
        if (expr instanceof CallExpression e)
            return genIR(e);
        if (expr instanceof FloatExpression e)
            return genIR(e);
        if (expr instanceof FunctionExpression e)
            return genIR(e);
        if (expr instanceof SymbolExpression e)
            return genIR(e);
        if (expr instanceof IndexExpression e)
            return genIR(e);
        if (expr instanceof IntExpression e)
            return genIR(e);
        if (expr instanceof StringExpression e)
            return genIR(e);
        if (expr instanceof InitListExpression e)
            return genIR(e);
        if (expr instanceof UnaryExpression e)
            return genIR(e);

        throw new QScriptException(expr.getLocation(), "no genIR for class '%s':\n%s", expr.getClass(), expr);
    }

    private Value genIR(final BinaryExpression expr) {
        var op = expr.getOperator();

        if ("=".equals(op)) {
            final var assignee = (LValue) genIR(expr.getLHS());
            final var value = createCast(genIR(expr.getRHS()), assignee.getType());
            assignee.setValue(value.get());
            return assignee;
        }

        var left = genIR(expr.getLHS());
        var right = genIR(expr.getRHS());

        if (left.getType() != right.getType()) {
            final var higher = Type.getHigherOrder(expr.getLocation(), left.getType(), right.getType());
            left = createCast(left, higher);
            right = createCast(right, higher);
        }

        Value result = switch (op) {
            case "==" -> createEQ(left, right);
            case "!=" -> createNE(left, right);
            case "<" -> createLT(left, right);
            case ">" -> createGT(left, right);
            case "<=" -> createLE(left, right);
            case ">=" -> createGE(left, right);
            case "&&" -> createLAnd(left, right);
            case "||" -> createLOr(left, right);
            case "^^" -> createLXOr(left, right);
            default -> null;
        };

        if (result != null)
            return result;

        final var assign = op.contains("=");
        if (assign)
            op = op.replace("=", "");

        result = switch (op) {
            case "+" -> createAdd(left, right);
            case "-" -> createSub(left, right);
            case "*" -> createMul(left, right);
            case "/" -> createDiv(left, right);
            case "%" -> createRem(left, right);
            case "&" -> createAnd(left, right);
            case "|" -> createOr(left, right);
            case "^" -> createXOr(left, right);
            default -> null;
        };

        if (result != null) {
            if (assign) {
                final var assignee = (LValue) genIR(expr.getLHS());
                final var value = createCast(result, assignee.getType());
                assignee.setValue(value.get());
                return assignee;
            }
            return result;
        }

        throw new QScriptException(
                expr.getLocation(),
                "no such operator '%s %s %s'",
                left.getType(),
                expr.getOperator(),
                right.getType());
    }

    private Value genIR(final CallExpression expr) {
        final var callee = genIR(expr.getCallee());
        final var fnty = (FunctionType) callee.getType();
        final var args = new PointerPointer<LLVMValueRef>(expr.getArgCount());
        for (int i = 0; i < expr.getArgCount(); ++i)
            args.put(i, createCast(genIR(expr.getArg(i)), fnty.getArg(i)).get());
        final var result = LLVMBuildCall2(
                builder,
                callee.getLLVMType(),
                callee.get(),
                args,
                expr.getArgCount(),
                "");
        return RValue.create(this, expr.getType(), result);
    }

    private void genIR(final CompoundStatement expr) {
        stack.push(new HashMap<>());
        for (int i = 0; i < expr.getCount(); ++i)
            genIR(expr.get(i));
        stack.pop();
    }

    private void genIR(final DefFunStatement stmt) {
        final var ft = genIR(stmt.getFunctionType());

        var f = LLVMGetNamedFunction(module, stmt.getName());
        if (f == null) {
            f = LLVMAddFunction(module, stmt.getName(), ft);
            if (f == null)
                throw new QScriptException(
                        stmt.getLocation(),
                        "failed to create function '%s':\n%s",
                        stmt.getName(),
                        stmt);
        }

        stack.peek().put(stmt.getName(), RValue.create(this, stmt.getFunctionType(), f));

        if (stmt.getBody() == null)
            return;

        if (LLVMCountBasicBlocks(f) != 0)
            throw new QScriptException(stmt.getLocation(), "cannot redefine function '%s'", stmt.getName());

        final var entry = LLVMAppendBasicBlockInContext(context, f, "entry");
        LLVMPositionBuilderAtEnd(builder, entry);

        stack.push(new HashMap<>());
        for (int i = 0; i < stmt.getArgCount(); ++i) {
            final var arg = stmt.getArg(i);

            final var llvmarg = LLVMGetParam(f, i);
            LLVMSetValueName(llvmarg, arg.name());

            final var value = LValue.alloca(this, arg.type(), llvmarg);
            stack.peek().put(arg.name(), value);
        }

        genIR(stmt.getBody());

        LLVMClearInsertionPosition(builder);
        stack.pop();

        if (LLVMVerifyFunction(f, LLVMPrintMessageAction) != 0) {
            throw new QScriptException(stmt.getLocation(), "failed to verify function");
        }

        LLVMRunFunctionPassManager(fpm, f);
    }

    private void genIR(final DefVarStatement stmt) {
        final var name = stmt.getName();

        if (isGlobal()) {
            final var vt = genIR(stmt.getType());

            final var ptr = LLVMAddGlobal(module, vt, name);
            final var value = LValue.direct(this, stmt.getType(), ptr);

            if (stmt.hasInit()) {
                if (stmt.getInit().isConst()) {
                    final var init = genIR(stmt.getInit());
                    LLVMSetInitializer(ptr, init.get());
                } else {
                    throw new QScriptException(stmt.getLocation(), "global initializer must be constant");
                }
            }

            stack.peek().put(name, value);
            return;
        }

        final Value value;
        if (stmt.hasInit()) {
            final var init = createCast(genIR(stmt.getInit()), stmt.getType());
            value = LValue.copy(this, init);
        } else {
            value = LValue.alloca(this, stmt.getType());
        }

        stack.peek().put(name, value);
    }

    private Value genIR(final FloatExpression expr) {
        final var type = genIR(expr.getType());
        final var value = LLVMConstReal(type, expr.getValue());
        return RValue.create(this, expr.getType(), value);
    }

    private Value genIR(final FunctionExpression expr) {
        throw new QScriptException(expr.getLocation(), "TODO");
    }

    private Value genIR(final SymbolExpression expr) {
        final var id = expr.getId();
        for (int i = stack.size() - 1; i >= 0; --i) {
            final var vars = stack.get(i);
            if (vars.containsKey(id))
                return vars.get(id);
        }

        throw new QScriptException(expr.getLocation(), "undefined symbol '%s'", expr.getId());
    }

    private void genIR(final IfStatement expr) {

        final var f = LLVMGetBasicBlockParent(LLVMGetInsertBlock(builder));
        final var head = LLVMAppendBasicBlockInContext(context, f, "head");
        final var then = LLVMAppendBasicBlockInContext(context, f, "then");
        final var else_ = LLVMAppendBasicBlockInContext(context, f, expr.hasElse() ? "else" : "end");
        final var end = expr.hasElse() ? LLVMAppendBasicBlockInContext(context, f, "end") : else_;

        LLVMBuildBr(builder, head);

        LLVMPositionBuilderAtEnd(builder, head);
        final var condition = genIR(expr.getCondition());
        LLVMBuildCondBr(builder, condition.get(), then, else_);

        LLVMPositionBuilderAtEnd(builder, then);
        genIR(expr.getThen());
        if (LLVMGetBasicBlockTerminator(LLVMGetInsertBlock(builder)) == null)
            LLVMBuildBr(builder, end);

        if (expr.hasElse()) {
            LLVMPositionBuilderAtEnd(builder, else_);
            genIR(expr.getElse());
            if (LLVMGetBasicBlockTerminator(LLVMGetInsertBlock(builder)) == null)
                LLVMBuildBr(builder, end);
        }

        LLVMPositionBuilderAtEnd(builder, end);
    }

    private Value genIR(final IndexExpression expr) {
        final var ptr = genIR(expr.getPtr());
        final var index = genIR(expr.getIndex());

        final var arraytype = expr.getPtr().getType();

        if (arraytype instanceof PointerType type) {
            final var llvmbase = genIR(type.getBase());

            final var indices = new PointerPointer<LLVMValueRef>(1);
            indices.put(0, index.get());

            final var gep = LLVMBuildGEP2(builder, llvmbase, ptr.get(), indices, 1, "");
            return LValue.direct(this, type.getBase(), gep);
        }

        if (arraytype instanceof ArrayType type) {
            final var llvmtype = genIR(type);

            final var indices = new PointerPointer<LLVMValueRef>(2);
            indices.put(0, LLVMConstInt(LLVMInt32TypeInContext(context), 0, 1));
            indices.put(1, index.get());
            final var gep = LLVMBuildGEP2(builder, llvmtype, ((LValue) ptr).getPtr(), indices, 2, "");
            return LValue.direct(this, type.getBase(), gep);
        }

        throw new QScriptException(
                expr.getLocation(),
                "type must be an array or pointer type, but is '%s'",
                arraytype);
    }

    private Value genIR(final IntExpression expr) {
        final var type = genIR(expr.getType());
        final var value = LLVMConstInt(type, expr.getValue(), 1);
        return RValue.create(this, expr.getType(), value);
    }

    private void genIR(final ReturnStatement expr) {
        if (!expr.hasExpr()) {
            LLVMBuildRetVoid(builder);
            return;
        }

        final var value = createCast(genIR(expr.getExpr()), expr.getResult());
        LLVMBuildRet(builder, value.get());
        return;
    }

    private Value genIR(final StringExpression expr) {
        final var value = LLVMBuildGlobalStringPtr(builder, expr.getValue(), "");
        return RValue.create(this, expr.getType(), value);
    }

    private Value genIR(final InitListExpression expr) {
        if (expr.getType() instanceof StructType type) {
            final var llvmtype = genIR(type);
            final var ptr = createAlloca(llvmtype);

            for (int i = 0; i < expr.getArgCount(); ++i) {
                final var val = createCast(genIR(expr.getArg(i)), type.getElement(i)).get();
                final var gep = LLVMBuildStructGEP2(builder, llvmtype, ptr, i, "");
                createStore(val, gep);
            }

            return LValue.direct(this, type, ptr);
        }

        if (expr.getType() instanceof ArrayType type) {
            final var llvmtype = genIR(type);
            final var ptr = createAlloca(llvmtype);

            final var indices = new PointerPointer<LLVMValueRef>(2);
            indices.put(0, LLVMConstInt(LLVMInt32TypeInContext(context), 0, 1));
            for (int i = 0; i < expr.getArgCount(); ++i) {
                final var val = createCast(genIR(expr.getArg(i)), type.getBase()).get();
                indices.put(1, LLVMConstInt(LLVMInt32TypeInContext(context), i, 1));
                final var gep = LLVMBuildGEP2(builder, llvmtype, ptr, indices, 2, "");
                createStore(val, gep);
            }

            return LValue.direct(this, type, ptr);
        }

        throw new QScriptException(
                expr.getLocation(),
                "type must be an array or struct type, but is '%s'",
                expr.getType());
    }

    private Value genIR(final UnaryExpression expr) {
        final var op = expr.getOperator();
        final var value = genIR(expr.getOperand());

        final var llvmtype = value.getLLVMType();

        final boolean assign;
        final Value result;

        if ("++".equals(op)) {
            assign = true;
            result = createAdd(value, RValue.create(this, value.getType(), LLVMConstInt(llvmtype, 1, 1)));
        } else if ("--".equals(op)) {
            assign = true;
            result = createSub(value, RValue.create(this, value.getType(), LLVMConstInt(llvmtype, 1, 1)));
        } else {
            assign = false;
            result = switch (op) {
                case "!" -> createNot(value);
                case "-" -> createNeg(value);
                case "~" -> createLNeg(value);
                default -> null;
            };
        }

        if (result != null) {
            final var prev = value.get();
            if (assign)
                ((LValue) value).setValue(result.get());
            if (expr.isRight())
                return RValue.create(this, value.getType(), prev);
            return result;
        }

        throw new QScriptException(
                expr.getLocation(),
                "no such operator '%s%s'",
                op,
                value.getType());
    }

    private void genIR(final WhileStatement expr) {

        final var f = LLVMGetBasicBlockParent(LLVMGetInsertBlock(builder));
        final var head = LLVMAppendBasicBlockInContext(context, f, "head");
        final var loop = LLVMAppendBasicBlockInContext(context, f, "loop");
        final var end = LLVMAppendBasicBlockInContext(context, f, "end");

        LLVMBuildBr(builder, head);

        LLVMPositionBuilderAtEnd(builder, head);
        final var condition = genIR(expr.getCondition());
        LLVMBuildCondBr(builder, condition.get(), loop, end);

        LLVMPositionBuilderAtEnd(builder, loop);
        genIR(expr.getLoop());
        LLVMBuildBr(builder, head);

        LLVMPositionBuilderAtEnd(builder, end);
    }
}

package io.scriptor.backend;

import static io.scriptor.backend.GenStatement.genStmt;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildAlloca;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildLoad2;
import static org.bytedeco.llvm.global.LLVM.LLVMBuildStore;
import static org.bytedeco.llvm.global.LLVM.LLVMCodeGenLevelDefault;
import static org.bytedeco.llvm.global.LLVM.LLVMCodeModelDefault;
import static org.bytedeco.llvm.global.LLVM.LLVMContextCreate;
import static org.bytedeco.llvm.global.LLVM.LLVMContextDispose;
import static org.bytedeco.llvm.global.LLVM.LLVMCreateBuilderInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMCreateTargetMachine;
import static org.bytedeco.llvm.global.LLVM.LLVMDisposeBuilder;
import static org.bytedeco.llvm.global.LLVM.LLVMDisposeModule;
import static org.bytedeco.llvm.global.LLVM.LLVMDumpModule;
import static org.bytedeco.llvm.global.LLVM.LLVMGetBasicBlockParent;
import static org.bytedeco.llvm.global.LLVM.LLVMGetDefaultTargetTriple;
import static org.bytedeco.llvm.global.LLVM.LLVMGetEntryBasicBlock;
import static org.bytedeco.llvm.global.LLVM.LLVMGetFirstInstruction;
import static org.bytedeco.llvm.global.LLVM.LLVMGetInsertBlock;
import static org.bytedeco.llvm.global.LLVM.LLVMGetNextInstruction;
import static org.bytedeco.llvm.global.LLVM.LLVMGetTargetFromTriple;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeAsmParser;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeAsmPrinter;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeTarget;
import static org.bytedeco.llvm.global.LLVM.LLVMIsAAllocaInst;
import static org.bytedeco.llvm.global.LLVM.LLVMLinkModules2;
import static org.bytedeco.llvm.global.LLVM.LLVMModuleCreateWithNameInContext;
import static org.bytedeco.llvm.global.LLVM.LLVMObjectFile;
import static org.bytedeco.llvm.global.LLVM.LLVMPositionBuilderAtEnd;
import static org.bytedeco.llvm.global.LLVM.LLVMPositionBuilderBefore;
import static org.bytedeco.llvm.global.LLVM.LLVMPrintMessageAction;
import static org.bytedeco.llvm.global.LLVM.LLVMRelocPIC;
import static org.bytedeco.llvm.global.LLVM.LLVMTargetMachineEmitToFile;
import static org.bytedeco.llvm.global.LLVM.LLVMVerifyModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMBuilderRef;
import org.bytedeco.llvm.LLVM.LLVMContextRef;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMTargetRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import io.scriptor.frontend.StackFrame;
import io.scriptor.frontend.stmt.Stmt;
import io.scriptor.util.QScriptException;

public class Builder {

    private static boolean validContext;
    private static LLVMContextRef context;

    public static void createContext() {
        if (!validContext) {
            context = LLVMContextCreate();
            validContext = true;
        }
    }

    public static void disposeContext() {
        if (validContext) {
            LLVMContextDispose(context);
            validContext = false;
        }
    }

    public static LLVMContextRef getContext() {
        assert validContext;
        return context;
    }

    private boolean validBuilder = true;
    private boolean validModule = true;

    private final String name;
    private final StackFrame frame;
    private final Stack<Map<String, Value>> stack = new Stack<>();

    private final LLVMBuilderRef builder;
    private final LLVMModuleRef module;

    public Builder(final StackFrame frame, final String name) {
        this.name = name;
        this.frame = frame;

        stack.push(new HashMap<>());

        this.builder = LLVMCreateBuilderInContext(context);
        this.module = LLVMModuleCreateWithNameInContext(name, context);
    }

    public void dispose() {
        if (validBuilder) {
            LLVMDisposeBuilder(builder);
            validBuilder = false;
        }
        if (validModule) {
            LLVMDisposeModule(module);
            validModule = false;
        }
    }

    public String getName() {
        return name;
    }

    public StackFrame getFrame() {
        return frame;
    }

    public void push() {
        stack.push(new HashMap<>());
    }

    public void pop() {
        stack.pop();
    }

    public void put(final String name, final Value value) {
        stack.peek().put(name, value);
    }

    public boolean contains(final String name) {
        for (int i = stack.size() - 1; i >= 0; --i)
            if (stack.get(i).containsKey(name))
                return true;
        return false;
    }

    public Value get(final String name) {
        for (int i = stack.size() - 1; i >= 0; --i)
            if (stack.get(i).containsKey(name))
                return stack.get(i).get(name);
        return null;
    }

    public LLVMBuilderRef getBuilder() {
        assert validBuilder;
        return builder;
    }

    public LLVMModuleRef getModule() {
        assert validModule;
        return module;
    }

    public void dumpModule() {
        assert validModule;
        LLVMDumpModule(module);
    }

    public void link(final Builder src) {
        assert validModule;
        assert src.validModule;

        final var srcModule = src.module;
        final var srcName = src.name;
        final var error = new PointerPointer<>();

        if (LLVMVerifyModule(srcModule, LLVMPrintMessageAction, error) != 0)
            throw new QScriptException(
                    null,
                    "failed to verify module '%s': %s",
                    srcName,
                    !error.isNull() ? error.getString(0) : "no detail");

        if (LLVMLinkModules2(module, srcModule) != 0)
            throw new QScriptException(null, "failed to link against '%s'", srcName);

        if (LLVMVerifyModule(module, LLVMPrintMessageAction, error) != 0)
            throw new QScriptException(
                    null,
                    "failed to verify module '%s': %s",
                    name,
                    !error.isNull() ? error.getString(0) : "no detail");

        src.validModule = false;
    }

    public void emitToFile(final String filename) {
        assert validModule;

        LLVMInitializeNativeTarget();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeAsmPrinter();

        final var triple = LLVMGetDefaultTargetTriple();

        final var error = new PointerPointer<>();
        final var target = new LLVMTargetRef();
        if (LLVMGetTargetFromTriple(triple, target, error) != 0) {
            throw new QScriptException(
                    null,
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
            throw new QScriptException(null, "failed to emit to file '%s': %s", filename, error.getString(0));
        }
    }

    public boolean isGlobal() {
        assert validBuilder;
        return LLVMGetInsertBlock(builder) == null;
    }

    public LLVMValueRef genAlloca(final LLVMTypeRef ty) {
        assert validBuilder;

        final var bb = LLVMGetInsertBlock(builder);
        final var f = LLVMGetBasicBlockParent(bb);
        final var ebb = LLVMGetEntryBasicBlock(f);
        LLVMPositionBuilderAtEnd(builder, ebb);

        var instr = LLVMGetFirstInstruction(ebb);
        while (instr != null && LLVMIsAAllocaInst(instr) != null)
            instr = LLVMGetNextInstruction(instr);

        if (instr != null) {
            LLVMPositionBuilderBefore(builder, instr);
        } else {
            LLVMPositionBuilderAtEnd(builder, ebb);
        }

        final var alloca = LLVMBuildAlloca(builder, ty, "");

        LLVMPositionBuilderAtEnd(builder, bb);
        return alloca;
    }

    public LLVMValueRef genLoad(final LLVMTypeRef ty, final LLVMValueRef ptr) {
        assert validBuilder;
        return LLVMBuildLoad2(builder, ty, ptr, "");
    }

    public LLVMValueRef genStore(final LLVMValueRef v, final LLVMValueRef ptr) {
        assert validBuilder;
        return LLVMBuildStore(builder, v, ptr);
    }

    public void genIR(final Stmt stmt) {
        genStmt(this, stmt);
    }
}

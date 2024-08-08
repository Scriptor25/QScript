package io.scriptor.backend.value;

import java.util.ArrayList;
import java.util.List;

import io.scriptor.backend.Block;
import io.scriptor.type.FunctionType;

public class Function extends GlobalValue {

    private final FunctionType functionType;
    private final ArgValue[] args;
    private final List<Block> blocks = new ArrayList<>();

    public Function(final FunctionType type) {
        this(type, null);
    }

    public Function(final FunctionType type, final String name) {
        super(type, name);
        this.functionType = type;

        args = new ArgValue[type.getArgCount()];
        for (int i = 0; i < args.length; ++i)
            args[i] = new ArgValue(type.getArg(i));
    }

    @Override
    public void dump() {
        final var result = functionType.getResult();
        final var name = getName();

        if (isEmpty()) {
            System.out.print("declare ");
        } else {
            System.out.print("define ");
        }
        System.out.printf("%s @%s(", result, name);

        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                System.out.print(", ");
            args[i].dump();
        }

        if (functionType.isVarArg()) {
            if (args.length > 0)
                System.out.print(", ");
            System.out.print("?");
        }
        System.out.print(")");

        if (isEmpty())
            return;

        System.out.println(" {");
        for (int i = 0; i < blocks.size(); ++i) {
            if (i > 0)
                System.out.println();
            final var block = blocks.get(i);
            block.dump();
            System.out.println();
        }
        System.out.print("}");
    }

    public boolean isEmpty() {
        return blocks.isEmpty();
    }

    public int getArgCount() {
        return args.length;
    }

    public ArgValue getArg(final int index) {
        return args[index];
    }

    public void insert(final Block block) {
        if (!blocks.contains(block))
            blocks.add(block);
        block.setParent(this);
    }
}

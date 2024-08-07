package io.scriptor.backend.value;

import java.util.ArrayList;
import java.util.List;

import io.scriptor.backend.Block;
import io.scriptor.type.FunctionType;

public class Function extends GlobalValue {

    private final ArgValue[] args;
    private final List<Block> blocks = new ArrayList<>();

    public Function(final FunctionType type) {
        this(type, null);
    }

    public Function(final FunctionType type, final String name) {
        super(type, null, name);

        args = new ArgValue[type.getArgCount()];
        for (int i = 0; i < args.length; ++i)
            args[i] = new ArgValue(type.getArg(i));
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

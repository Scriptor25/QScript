package io.scriptor.backend.inst;

import io.scriptor.backend.Block;
import io.scriptor.backend.value.Value;
import io.scriptor.type.Type;

public abstract class Instruction extends Value {

    private Block parent;
    private Instruction previous, next;

    protected Instruction(final Type type) {
        super(type);
    }

    public Block getParent() {
        return parent;
    }

    public Instruction getNext() {
        return next;
    }

    public Instruction getPrevious() {
        return previous;
    }

    public void insertInto(final Block block) {
        this.parent = block;
        block.insert(this);
    }

    /**
     * insert inst directly after this
     * 
     * @param inst
     */
    public void append(final Instruction inst) {
        if (next != null)
            next.previous = inst;
        next = inst;
        next.previous = this;
    }

    /**
     * insert inst directly before this
     * 
     * @param inst
     */
    public void prepend(final Instruction inst) {
        if (previous != null)
            previous.next = inst;
        previous = inst;
        previous.next = this;
    }

    /**
     * insert inst at the end of the list
     * 
     * @param inst
     */
    public void insert(final Instruction inst) {
        if (next == null) {
            next = inst;
            inst.previous = this;
            return;
        }
        next.append(inst);
    }
}

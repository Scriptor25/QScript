package io.scriptor.backend;

import io.scriptor.backend.inst.Instruction;
import io.scriptor.backend.value.Function;

public class Block {

    private Function parent;
    private String name;
    private Instruction begin, end;

    public Block() {
    }

    public Block(final Function parent) {
        insertInto(parent);
    }

    public Block(final String name) {
        this.name = name;
    }

    public Block(final Function parent, final String name) {
        insertInto(parent);
        this.name = name;
    }

    public Function getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public Instruction getBegin() {
        return begin;
    }

    public Instruction getEnd() {
        return end;
    }

    public void setParent(final Function parent) {
        this.parent = parent;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setBegin(final Instruction begin) {
        this.begin = begin;
    }

    public void setEnd(final Instruction end) {
        this.end = end;
    }

    public void insertInto(final Function parent) {
        parent.insert(this);
    }

    public void insert(final Instruction inst) {
        if (end != null) {
            end.insert(inst);
        }
        end = inst;
    }
}

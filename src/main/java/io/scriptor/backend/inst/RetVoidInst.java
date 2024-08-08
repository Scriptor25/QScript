package io.scriptor.backend.inst;

public class RetVoidInst extends Instruction {

    public RetVoidInst() {
        super(null);
    }

    @Override
    public void dump() {
        super.dump();
        System.out.print("ret void");
    }
}

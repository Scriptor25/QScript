package io.scriptor.backend;

public class IRBuilder {

    private final IRContext context;

    public IRBuilder(final IRContext context) {
        this.context = context;
    }

    public IRContext getContext() {
        return context;
    }
}

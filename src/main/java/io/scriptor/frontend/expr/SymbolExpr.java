package io.scriptor.frontend.expr;

import io.scriptor.frontend.SourceLocation;
import io.scriptor.type.Type;

public class SymbolExpr extends Expr {

    public static SymbolExpr create(final SourceLocation sl, final Type ty, final String name) {
        return new SymbolExpr(sl, ty, name);
    }

    private final String name;

    private SymbolExpr(final SourceLocation sl, final Type ty, final String name) {
        super(sl, ty);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

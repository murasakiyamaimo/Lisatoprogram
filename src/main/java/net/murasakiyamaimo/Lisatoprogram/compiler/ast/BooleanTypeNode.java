package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public class BooleanTypeNode implements TypeNode {
    @Override
    public String getJavaTypeName() {
        return "boolean";
    }

    @Override
    public boolean isCompatibleWith(TypeNode other) {
        return other instanceof BooleanTypeNode;
    }
}

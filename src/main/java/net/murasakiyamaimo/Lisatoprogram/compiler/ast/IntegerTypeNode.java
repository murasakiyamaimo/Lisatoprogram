package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public class IntegerTypeNode implements TypeNode {
    @Override
    public String getJavaTypeName() {
        return "int";
    }

    @Override
    public boolean isCompatibleWith(TypeNode other) {
        return other instanceof IntegerTypeNode;
    }
}

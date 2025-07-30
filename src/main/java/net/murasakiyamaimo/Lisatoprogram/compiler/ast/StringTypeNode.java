package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public class StringTypeNode implements TypeNode {
    @Override
    public String getJavaTypeName() {
        return "String";
    }

    @Override
    public boolean isCompatibleWith(TypeNode other) {
        return other instanceof StringTypeNode;
    }
}

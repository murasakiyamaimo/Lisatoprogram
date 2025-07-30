package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public interface TypeNode extends AstNode {
    String getJavaTypeName();
    boolean isCompatibleWith(TypeNode other);
}

package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public class IdentifierNode implements ExpressionNode {
    private String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

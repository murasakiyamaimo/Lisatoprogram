package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public class StringLiteralNode implements ExpressionNode {
    private String value;

    public StringLiteralNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

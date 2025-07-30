package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public class BooleanLiteralNode implements ExpressionNode {
    private boolean value;

    public BooleanLiteralNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}

package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public class IntegerLiteralNode implements ExpressionNode {
    private int value;

    public IntegerLiteralNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

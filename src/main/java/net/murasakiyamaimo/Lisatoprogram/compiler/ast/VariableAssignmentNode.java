package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public class VariableAssignmentNode implements StatementNode {
    private String variableName;
    private ExpressionNode Value; // 値を表すノード

    public VariableAssignmentNode(String name, ExpressionNode initialValue) {
        this.variableName = name;
        this.Value = initialValue;
    }

    // ゲッターメソッド
    public String getVariableName() { return variableName; }
    public ExpressionNode getValue() { return Value; }
}

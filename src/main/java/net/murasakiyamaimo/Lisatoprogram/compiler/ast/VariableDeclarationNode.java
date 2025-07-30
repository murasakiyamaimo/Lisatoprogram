package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public class VariableDeclarationNode implements StatementNode {
    private String variableName;
    private ExpressionNode Value; // 初期値を表すノード
    private TypeNode variableType;       // 型を表すノード

    public VariableDeclarationNode(String name, ExpressionNode initialValue, TypeNode type) {
        this.variableName = name;
        this.Value = initialValue;
        this.variableType = type;
    }

    // ゲッターメソッド
    public String getVariableName() { return variableName; }
    public ExpressionNode getValue() { return Value; }
    public TypeNode getVariableType() { return variableType; }
}

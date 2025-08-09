package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

import java.util.List;

public class FunctionLiteralNode {
    private ParameterNode parameterNode;
    private String name;
    private List<StatementNode> statementNode;
    private TypeNode returnType;
    private ExpressionNode returnLiteral;

    public FunctionLiteralNode(ParameterNode parameterNode, String name, List<StatementNode> statementNode, TypeNode returnType, ExpressionNode returnLiteral) {
        this.parameterNode = parameterNode;
        this.name = name;
        this.statementNode = statementNode;
        this.returnType = returnType;
        this.returnLiteral = returnLiteral;
    }

    public String getName() {
        return name;
    }

    public List<StatementNode> getStatementNode() {
        return statementNode;
    }

    public ParameterNode getParameterNode() {
        return parameterNode;
    }

    public TypeNode getReturnType() {
        return returnType;
    }

    public ExpressionNode getReturnLiteral() {
        return returnLiteral;
    }
}

package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

import java.util.List;

public class FunctionDeclarationNode implements StatementNode {
    FunctionLiteralNode functionNode;

    public FunctionDeclarationNode(ParameterNode parameterNode, String name, List<StatementNode> statementNode, TypeNode returnType, ExpressionNode returnLiteral) {
        functionNode = new FunctionLiteralNode(parameterNode, name, statementNode, returnType, returnLiteral);
    }

    public FunctionLiteralNode getLiteral() {
        return functionNode;
    }
}

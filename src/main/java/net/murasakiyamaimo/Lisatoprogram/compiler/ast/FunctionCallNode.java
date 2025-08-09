package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

import java.util.List;

public class FunctionCallNode implements StatementNode, ExpressionNode {
    private List<ExpressionNode> expressionNode;
    private String name;

    public FunctionCallNode(String name, List<ExpressionNode> expressionNode) {
        this.expressionNode = expressionNode;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return expressionNode.size();
    }

    public ExpressionNode getArg(int i) {
        return expressionNode.get(i);
    }
}

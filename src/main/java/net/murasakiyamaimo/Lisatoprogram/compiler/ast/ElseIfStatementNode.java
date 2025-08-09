package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

import java.util.List;

public class ElseIfStatementNode implements AstNode {
    private final ExpressionNode condition;
    private final List<StatementNode> body;

    public ElseIfStatementNode(ExpressionNode condition, List<StatementNode> body) {
        this.condition = condition;
        this.body = body;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public List<StatementNode> getBody() {
        return body;
    }
}

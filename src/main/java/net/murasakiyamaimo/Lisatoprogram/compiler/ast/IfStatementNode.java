package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class IfStatementNode implements StatementNode {
    private final ExpressionNode condition;
    private final List<StatementNode> thenBody;
    private final List<ElseIfStatementNode> elseIfStatements;
    private final List<StatementNode> elseBody;

    public IfStatementNode(ExpressionNode condition, List<StatementNode> thenBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseIfStatements = new ArrayList<>();
        this.elseBody = new ArrayList<>();
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public List<StatementNode> getThenBody() {
        return thenBody;
    }

    public void addElseIfStatementNode(ExpressionNode condition, List<StatementNode> body) {
        this.elseIfStatements.add(new ElseIfStatementNode(condition, body));
    }

    public List<ElseIfStatementNode> getElseIfStatements() {
        return elseIfStatements;
    }

    public void setElseBody(List<StatementNode> elseBody) {
        this.elseBody.addAll(elseBody);
    }

    public List<StatementNode> getElseBody() {
        return elseBody;
    }
}

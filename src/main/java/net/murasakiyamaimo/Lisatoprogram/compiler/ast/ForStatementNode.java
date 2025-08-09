package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class ForStatementNode implements StatementNode {
    private final String counterVariableName;
    private final ExpressionNode loopCount;
    private final List<StatementNode> body;

    public ForStatementNode(String counterVariableName, ExpressionNode loopCount) {
        this.counterVariableName = counterVariableName;
        this.loopCount = loopCount;
        this.body = new ArrayList<>();
    }

    public String getCounterVariableName() {
        return counterVariableName;
    }

    public ExpressionNode getLoopCount() {
        return loopCount;
    }

    public void addStatement(StatementNode statement) {
        this.body.add(statement);
    }

    public List<StatementNode> getBody() {
        return body;
    }
}

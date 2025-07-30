package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

public class PrintStatementNode implements StatementNode {
    private ExpressionNode expression;

    public PrintStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public ExpressionNode getExpression() {
        return expression;
    }
}

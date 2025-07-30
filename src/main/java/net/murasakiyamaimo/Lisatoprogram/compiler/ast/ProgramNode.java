package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode implements AstNode {
    private List<StatementNode> statements = new ArrayList<>();

    public void addStatement(StatementNode statement) {
        this.statements.add(statement);
    }

    public List<StatementNode> getStatements() {
        return statements;
    }
}
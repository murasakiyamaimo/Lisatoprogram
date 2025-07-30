package net.murasakiyamaimo.Lisatoprogram.compiler.codegen;

import net.murasakiyamaimo.Lisatoprogram.compiler.ast.*;
import net.murasakiyamaimo.Lisatoprogram.compiler.semantics.*;

public class JavaCodeGenerator {
    private StringBuilder codeBuilder = new StringBuilder();
    private SymbolTable symbolTable;

    public JavaCodeGenerator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable; // 意味解析器から受け取ったシンボルテーブルを使う
    }

    public String generate(ProgramNode programNode) {
        for (StatementNode statement : programNode.getStatements()) {
            generate(statement);
        }
        return codeBuilder.toString();
    }

    public void generate(StatementNode statement) {
        if (statement instanceof VariableDeclarationNode) {
            // 変数宣言ノード
            generate((VariableDeclarationNode) statement);
        } else if (statement instanceof PrintStatementNode) {
            // print文ノード
            generate((PrintStatementNode) statement);
        } else if (statement instanceof PilikeStatementNode) {
            // for文ノード
            generate((PilikeStatementNode) statement);
        }
    }

    // for文
    public void generate(PilikeStatementNode node) {
        String counterVariableName = node.getCounterVariableName();
        ExpressionNode loopCountExpr = node.getLoopCount();
        String loopCountString;

        if (loopCountExpr instanceof IntegerLiteralNode) {
            loopCountString = String.valueOf(((IntegerLiteralNode) loopCountExpr).getValue());
        } else if (loopCountExpr instanceof IdentifierNode) {
            loopCountString = ((IdentifierNode) loopCountExpr).getName();
        } else {
            throw new RuntimeException("Unsupported loop count expression type for code generation: " + loopCountExpr.getClass().getSimpleName());
        }

        codeBuilder.append("for (int ")
                .append(counterVariableName)
                .append(" = 0;")
                .append(counterVariableName)
                .append(" < ")
                .append(loopCountString)
                .append(";")
                .append(counterVariableName)
                .append("++) {\n");
        for (StatementNode statement : node.getBody()) {
            codeBuilder.append("    ");
            generate(statement);
        }
        codeBuilder.append("}");
    }

    // 変数定義
    public void generate(VariableDeclarationNode node) {
        String javaTypeName = node.getVariableType().getJavaTypeName();
        String variableName = node.getVariableName();
        ExpressionNode initialValue = node.getValue();
        String initialValueString;

        if (initialValue instanceof StringLiteralNode) {
            initialValueString = "\"" + ((StringLiteralNode) initialValue).getValue() + "\"";
        } else if (initialValue instanceof IntegerLiteralNode) {
            initialValueString = String.valueOf(((IntegerLiteralNode) initialValue).getValue());
        } else if (initialValue instanceof BooleanLiteralNode) {
            initialValueString = String.valueOf(((BooleanLiteralNode) initialValue).getValue());
        } else if (initialValue instanceof IdentifierNode) {
            // 変数を初期値として使う場合
            initialValueString = ((IdentifierNode) initialValue).getName();
        }
        else {
            throw new RuntimeException("Unsupported initial value type for code generation: " + initialValue.getClass().getSimpleName());
        }

        codeBuilder.append(javaTypeName)
                .append(" ")
                .append(variableName)
                .append(" = ")
                .append(initialValueString)
                .append(";\n");
    }

    // 標準出力
    public void generate(PrintStatementNode node) {
        ExpressionNode expr = node.getExpression();
        String exprString;

        if (expr instanceof StringLiteralNode) {
            exprString = "\"" + ((StringLiteralNode) expr).getValue() + "\"";
        } else if (expr instanceof IntegerLiteralNode) {
            exprString = String.valueOf(((IntegerLiteralNode) expr).getValue());
        } else if (expr instanceof BooleanLiteralNode) {
            exprString = String.valueOf(((BooleanLiteralNode) expr).getValue());
        } else if (expr instanceof IdentifierNode) {
            // 変数を出力する場合
            exprString = ((IdentifierNode) expr).getName();
        }
        else {
            throw new RuntimeException("Unsupported expression type for print statement: " + expr.getClass().getSimpleName());
        }

        codeBuilder.append("System.out.println(")
                .append(exprString)
                .append(");\n");
    }


    // 最終的な完全なJavaプログラムの生成メソッドは先述の例と同じ。
    public String getFullJavaProgram(String mainContent) {
        return "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        " + mainContent.replace("\n", "\n        ") + "\n" +
                "    }\n" +
                "}\n";
    }
}

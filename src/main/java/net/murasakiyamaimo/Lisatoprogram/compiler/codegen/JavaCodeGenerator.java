package net.murasakiyamaimo.Lisatoprogram.compiler.codegen;

import net.murasakiyamaimo.Lisatoprogram.compiler.ast.*;

import java.util.Map;

public class JavaCodeGenerator {
    private StringBuilder codeScope = new StringBuilder();

    private StringBuilder currentScope() {
        return codeScope;
    }

    public String generate(ProgramNode programNode) {
        StringBuilder mainCodeBuilder = new StringBuilder();
        StringBuilder classCodeBuilder = new StringBuilder();
        for (StatementNode statement : programNode.getStatements()) {
            if (statement instanceof FunctionDeclarationNode) {
                codeScope = classCodeBuilder;
            } else {
                codeScope = mainCodeBuilder;
            }
            generate(statement);
        }
        return getFullJavaProgram(mainCodeBuilder.toString(), classCodeBuilder.toString());
    }

    public String getExpressionString(ExpressionNode expr) {
        if (expr instanceof StringLiteralNode) {
            return "\"" + ((StringLiteralNode) expr).getValue() + "\"";
        } else if (expr instanceof IntegerLiteralNode) {
            return String.valueOf(((IntegerLiteralNode) expr).getValue());
        } else if (expr instanceof BooleanLiteralNode) {
            return String.valueOf(((BooleanLiteralNode) expr).getValue());
        } else if (expr instanceof IdentifierNode) {
            return ((IdentifierNode) expr).getName();
        } else if (expr instanceof ComparisonExpressionNode) {
            return generate((ComparisonExpressionNode) expr);
        } else if (expr instanceof FunctionCallNode) {
            return generateExpression((FunctionCallNode) expr);
        }
        throw new RuntimeException("Unsupported expression type for code generation: " + expr.getClass().getSimpleName());
    }

    public void generate(StatementNode statement) {
        if (statement instanceof VariableDeclarationNode) {
            // 変数宣言ノード
            generate((VariableDeclarationNode) statement);
        } else if (statement instanceof VariableAssignmentNode) {
            // 変数代入ノード
            generate((VariableAssignmentNode) statement);
        } else if (statement instanceof PrintStatementNode) {
            // print文ノード
            generate((PrintStatementNode) statement);
        } else if (statement instanceof ForStatementNode) {
            // for文ノード
            generate((ForStatementNode) statement);
        } else if (statement instanceof IfStatementNode) {
            // if文ノード
            generate((IfStatementNode) statement);
        } else if (statement instanceof FunctionDeclarationNode) {
            // 関数定義
            generate((FunctionDeclarationNode) statement);
        } else if (statement instanceof FunctionCallNode) {
            // 関数呼び出し
            generateStatement((FunctionCallNode) statement);
        }
    }

    // 関数定義
    public void generate(FunctionDeclarationNode node) {
        String returnType;
        if (node.getLiteral().getReturnType() != null) {
            returnType = node.getLiteral().getReturnType().getJavaTypeName();
        } else {
            returnType = "void";
        }

        String functionName = node.getLiteral().getName();

        currentScope().append("public static ")
                .append(returnType)
                .append(" ")
                .append(functionName)
                .append("(");

        for (int i = 0; i < node.getLiteral().getParameterNode().get().size(); i++) {
            Map.Entry<String, TypeNode> entry = node.getLiteral().getParameterNode().get().get(i);
            codeScope.append(entry.getValue().getJavaTypeName())
                    .append(" ")
                    .append(entry.getKey());
            if (i + 1 != node.getLiteral().getParameterNode().get().size()) {
                codeScope.append(", ");
            }
        }

        currentScope().append(")")
                .append(" {\n");

        for (StatementNode statementNode : node.getLiteral().getStatementNode()) {
            generate(statementNode);
        }

        if (node.getLiteral().getReturnType() != null) {
            currentScope().append("return ")
                    .append(getExpressionString(node.getLiteral().getReturnLiteral()))
                    .append(";\n");
        }

        currentScope().append("}\n");
    }

    // 関数呼び出し(statement)
    public void generateStatement(FunctionCallNode node) {
        currentScope().append(node.getName())
                .append("(");
        for (int i = 0; i < node.getSize(); i++) {
            currentScope().append(getExpressionString(node.getArg(i)));
            if (i + 1 != node.getSize()) {
                currentScope().append(", ");
            }
        }
        currentScope().append(");");
    }

    // 関数呼び出し(式)
    public String generateExpression(FunctionCallNode node) {
        StringBuilder func = new StringBuilder();
        func.append(node.getName())
                .append("(");
        for (int i = 0; i < node.getSize(); i++) {
            func.append(getExpressionString(node.getArg(i)));
            if (i + 1 != node.getSize()) {
                func.append(", ");
            }
        }
        func.append(")");
        return func.toString();
    }

    // 式
    public String generate(ComparisonExpressionNode node) {
        String left = getExpressionString(node.getLeft());
        String right = getExpressionString(node.getRight());
        String operator = node.getOperator();
        return "(" + left + " " + operator + " " + right + ")";
    }

    // if文
    public void generate(IfStatementNode node) {
        String conditionString = getExpressionString(node.getCondition());

        currentScope().append("if (")
                .append(conditionString)
                .append(") {\n");
        for (StatementNode statement : node.getThenBody()) {
            currentScope().append("    ");
            generate(statement);
        }
        currentScope().append("}");

        // else if
        for (ElseIfStatementNode elseIfBlock : node.getElseIfStatements()) {
            String elseIfConditionString = getExpressionString(elseIfBlock.getCondition());
            currentScope().append(" else if (").append(elseIfConditionString).append(") {\n");
            for (StatementNode statement : elseIfBlock.getBody()) {
                currentScope().append("    ");
                generate(statement);
            }
            currentScope().append("}");
        }

        // else
        if (!node.getElseBody().isEmpty()) {
            currentScope().append(" else {\n");
            for (StatementNode statement : node.getElseBody()) {
                currentScope().append("    ");
                generate(statement);
            }
            currentScope().append("}");
        }

        currentScope().append("\n");
    }

    // 変数代入
    public void generate(VariableAssignmentNode node) {
        String variableName = node.getVariableName();
        ExpressionNode value = node.getValue();
        String valueString = getExpressionString(value);

        currentScope().append(variableName)
                .append(" = ")
                .append(valueString)
                .append(";\n");
    }

    // for文
    public void generate(ForStatementNode node) {
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

        currentScope().append("for (int ")
                .append(counterVariableName)
                .append(" = 0;")
                .append(counterVariableName)
                .append(" < ")
                .append(loopCountString)
                .append(";")
                .append(counterVariableName)
                .append("++) {\n");
        for (StatementNode statement : node.getBody()) {
            currentScope().append("    ");
            generate(statement);
        }
        currentScope().append("}");
    }

    // 変数定義
    public void generate(VariableDeclarationNode node) {
        String javaTypeName = node.getVariableType().getJavaTypeName();
        String variableName = node.getVariableName();
        ExpressionNode initialValue = node.getValue();
        String initialValueString = getExpressionString(initialValue);

        currentScope().append(javaTypeName)
                .append(" ")
                .append(variableName)
                .append(" = ")
                .append(initialValueString)
                .append(";\n");
    }

    // 標準出力
    public void generate(PrintStatementNode node) {
        ExpressionNode expr = node.getExpression();
        String exprString = getExpressionString(expr);

        currentScope().append("System.out.println(")
                .append(exprString)
                .append(");\n");
    }


    // 最終的な完全なJavaプログラムの生成メソッドは先述の例と同じ。
    public String getFullJavaProgram(String mainContent, String classContent) {
        return "public class Main {\n" +
                "    " + classContent.replace("\n", "\n        ") +
                "    public static void main(String[] args) {\n" +
                "        " + mainContent.replace("\n", "\n        ") + "\n" +
                "    }\n" +
                "}\n";
    }
}

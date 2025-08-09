package net.murasakiyamaimo.Lisatoprogram.astbuilder;

import net.murasakiyamaimo.Lisatoprogram.compiler.ast.*;
import net.murasakiyamaimo.Lisatoprogram.parser.LisatoprogramBaseVisitor;
import net.murasakiyamaimo.Lisatoprogram.parser.LisatoprogramParser;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// ANTLRのParse Treeを走査し、カスタムASTを構築するVisitor
public class AstBuilder extends LisatoprogramBaseVisitor<AstNode> {

    // プログラム全体のルートノードを構築
    @Override
    public AstNode visitProgram(LisatoprogramParser.ProgramContext ctx) {
        ProgramNode programNode = new ProgramNode();
        for (ParseTree child : ctx.children) {
            if (child instanceof LisatoprogramParser.StatementContext) {
                programNode.addStatement((StatementNode) visit(child));
            } else if (child instanceof LisatoprogramParser.FunctionDeclarationContext) {
                programNode.addStatement((StatementNode) visitFunctionDeclaration((LisatoprogramParser.FunctionDeclarationContext) child));
            }
        }
        return programNode;
    }

    // 各ステートメントノードを訪問し、適切なASTノードを返す
    @Override
    public AstNode visitStatement(LisatoprogramParser.StatementContext ctx) {
        if (ctx.variableDeclaration() != null) {
            return visit(ctx.variableDeclaration());
        } else if (ctx.variableAssignment() != null) {
            return visit(ctx.variableAssignment());
        } else if (ctx.printStatement() != null) {
            return visit(ctx.printStatement());
        } else if (ctx.forStatement() != null) {
            return visit(ctx.forStatement());
        } else if (ctx.ifStatement() != null) {
            return visit(ctx.ifStatement());
        } else if (ctx.functionCall() != null) {
            return visit(ctx.functionCall());
        }
        return null; // 未知のステートメントタイプ
    }

    // 関数定義
    @Override
    public AstNode visitFunctionDeclaration(LisatoprogramParser.FunctionDeclarationContext ctx) {
        ParameterNode parameterNode = new ParameterNode();
        String name = ctx.IDENTIFIER().getText();
        TypeNode returnType = null;
        ExpressionNode returnLiteral = null;
        if (ctx.type() != null) {
            returnType = (TypeNode) visit(ctx.type());
            returnLiteral = (ExpressionNode) visit(ctx.expression());
        }
        List<StatementNode> statementNode = new ArrayList<>();
        for (int i = 0;i < ctx.parameter().size();i++) {
            parameterNode.add(ctx.parameter(i).IDENTIFIER().getText(), (TypeNode) visit(ctx.parameter(i).type()));
        }
        for (LisatoprogramParser.StatementContext statementContext : ctx.statement()) {
            statementNode.add((StatementNode) visit(statementContext));
        }

        return new FunctionDeclarationNode(parameterNode, name, statementNode, returnType, returnLiteral);
    }

    // 関数呼び出し
    @Override
    public AstNode visitFunctionCall(LisatoprogramParser.FunctionCallContext ctx) {
        String name = ctx.IDENTIFIER().getText();

        List<ExpressionNode> arguments = new ArrayList<>();
        for (int i = 0; i < ctx.expression().size(); i++) {
            arguments.add((ExpressionNode) visit(ctx.expression(i)));
        }
        return new FunctionCallNode(name, arguments);
    }

    // 条件分岐(if)ノードを構築
    @Override
    public AstNode visitIfStatement(LisatoprogramParser.IfStatementContext ctx) {
        ExpressionNode condition = (ExpressionNode) visit(ctx.ifCondition);
        List<StatementNode> thenBody = ctx.ifStatements.stream()
                .map(s -> (StatementNode) visit(s))
                .collect(Collectors.toList());

        IfStatementNode node = new IfStatementNode(condition, thenBody);

        for (LisatoprogramParser.ElseIfStatementContext elseIfCtx : ctx.elseIfBlock) {
            ExpressionNode elseIfCondition = (ExpressionNode) visit(elseIfCtx.elseIfCondition);
            List<StatementNode> elseIfBody = elseIfCtx.elseIfStatements.stream()
                    .map(s -> (StatementNode) visit(s))
                    .collect(Collectors.toList());
            node.addElseIfStatementNode(elseIfCondition, elseIfBody);
        }

        if (ctx.elseStatements != null) {
            List<StatementNode> elseBody = ctx.elseStatements.stream()
                    .map(s -> (StatementNode) visit(s))
                    .collect(Collectors.toList());
            node.setElseBody(elseBody);
        }

        return node;
    }

    // ループ(for)ノードを構築
    @Override
    public AstNode visitForStatement(LisatoprogramParser.ForStatementContext ctx) {
        String counterVariableName = ctx.IDENTIFIER().getText();
        ExpressionNode loopCount = (ExpressionNode) visit(ctx.expression());
        ForStatementNode pilikeNode = new ForStatementNode(counterVariableName, loopCount);
        for (LisatoprogramParser.StatementContext statementCtx : ctx.statement()) {
            pilikeNode.addStatement((StatementNode) visit(statementCtx));
        }
        return pilikeNode;
    }

    // 変数代入ノードを構築
    @Override
    public AstNode visitVariableAssignment(LisatoprogramParser.VariableAssignmentContext ctx) {
        String variableName = ctx.IDENTIFIER().getText();
        ExpressionNode value = (ExpressionNode) visit(ctx.expression());
        return new VariableAssignmentNode(variableName, value);
    }

    // 変数宣言ノードを構築
    @Override
    public AstNode visitVariableDeclaration(LisatoprogramParser.VariableDeclarationContext ctx) {
        String variableName = ctx.IDENTIFIER().getText();
        ExpressionNode initialValue = (ExpressionNode) visit(ctx.expression());
        TypeNode type = (TypeNode) visit(ctx.type());
        return new VariableDeclarationNode(variableName, initialValue, type);
    }

    // print文ノードを構築
    @Override
    public AstNode visitPrintStatement(LisatoprogramParser.PrintStatementContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        return new PrintStatementNode(expression);
    }

    @Override
    public AstNode visitComparisonExpression(LisatoprogramParser.ComparisonExpressionContext ctx) {
        ExpressionNode left = (ExpressionNode) visit(ctx.expression(0));
        ExpressionNode right = (ExpressionNode) visit(ctx.expression(1));
        String operator = ctx.comparisonOperator().getText();
        return new ComparisonExpressionNode(left, right, operator);
    }

    // 文字列リテラルノードを構築
    @Override
    public AstNode visitStringLiteral(LisatoprogramParser.StringLiteralContext ctx) {
        String text = ctx.STRING_LITERAL().getText();
        // 引用符を削除して値を格納
        return new StringLiteralNode(text.substring(1, text.length() - 1));
    }

    // 整数リテラルノードを構築
    @Override
    public AstNode visitIntegerLiteral(LisatoprogramParser.IntegerLiteralContext ctx) {
        return new IntegerLiteralNode(Integer.parseInt(ctx.INTEGER_LITERAL().getText()));
    }

    // 真偽値リテラルノードを構築
    @Override
    public AstNode visitBooleanLiteral(LisatoprogramParser.BooleanLiteralContext ctx) {
        return new BooleanLiteralNode(ctx.BOOLEAN_LITERAL().getText().equals("tuni"));
    }

    // 識別子ノードを構築
    @Override
    public AstNode visitIdentifier(LisatoprogramParser.IdentifierContext ctx) {
        return new IdentifierNode(ctx.IDENTIFIER().getText());
    }

    // String型ノードを構築
    @Override
    public AstNode visitStringType(LisatoprogramParser.StringTypeContext ctx) {
        return new StringTypeNode();
    }

    // int型ノードを構築
    @Override
    public AstNode visitIntegerType(LisatoprogramParser.IntegerTypeContext ctx) {
        return new IntegerTypeNode();
    }

    // boolean型ノードを構築
    @Override
    public AstNode visitBooleanType(LisatoprogramParser.BooleanTypeContext ctx) {
        return new BooleanTypeNode();
    }
}
package net.murasakiyamaimo.Lisatoprogram.astbuilder;

import net.murasakiyamaimo.Lisatoprogram.compiler.ast.*;
import net.murasakiyamaimo.Lisatoprogram.parser.LisatoprogramBaseVisitor;
import net.murasakiyamaimo.Lisatoprogram.parser.LisatoprogramParser;

// ANTLRのParse Treeを走査し、カスタムASTを構築するVisitor
public class AstBuilder extends LisatoprogramBaseVisitor<AstNode> {

    // プログラム全体のルートノードを構築
    @Override
    public AstNode visitProgram(LisatoprogramParser.ProgramContext ctx) {
        ProgramNode programNode = new ProgramNode();
        for (LisatoprogramParser.StatementContext statementCtx : ctx.statement()) {
            programNode.addStatement((StatementNode) visit(statementCtx));
        }
        return programNode;
    }

    // 各ステートメントノードを訪問し、適切なASTノードを返す
    @Override
    public AstNode visitStatement(LisatoprogramParser.StatementContext ctx) {
        if (ctx.variableDeclaration() != null) {
            return visit(ctx.variableDeclaration());
        } else if (ctx.printStatement() != null) {
            return visit(ctx.printStatement());
        } else if (ctx.pilikeStatement() != null) {
            return visit(ctx.pilikeStatement());
        }
        return null; // 未知のステートメントタイプ
    }

    // ループ(for)ノードを構築
    @Override
    public AstNode visitPilikeStatement(LisatoprogramParser.PilikeStatementContext ctx) {
        System.out.println("make Pilike Statement node");
        String counterVariableName = ctx.IDENTIFIER().getText();
        ExpressionNode loopCount = (ExpressionNode) visit(ctx.expression());
        PilikeStatementNode pilikeNode = new PilikeStatementNode(counterVariableName, loopCount);
        for (LisatoprogramParser.StatementContext statementCtx : ctx.statement()) {
            pilikeNode.addStatement((StatementNode) visit(statementCtx));
        }
        return pilikeNode;
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
        return new BooleanLiteralNode(Boolean.parseBoolean(ctx.BOOLEAN_LITERAL().getText()));
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
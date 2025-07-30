package net.murasakiyamaimo.Lisatoprogram.compiler.semantics;

import net.murasakiyamaimo.Lisatoprogram.compiler.ast.*;

public class SemanticAnalyzer {
    private SymbolTable symbolTable = new SymbolTable();

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    // プログラム全体のASTを解析
    public void analyze(ProgramNode programNode) throws SemanticException {
        for (StatementNode statement : programNode.getStatements()) {
            analyze(statement);
        }
    }

    // AST(何かわからない)ときの解析
    public void analyze(StatementNode statement) throws SemanticException {
        if (statement instanceof VariableDeclarationNode) {
            System.out.println("var");
            analyze((VariableDeclarationNode) statement);
        } else if (statement instanceof PrintStatementNode) {
            System.out.println("print");
            analyze((PrintStatementNode) statement);
        } else if (statement instanceof PilikeStatementNode) {
            System.out.println("for");
            analyze((PilikeStatementNode) statement);
        }
    }

    private void analyze(PilikeStatementNode node) throws  SemanticException {
        System.out.println("analyze Pilike");
        String counterVariableName = node.getCounterVariableName();
        ExpressionNode loopCountExpr = node.getLoopCount();

        TypeNode inferredLoopCountType = inferType(loopCountExpr);
        if (!(inferredLoopCountType instanceof IntegerTypeNode)) {
            throw new SemanticException("型エラー: ループは整数回である必要があります");
        }

        if (symbolTable.containsVariable(counterVariableName)) {
            throw new SemanticException("変数 '" + counterVariableName + "' は既に定義されています。");
        }
        symbolTable.defineVariable(counterVariableName, new IntegerTypeNode());

        for (StatementNode statement : node.getBody()) {
            analyze(statement);
        }
        symbolTable.removeVariable(counterVariableName);
    }

    // 変数宣言ノードの意味解析
    private void analyze(VariableDeclarationNode node) throws SemanticException {
        String variableName = node.getVariableName();
        TypeNode declaredType = node.getVariableType();
        ExpressionNode initialValue = node.getValue();

        // 変数が既に定義されていないかチェック
        if (symbolTable.containsVariable(variableName)) {
            throw new SemanticException("変数 '" + variableName + "' は既に定義されています。");
        }

        // 初期値の型を推論
        TypeNode inferredInitialType = inferType(initialValue);

        // 型チェック: 宣言された型と初期値の型が互換性があるか
        if (inferredInitialType != null && !declaredType.isCompatibleWith(inferredInitialType)) {
            throw new SemanticException("型不一致エラー: 初期値の型 '" + inferredInitialType.getJavaTypeName() +
                    "' は宣言された型 '" + declaredType.getJavaTypeName() + "' と互換性がありません。");
        }

        // 変数をシンボルテーブルに登録
        symbolTable.defineVariable(variableName, declaredType);
    }

    // print文ノードの意味解析
    private void analyze(PrintStatementNode node) throws SemanticException {
        // print文の式が有効な式であるか、変数を参照している場合はそれが定義済みかチェック
        ExpressionNode expr = node.getExpression();
        if (expr instanceof IdentifierNode) {
            String varName = ((IdentifierNode) expr).getName();
            if (!symbolTable.containsVariable(varName)) {
                throw new SemanticException("未定義の変数 '" + varName + "' が使用されています。");
            }
            // 型チェックはここでは不要だが、より厳密な言語では必要になる場合がある
        }
        // リテラルの場合は常に有効
    }

    // 式ノードから型を推論するヘルパーメソッド
    private TypeNode inferType(ExpressionNode expr) throws SemanticException {
        if (expr instanceof StringLiteralNode) {
            return new StringTypeNode();
        } else if (expr instanceof IntegerLiteralNode) {
            return new IntegerTypeNode();
        } else if (expr instanceof BooleanLiteralNode) {
            return new BooleanTypeNode();
        } else if (expr instanceof IdentifierNode) {
            // 識別子の場合はシンボルテーブルから型を取得
            String varName = ((IdentifierNode) expr).getName();
            TypeNode type = symbolTable.getVariableType(varName);
            if (type == null) {
                throw new SemanticException("未定義の変数 '" + varName + "' が使用されています。");
            }
            return type;
        }
        // 他の式タイプもここに追加
        return null; // 未知の式タイプ
    }
}
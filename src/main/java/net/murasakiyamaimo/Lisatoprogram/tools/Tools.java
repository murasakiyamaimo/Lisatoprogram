package net.murasakiyamaimo.Lisatoprogram.tools;

import net.murasakiyamaimo.Lisatoprogram.compiler.ast.*;
import net.murasakiyamaimo.Lisatoprogram.compiler.semantics.SemanticException;
import net.murasakiyamaimo.Lisatoprogram.compiler.semantics.SymbolTable;

public class Tools {
    // 型推論
    public static TypeNode inferType(ExpressionNode expr, SymbolTable symbolTable) throws SemanticException {
        if (expr instanceof ComparisonExpressionNode) {
            return new BooleanTypeNode();
        } else if (expr instanceof StringLiteralNode) {
            return new StringTypeNode();
        } else if (expr instanceof IntegerLiteralNode) {
            return new IntegerTypeNode();
        } else if (expr instanceof BooleanLiteralNode) {
            return new BooleanTypeNode();
        } else if (expr instanceof FunctionLiteralNode) {
            return ((FunctionLiteralNode) expr).getReturnType();
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

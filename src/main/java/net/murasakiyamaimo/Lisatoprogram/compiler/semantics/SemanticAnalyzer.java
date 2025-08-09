package net.murasakiyamaimo.Lisatoprogram.compiler.semantics;

import net.murasakiyamaimo.Lisatoprogram.compiler.ast.*;
import net.murasakiyamaimo.Lisatoprogram.tools.Parameters;

import java.util.Map;
import java.util.Stack;

import static net.murasakiyamaimo.Lisatoprogram.tools.Tools.inferType;

public class SemanticAnalyzer {
    // SymbolTableのスタック管理
    private final Stack<SymbolTable> scopeStack = new Stack<>();

    public SemanticAnalyzer() {
        // グローバルスコープの作成とプッシュ
        scopeStack.push(new SymbolTable());
    }

    private SymbolTable currentScope() {
        return scopeStack.peek();
    }

    // プログラム全体のASTを解析
    public void analyze(ProgramNode programNode) throws SemanticException {
        for (StatementNode statement : programNode.getStatements()) {
            analyze(statement);
        }
    }

    // AST解析
    public void analyze(StatementNode statement) throws SemanticException {
        if (statement instanceof ComparisonExpressionNode) {
            analyze((ComparisonExpressionNode) statement);
        } else if (statement instanceof VariableDeclarationNode) {
            analyze((VariableDeclarationNode) statement);
        } else if (statement instanceof VariableAssignmentNode) {
            analyze((VariableAssignmentNode) statement);
        } else if (statement instanceof PrintStatementNode) {
            analyze((PrintStatementNode) statement);
        } else if (statement instanceof ForStatementNode) {
            analyze((ForStatementNode) statement);
        } else if (statement instanceof IfStatementNode) {
            analyze((IfStatementNode) statement);
        } else if (statement instanceof FunctionDeclarationNode) {
            analyze((FunctionDeclarationNode) statement);
        } else if (statement instanceof FunctionCallNode) {
            analyze((FunctionCallNode) statement);
        }
    }

    // 関数定義
    private void analyze(FunctionDeclarationNode node) throws SemanticException {
        if (currentScope().containsFunction(node.getLiteral().getName())) {
            throw new SemanticException("関数 " + node.getLiteral().getName() + "は既に定義されています。");
        }
        scopeStack.push(new SymbolTable());
        for (Map.Entry<String, TypeNode> entry : node.getLiteral().getParameterNode().get().entries()) {
            String key = entry.getKey();
            TypeNode value = entry.getValue();
            currentScope().defineVariable(key, value);
        }
        for (StatementNode statementNode : node.getLiteral().getStatementNode()) {
            analyze(statementNode);
        }
        TypeNode inferredReturnType = inferType(node.getLiteral().getReturnLiteral(), currentScope());
        TypeNode returnType = node.getLiteral().getReturnType();
        if (returnType != null && !returnType.isCompatibleWith(inferredReturnType)) {
            throw new SemanticException("返り値は" + returnType.getJavaTypeName() + "である必要があります。");
        }
        scopeStack.pop();
        currentScope().defineFunction(node.getLiteral().getName(), node.getLiteral());
    }

    // 関数呼び出し
    private void analyze(FunctionCallNode node) throws SemanticException {
        if (!currentScope().containsFunction(node.getName())) {
            throw new SemanticException("関数 " + node.getName() + "は定義されていません。");
        }
        FunctionLiteralNode functionLiteralNode = currentScope().getFunction(node.getName());
        Parameters<String, TypeNode> parameters = functionLiteralNode.getParameterNode().get();
        if (functionLiteralNode.getParameterNode().size() != node.getSize()) {
            throw new SemanticException(functionLiteralNode.getParameterNode().size() + "個の引数が必要ですが、" + node.getSize() + "個見つかりました。");
        }

        for (int i = 0; i < parameters.size(); i++) {
            Map.Entry<String, TypeNode> entry = parameters.get(i);
            TypeNode inferredType = inferType(node.getArg(i), currentScope());
            if (!inferredType.isCompatibleWith(entry.getValue())) {
                throw new SemanticException("引数 " + entry.getKey() + "は" + entry.getValue() + "である必要があります。");
            }
        }
    }

    // 式
    private void analyze(ComparisonExpressionNode node) throws SemanticException {
        if (node.getLeft() instanceof IdentifierNode) {
            String varName = ((IdentifierNode) node.getLeft()).getName();
            if (!currentScope().containsVariable(varName)) {
                throw new SemanticException("Asapi sapollata pasta '" + varName + "' kalivisku kittummusope.");
            }
        }

        if (node.getRight() instanceof IdentifierNode) {
            String varName = ((IdentifierNode) node.getRight()).getName();
            if (!currentScope().containsVariable(varName)) {
                throw new SemanticException("Asapi sapollata pasta '" + varName + "' kalivisku kittummusope.");
            }
        }

        TypeNode leftType = inferType(node.getLeft(), currentScope());
        TypeNode rightType = inferType(node.getRight(), currentScope());

        if (!leftType.isCompatibleWith(rightType)) {
            throw new SemanticException("型エラー: 比較式の両辺の型が一致しません。");
        }
    }

    // If文
    private void analyze(IfStatementNode node) throws SemanticException {
        TypeNode conditionType = inferType(node.getCondition(), currentScope());
        if (!(conditionType instanceof BooleanTypeNode)) {
            throw new SemanticException("条件式はbooleanである必要があります");
        }

        // if内
        scopeStack.push(new SymbolTable(currentScope()));
        for (StatementNode statement : node.getThenBody()) {
            analyze(statement);
        }
        scopeStack.pop();

        // else if
        for (ElseIfStatementNode elseIfStatement : node.getElseIfStatements()) {
            scopeStack.push(new SymbolTable(currentScope()));
            TypeNode elseIfConditionType = inferType(elseIfStatement.getCondition(), currentScope());
            if (!(elseIfConditionType instanceof BooleanTypeNode)) {
                throw new SemanticException("else ifの条件式はboolean型である必要があります。");
            }

            for (StatementNode statement : elseIfStatement.getBody()) {
                analyze(statement);
            }
            scopeStack.pop();
        }
        // else
        if (!node.getElseBody().isEmpty()) {
            scopeStack.push(new SymbolTable(currentScope()));
            for (StatementNode statement : node.getElseBody()) {
                analyze(statement);
            }
            scopeStack.pop();
        }
    }

    // 変数代入
    private void analyze(VariableAssignmentNode node) throws SemanticException {
        String variableName = node.getVariableName();
        ExpressionNode value = node.getValue();

        if (!currentScope().containsVariable(variableName)) {
            throw new SemanticException("Asapi jamusopi miniko pasta '" + variableName + "' kalivisku killemmusope.");
        }

        TypeNode declaredType = currentScope().getVariableType(variableName);
        TypeNode assignedType = inferType(value, currentScope());

        if (assignedType != null && !declaredType.isCompatibleWith(assignedType)) {
            throw new SemanticException("Japitasi tosaka " + variableName + " kikkate vas tosaka " + assignedType.getJavaTypeName());
        }
    }

    // for文
    private void analyze(ForStatementNode node) throws SemanticException {
        String counterVariableName = node.getCounterVariableName();
        ExpressionNode loopCountExpr = node.getLoopCount();

        scopeStack.push(new SymbolTable(currentScope()));

        TypeNode inferredLoopCountType = inferType(loopCountExpr, currentScope());
        if (!(inferredLoopCountType instanceof IntegerTypeNode)) {
            throw new SemanticException("型エラー: ループは整数回である必要があります");
        }

        if (currentScope().containsVariable(counterVariableName)) {
            throw new SemanticException("変数 '" + counterVariableName + "' は既に定義されています。");
        }
        currentScope().defineVariable(counterVariableName, new IntegerTypeNode());

        for (StatementNode statement : node.getBody()) {
            analyze(statement);
        }
        scopeStack.pop();
    }

    // 変数宣言ノードの意味解析
    private void analyze(VariableDeclarationNode node) throws SemanticException {
        String variableName = node.getVariableName();
        TypeNode declaredType = node.getVariableType();
        ExpressionNode initialValue = node.getValue();

        if (currentScope().containsVariable(variableName)) {
            throw new SemanticException("変数 '" + variableName + "' は既に定義されています。");
        }

        TypeNode inferredInitialType = inferType(initialValue, currentScope());
        if (inferredInitialType != null && !declaredType.isCompatibleWith(inferredInitialType)) {
            throw new SemanticException("Japitasi tosaka: '" + inferredInitialType.getJavaTypeName() +
                    "' kikkate vas tosaka '" + declaredType.getJavaTypeName());
        }

        currentScope().defineVariable(variableName, declaredType);
    }

    // print文ノードの意味解析
    private void analyze(PrintStatementNode node) throws SemanticException {
        // print文の式が有効な式であるか、変数を参照している場合はそれが定義済みかチェック
        ExpressionNode expr = node.getExpression();
        if (expr instanceof IdentifierNode) {
            String varName = ((IdentifierNode) expr).getName();
            if (!currentScope().containsVariable(varName)) {
                throw new SemanticException("Asapi sapollata pasta '" + varName + "' kalivisku kittummusope.");
            }
        }
        // リテラルの場合は常に有効
    }
}
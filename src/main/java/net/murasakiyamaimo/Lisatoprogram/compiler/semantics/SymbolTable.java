package net.murasakiyamaimo.Lisatoprogram.compiler.semantics;

import net.murasakiyamaimo.Lisatoprogram.compiler.ast.FunctionLiteralNode;
import net.murasakiyamaimo.Lisatoprogram.compiler.ast.TypeNode;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final SymbolTable parent;
    private Map<String, TypeNode> variables = new HashMap<>();
    private Map<String, FunctionLiteralNode> functions = new HashMap<>();

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable() {
        this(null);
    }

    public void defineFunction(String name, FunctionLiteralNode function) throws SemanticException {
        if (functions.containsKey(name)) {
            throw new SemanticException("関数 " + name + "は既に定義されています。");
        }
        functions.put(name, function);
    }

    public FunctionLiteralNode getFunction(String name) {
        if (functions.containsKey(name)) {
            return functions.get(name);
        }
        if (parent != null) {
            return parent.getFunction(name);
        }
        return null;
    }

    public boolean containsFunction(String name) {
        return getFunction(name) != null;
    }

    public void defineVariable(String name, TypeNode type) throws SemanticException {
        if (variables.containsKey(name)) {
            throw new SemanticException("変数 " + name + "は既に定義されています。");
        }
        variables.put(name, type);
    }

    public TypeNode getVariableType(String name) {
        // 現在のスコープで検索
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        // 親スコープを遡って検索
        if (parent != null) {
            return parent.getVariableType(name);
        }
        return null;
    }

    public boolean containsVariable(String name) {
        return getVariableType(name) != null;
    }
}

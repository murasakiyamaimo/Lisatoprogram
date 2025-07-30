package net.murasakiyamaimo.Lisatoprogram.compiler.semantics;

import net.murasakiyamaimo.Lisatoprogram.compiler.ast.TypeNode;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, TypeNode> variables = new HashMap<>();

    public void defineVariable(String name, TypeNode type) throws SemanticException {
        if (variables.containsKey(name)) {
            throw new SemanticException("変数 " + name + "は既に定義されています。");
        }
        variables.put(name, type);
    }

    public void removeVariable(String name) throws SemanticException {
        if (variables.get(name) == null) {
            throw new SemanticException("そのような変数" + name + "は存在しません");
        }
        variables.remove(name);
    }

    public TypeNode getVariableType(String name) {
        return variables.get(name);
    }

    public boolean containsVariable(String name) {
        return variables.containsKey(name);
    }
}

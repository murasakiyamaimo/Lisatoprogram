package net.murasakiyamaimo.Lisatoprogram.compiler.ast;

import net.murasakiyamaimo.Lisatoprogram.tools.Parameters;

public class ParameterNode {
    private Parameters<String, TypeNode> parameters = new Parameters<>();

    public void add(String name, TypeNode type) {
        parameters.add(name, type);
    }

    public Parameters<String, TypeNode> get() {
        return parameters;
    }

    public int size() {
        return parameters.size();
    }
}

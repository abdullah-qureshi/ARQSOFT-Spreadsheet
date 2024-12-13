package org.example;

import java.util.List;

abstract class FormulaNode {
    private final List<FormulaNode> children;

    public FormulaNode(List<FormulaNode> children) {
        this.children = children;
    }

    public List<FormulaNode> getChildren() {
        return children;
    }

    public abstract double evaluate(Spreadsheet spreadsheet);

    @Override
    public abstract String toString();
}
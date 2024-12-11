package org.example;

import java.util.List;

class MultiplicationNode extends FormulaNode {

    public MultiplicationNode(List<FormulaNode> children) {
        super(children);
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        double result = 1; // Multiplicative identity
        for (FormulaNode child : getChildren()) {
            result *= child.evaluate(spreadsheet);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("(");
        List<FormulaNode> children = getChildren();

        for (int i = 0; i < children.size(); i++) {
            result.append(children.get(i).toString());
            if (i < children.size() - 1) {
                result.append(" * ");
            }
        }

        result.append(")");
        return result.toString();
    }
}

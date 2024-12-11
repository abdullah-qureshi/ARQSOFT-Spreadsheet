package org.example;

import java.util.List;

class MinNode extends FormulaNode {

    public MinNode(List<FormulaNode> children) {
        super(children);
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        if (getChildren().isEmpty()) {
            throw new IllegalStateException("MinNode requires at least one operand.");
        }

        double min = Double.POSITIVE_INFINITY; // Start with the smallest possible value
        for (FormulaNode child : getChildren()) {
            double value = child.evaluate(spreadsheet);
            min = Math.min(min, value);
        }
        return min;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("MIN(");
        List<FormulaNode> children = getChildren();

        for (int i = 0; i < children.size(); i++) {
            result.append(children.get(i).toString());
            if (i < children.size() - 1) {
                result.append(", ");
            }
        }

        result.append(")");
        return result.toString();
    }
}

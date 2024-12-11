package org.example;

import java.util.List;

class MaxNode extends FormulaNode {

    public MaxNode(List<FormulaNode> children) {
        super(children);
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        if (getChildren().isEmpty()) {
            throw new IllegalStateException("MaxNode requires at least one operand.");
        }

        double max = Double.NEGATIVE_INFINITY; // Start with the smallest possible value
        for (FormulaNode child : getChildren()) {
            double value = child.evaluate(spreadsheet);
            max = Math.max(max, value);
        }
        return max;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("MAX(");
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

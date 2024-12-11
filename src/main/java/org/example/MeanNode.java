package org.example;

import java.util.List;

class MeanNode extends FormulaNode {

    public MeanNode(List<FormulaNode> children) {
        super(children);
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        if (getChildren().isEmpty()) {
            throw new IllegalStateException("MeanNode requires at least one operand.");
        }

        double sum = 0;
        int count = 0;

        for (FormulaNode child : getChildren()) {
            sum += child.evaluate(spreadsheet);
            count++;
        }

        return sum / count;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("MEAN(");
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

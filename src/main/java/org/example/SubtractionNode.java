package org.example;

import java.util.List;

class SubtractionNode extends FormulaNode {

    public SubtractionNode(List<FormulaNode> children) {
        super(children);
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        List<FormulaNode> children = getChildren();
        if (children.isEmpty()) {
            throw new IllegalStateException("InversionNode requires at least one operand.");
        }

        // Start with the value of the first child
        double result = children.get(0).evaluate(spreadsheet);

        // Sequentially subtract the other children
        for (int i = 1; i < children.size(); i++) {
            result -= children.get(i).evaluate(spreadsheet);
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
                result.append(" - ");
            }
        }

        result.append(")");
        return result.toString();
    }
}

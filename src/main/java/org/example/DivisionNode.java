package org.example;

import java.util.List;

class DivisionNode extends FormulaNode {

    public DivisionNode(List<FormulaNode> children) {
        super(children);
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        List<FormulaNode> children = getChildren();
        if (children.isEmpty()) {
            throw new IllegalStateException("DivisionNode requires at least one operand.");
        }

        // Start with the value of the first child
        double result = children.get(0).evaluate(spreadsheet);

        // Sequentially divide by the other children
        for (int i = 1; i < children.size(); i++) {
            double divisor = children.get(i).evaluate(spreadsheet);
            if (divisor == 0) {
                throw new ArithmeticException("Division by zero.");
            }
            result /= divisor;
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
                result.append(" / ");
            }
        }

        result.append(")");
        return result.toString();
    }
}

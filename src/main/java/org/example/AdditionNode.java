package org.example;
import java.util.List;

class AdditionNode extends FormulaNode {

    public AdditionNode(List<FormulaNode> children) {
        super(children);
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        double sum = 0;
        for (FormulaNode child : getChildren()) {
            sum += child.evaluate(spreadsheet); // Evaluate each child and add to the sum
        }
        return sum;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("(");
        List<FormulaNode> children = getChildren();

        for (int i = 0; i < children.size(); i++) {
            result.append(children.get(i).toString());
            if (i < children.size() - 1) {
                result.append(" + ");
            }
        }

        result.append(")");
        return result.toString();
    }
}

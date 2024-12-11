package org.example;

class FormulaContent extends Content {
    private FormulaNode root;
    private String lastValue;

    public FormulaContent(FormulaNode root) {
        this.root = root; // Root node can be null if no formula is present
        this.lastValue = "";
    }

    @Override
    public String toString() {
        return root == null ? "" : "=" + root.toString();
    }

    // Evaluates the formula starting from the root node
    public double evaluateFormula(Spreadsheet spreadsheet) {
        if (root == null) {
            throw new IllegalStateException("No formula present to evaluate.");
        }
        // Compute the new value and store it
        double value = root.evaluate(spreadsheet);
        lastValue = Double.toString(value);
        return value;
    }

    // Get the last evaluated value (for display)
    public String getLastValue() {
        return lastValue;
    }

    public FormulaNode getRoot(){
        return root;
    }
}

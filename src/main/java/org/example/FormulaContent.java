package org.example;

class FormulaContent extends Content {
    private final FormulaNode root;
    private String lastValue;

    public FormulaContent(FormulaNode root) {
        this.root = root;
        this.lastValue = "";
    }

    public double evaluateFormula(Spreadsheet spreadsheet, String coordinate) {
        if (root == null) {
            throw new IllegalStateException("Formula is empty");
        }

        if (hasCircularReference(coordinate)) {
            lastValue = "#CIRCULAR";
            throw new IllegalArgumentException("#ERROR_CIRCULAR_REFERENCE");
        }

        double result = root.evaluate(spreadsheet);
        lastValue = String.valueOf(result);
        return result;
    }

    public FormulaNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return root == null ? "" : "=" + root;
    }

    public String getLastValue() {
        return lastValue;
    }

    public boolean hasCircularReference(String coordinate) {
        return root.containsReference(coordinate);
    }
}
package org.example;

class FormulaContent extends Content {
        private FormulaNode root;
    private String lastValue;
    private boolean isEvaluating; // Add this flag to detect cycles during evaluation

    public FormulaContent(FormulaNode root) {
        this.root = root;
        this.lastValue = "";
        this.isEvaluating = false;
    }

    public double evaluateFormula(Spreadsheet spreadsheet, String coordinate) {
        if (root == null) {
            throw new IllegalStateException("No formula present to evaluate.");
        }
        
        if (isEvaluating) {
            lastValue = "#CIRCULAR";
            throw new IllegalArgumentException("#ERROR_CIRCULAR_REFERENCE");
        }

        try {
            isEvaluating = true;
            double value = root.evaluate(spreadsheet);
            lastValue = Double.toString(value);
            return value;
        } finally {
            isEvaluating = false;
        }
    }

    @Override
    public String toString() {
        return root == null ? "" : "=" + root.toString();
    }

    public boolean detectCircularReference(String coordinate){
        return root.containsReference(coordinate);
    }

    // Get the last evaluated value (for display)
    public String getLastValue() {
        return lastValue;
    }

    public FormulaNode getRoot(){
        return root;
    }
}

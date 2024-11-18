class FormulaContent extends Content {
    private FormulaNode root;

    public FormulaContent(FormulaNode root) {
        this.root = root; // Root node can be null if no formula is present
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
        return root.evaluate(spreadsheet);
    }
}

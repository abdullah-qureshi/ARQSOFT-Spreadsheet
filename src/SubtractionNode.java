class SubtractionNode extends FormulaNode {
    private final FormulaNode left;
    private final FormulaNode right;

    public SubtractionNode(FormulaNode left, FormulaNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        return left.evaluate(spreadsheet) - right.evaluate(spreadsheet);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " - " + right.toString() + ")";
    }
}

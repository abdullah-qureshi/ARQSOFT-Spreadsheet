class DivisionNode extends FormulaNode {
    private final FormulaNode left;
    private final FormulaNode right;

    public DivisionNode(FormulaNode left, FormulaNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        double rightValue = right.evaluate(spreadsheet);
        if (rightValue == 0) {
            throw new ArithmeticException("Division by zero in formula.");
        }
        return left.evaluate(spreadsheet) / rightValue;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " / " + right.toString() + ")";
    }
}

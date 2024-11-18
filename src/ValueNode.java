class ValueNode extends FormulaNode {
    private final double value;

    public ValueNode(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}

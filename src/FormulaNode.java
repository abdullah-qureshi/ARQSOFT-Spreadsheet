abstract class FormulaNode {
    public abstract double evaluate(Spreadsheet spreadsheet);

    @Override
    public abstract String toString();
}

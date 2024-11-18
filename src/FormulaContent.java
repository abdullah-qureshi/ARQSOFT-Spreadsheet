// FormulaContent.java
class FormulaContent extends Content {
    private String formula;

    public FormulaContent(String formula) {
        this.formula = formula;
    }

    @Override
    public String toString() {
        return formula;
    }
}
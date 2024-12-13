package org.example;
import java.util.List;

class CellNode extends FormulaNode {
    private final String coordinate;

    public CellNode(String coordinate) {
        super(List.of()); // Pass an empty list as children
        this.coordinate = coordinate;
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        Cell cell = spreadsheet.getCell(coordinate);
        if (cell == null) {
//            return 0;
            throw new IllegalArgumentException("Referenced cell does not exist: " + coordinate);
        }

        Content content = cell.getContent();
        if (content instanceof NumericContent) {
            return Double.parseDouble(cell.getContentString());
        } else if (content instanceof FormulaContent formulaContent) {
            return Double.parseDouble(formulaContent.getLastValue());
        }
        return 0;

//        throw new IllegalArgumentException("Referenced cell does not contain a numeric or formula value: " + coordinate);
    }

    public String getCoordinate() {
        return coordinate;
    }

    @Override
    public String toString() {
        return coordinate; // Displays the coordinate of the cell, e.g., "A1"
    }
}

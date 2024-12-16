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
            throw new IllegalArgumentException("Referenced cell does not exist: " + coordinate);
        }

        Content content = cell.getContent();
        if (content instanceof NumericContent) {
            return Double.parseDouble(cell.getContentString());
        } else if (content instanceof FormulaContent formulaContent) {
            // First check for circular reference
            if (formulaContent.getLastValue().equals("#CIRCULAR")) {
                throw new IllegalArgumentException("#ERROR_CIRCULAR_REFERENCE");
            }
            
            try {
                return spreadsheet.evaluateCell(coordinate);
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("#ERROR_CIRCULAR_REFERENCE")) {
                    throw new IllegalArgumentException("#ERROR_CIRCULAR_REFERENCE");
                }
                throw e;
            }
        } else if (content instanceof TextContent && 
                content.toString().equals("#ERROR_CIRCULAR_REFERENCE")) {
            throw new IllegalArgumentException("#ERROR_CIRCULAR_REFERENCE");
        }

        throw new IllegalArgumentException("#ERROR_CIRCULAR_REFERENCE");
    }

    public String getCoordinate() {
        return coordinate;
    }

    @Override
    public String toString() {
        return coordinate;
    }

    @Override
    public boolean containsReference(String coordinate) {
        // Check if this cell directly references the target coordinate
        return coordinate.equals(this.coordinate) || super.containsReference(coordinate);
    }
}

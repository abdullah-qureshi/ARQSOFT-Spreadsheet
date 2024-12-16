package org.example;

import java.util.ArrayList;
import java.util.List;

public class Spreadsheet {
    private final List<List<Cell>> cells;

    public Spreadsheet() {
        cells = new ArrayList<>();
    }

    public Cell getCell(String coordinate) {
        int[] indices = parseCoordinate(coordinate);
        if (indices != null) {
            ensureCapacity(indices[0], indices[1]);
            return cells.get(indices[0]).get(indices[1]);
        }
        return null;
    }

    public List<List<Cell>> getCells() {
        return cells;
    }
    public void displaySpreadsheet() {
        int maxRows = cells.size();
        int maxCols = getMaxCols();

        System.out.print("   ");
        for (int col = 0; col < maxCols; col++) {
            System.out.printf("%-10s", getColumnName(col));
        }
        System.out.println();

        for (int row = 0; row < maxRows; row++) {
            System.out.printf("%-3d", row + 1);

            List<Cell> rowCells = cells.get(row);
            for (int col = 0; col < maxCols; col++) {
                if (col < rowCells.size()) {
                    Cell cell = rowCells.get(col);
                    String displayValue = getCellDisplayValue(cell);
                    System.out.printf("%-10s", displayValue);
                } else {
                    System.out.printf("%-10s", "");
                }
            }
            System.out.println();
        }
    }

    public void setCellContent(String coordinate, Content content) {
        Cell cell = getCell(coordinate);
        if (cell == null) {
        throw new IllegalArgumentException("Invalid cell coordinate.");
        }

        clearDependencies(cell);

        if (content instanceof FormulaContent formulaContent) {
            List<String> dependencies = extractDependencies(formulaContent);
            for (String dependentCoordinate : dependencies) {
                Cell dependentCell = getCell(dependentCoordinate);
                if (dependentCell != null) {
                    dependentCell.addDependent(cell);
                }
            }
        }

        cell.setContent(content);

        // Evaluate formula but keep the formula content
        if (content instanceof FormulaContent formulaContent) {
            try {
                formulaContent.evaluateFormula(this, coordinate);
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("#ERROR_CIRCULAR_REFERENCE")) {
                    cell.setContent(new TextContent("#ERROR_CIRCULAR_REFERENCE"));
                } else {
                    cell.setContent(new TextContent(e.getMessage()));
                }
            }
        }
        
        cell.updateDependents(this, coordinate);
    }   

    private String getCellDisplayValue(Cell cell) {
        Content content = cell.getContent();
        if (content instanceof NumericContent) {
            return cell.getContentString();
        } else if (content instanceof FormulaContent formulaContent) {
            return formulaContent.getLastValue(); // Use cached value
        } else if (content != null) {
            return content.toString();
        }
        return "";
    }

    private void clearDependencies(Cell cell) {
        for (List<Cell> row : cells) {
            for (Cell otherCell : row) {
                otherCell.removeDependent(cell);
            }
        }
    }

    private List<String> extractDependencies(FormulaContent formulaContent) {
        List<String> dependencies = new ArrayList<>();
        collectDependencies(formulaContent.getRoot(), dependencies);
        return dependencies;
    }

    private void collectDependencies(FormulaNode node, List<String> dependencies) {
        if (node instanceof CellNode cellNode) {
            if (!dependencies.contains(cellNode.getCoordinate())) {
                dependencies.add(cellNode.getCoordinate());
            }
        } else if (node instanceof ValueNode) {
            // No dependencies for value nodes
        } else if (node.getChildren() != null) {
            for (FormulaNode child : node.getChildren()) {
                collectDependencies(child, dependencies);
            }
        }
    }

    public double evaluateCell(String coordinate) {
        Cell cell = getCell(coordinate);
        if (cell == null) {
            throw new IllegalArgumentException("Cell does not exist: " + coordinate);
        }

        Content content = cell.getContent();
        if (content instanceof NumericContent) {
            return Double.parseDouble(cell.getContentString());
        } else if (content instanceof FormulaContent) {
            try{
                System.out.println("Evaluating formula at " + coordinate);
                return ((FormulaContent) content).evaluateFormula(this, coordinate);
            } catch (IllegalArgumentException e) {
                System.out.println("Error evaluating formula at " + coordinate + ": " + e.getMessage());
                cell.setContent(new TextContent(e.getMessage()));
            }
        }

        throw new IllegalArgumentException("Cell does not contain a numeric or formula value: " + coordinate);
    }

    private String getColumnName(int colIndex) {
        StringBuilder columnName = new StringBuilder();
        while (colIndex >= 0) {
            columnName.insert(0, (char) ('A' + (colIndex % 26)));
            colIndex = (colIndex / 26) - 1;
        }
        return columnName.toString();
    }

    private int getMaxCols() {
        int maxCols = 0;
        for (List<Cell> row : cells) {
            if (row.size() > maxCols) {
                maxCols = row.size();
            }
        }
        return maxCols;
    }

    private void ensureCapacity(int row, int col) {
        while (cells.size() <= row) {
            cells.add(new ArrayList<>());
        }
        for (int i = 0; i <= row; i++) {
            List<Cell> rowCells = cells.get(i);
            while (rowCells.size() <= col) {
                rowCells.add(new Cell(getCoordinate(i, rowCells.size())));
            }
        }
    }

    private String getCoordinate(int row, int col) {
        return getColumnName(col) + Integer.toString(row + 1);
    }

    private int[] parseCoordinate(String coordinate) {
        if (coordinate == null || coordinate.length() < 2) return null;

        int i = 0;
        while (i < coordinate.length() && Character.isLetter(coordinate.charAt(i))) {
            i++;
        }
        String columnPart = coordinate.substring(0, i).toUpperCase();
        String rowPart = coordinate.substring(i);

        int rowIndex;
        try {
            rowIndex = Integer.parseInt(rowPart) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        int colIndex = parseColumnName(columnPart);
        if (rowIndex >= 0 && colIndex >= 0) {
            return new int[]{rowIndex, colIndex};
        }
        return null;
    }

    private int parseColumnName(String columnName) {
        int colIndex = 0;
        for (int i = 0; i < columnName.length(); i++) {
            colIndex = colIndex * 26 + (columnName.charAt(i) - 'A' + 1);
        }
        return colIndex - 1;
    }
}

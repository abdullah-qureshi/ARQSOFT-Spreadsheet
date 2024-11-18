import java.util.ArrayList;
import java.util.List;

public class Spreadsheet {
    private List<List<Cell>> cells;

    public Spreadsheet() {
        cells = new ArrayList<>(); // Initialize an empty spreadsheet
    }

    public Cell getCell(String coordinate) {
        int[] indices = parseCoordinate(coordinate);
        if (indices != null) {
            ensureCapacity(indices[0], indices[1]);
            return cells.get(indices[0]).get(indices[1]);
        }
        return null;
    }

    public void setCellContent(String coordinate, Content content) {
        Cell cell = getCell(coordinate);
        if (cell != null) {
            cell.setContent(content);
        } else {
            System.out.println("Invalid cell coordinate.");
        }
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
                    String content = rowCells.get(col).getContentString();
                    System.out.printf("%-10s", content);
                } else {
                    System.out.printf("%-10s", ""); // Empty cell space
                }
            }
            System.out.println();
        }
    }

    // return the list of cells
    public List<List<Cell>> getCells() {
        return cells;
    }

    private String getColumnName(int colIndex) {
        StringBuilder columnName = new StringBuilder();
        while (colIndex >= 0) {
            columnName.insert(0, (char) ('A' + (colIndex % 26)));
            colIndex = (colIndex / 26) - 1;
        }
        return columnName.toString();
    }

    // get the maximum number of columns in any row
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

    // Convert row/column indices to a cell coordinate
    private String getCoordinate(int row, int col) {
        return getColumnName(col) + Integer.toString(row + 1);
    }

    // Convert a cell coordinate to row and column indices
    private int[] parseCoordinate(String coordinate) {
        if (coordinate == null || coordinate.length() < 2) return null;

        // Extract column part
        int i = 0;
        while (i < coordinate.length() && Character.isLetter(coordinate.charAt(i))) {
            i++;
        }
        String columnPart = coordinate.substring(0, i).toUpperCase();
        String rowPart = coordinate.substring(i);

        int rowIndex;
        try {
            rowIndex = Integer.parseInt(rowPart) - 1; // Convert row part to 0-based index
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
        return colIndex - 1; // Adjust for 0-based index
    }
}

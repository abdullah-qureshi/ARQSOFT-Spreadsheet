import java.io.*;
import java.util.List;

public class SpreadsheetFileManager {

    public static void saveSpreadsheet(Spreadsheet spreadsheet, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            List<List<Cell>> cells = spreadsheet.getCells();
            for (List<Cell> row : cells) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < row.size(); i++) {
                    Cell cell = row.get(i);
                    String contentString = cell.getContentString().replace(";", "\\;"); // Escape ;
                    line.append(contentString);
                    if (i < row.size() - 1) {
                        line.append(";"); // Delimit between cells
                    }
                }
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }

    public static Spreadsheet loadSpreadsheet(String filename) throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int rowNumber = 0;
            while ((line = reader.readLine()) != null) {
                String[] contents = line.split("(?<!\\\\);"); // Split by ; not preceded by \
                for (int colNumber = 0; colNumber < contents.length; colNumber++) {
                    String contentString = contents[colNumber].replace("\\;", ";"); // Unescape ;
                    Content content = parseContent(contentString);
                    String coordinate = getCoordinate(rowNumber, colNumber);
                    spreadsheet.setCellContent(coordinate, content);
                }
                rowNumber++;
            }
        }
        return spreadsheet;
    }


    // Parse content based on the first character
    private static Content parseContent(String contentString) {
        if (contentString.startsWith("=")) {
            return new FormulaContent(contentString); // Formula content
        } else {
            try {
                return new NumericContent(Double.parseDouble(contentString)); // Numeric content
            } catch (NumberFormatException e) {
                return new TextContent(contentString); // Text content
            }
        }
    }

    // Convert row and column indices to a cell coordinate (e.g., A1, B2)
    private static String getCoordinate(int row, int col) {
        StringBuilder columnName = new StringBuilder();
        while (col >= 0) {
            columnName.insert(0, (char) ('A' + (col % 26)));
            col = (col / 26) - 1;
        }
        return columnName.toString() + (row + 1);
    }
}

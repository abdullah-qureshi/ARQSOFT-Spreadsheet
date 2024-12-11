package org.example;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static Spreadsheet spreadsheet = new Spreadsheet();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Spreadsheet");
        while (true) {
            displayMenu();
            command = scanner.nextLine();

            switch (command) {
                case "1":
                    setCellContent(scanner);
                    break;
                case "2":
                    showCellContent(scanner);
                    break;
                case "3":
                    saveSpreadsheet(scanner);
                    break;
                case "4":
                    loadSpreadsheet(scanner);
                    break;
                case "5":
                    displaySpreadsheet(); // Display spreadsheet in table format
                    break;
                case "6":
                    System.out.println("Exiting the program...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Set cell content");
        System.out.println("2. Show cell content");
        System.out.println("3. Save spreadsheet");
        System.out.println("4. Load spreadsheet");
        System.out.println("5. Display all cells");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void setCellContent(Scanner scanner) {
        System.out.print("Enter cell coordinate (e.g., A1): ");
        String coordinate = scanner.nextLine();
        System.out.print("Enter cell content: ");
        String content = scanner.nextLine();

        Content cellContent;
        if (content.startsWith("=")) {
//            try {
                // Parse the formula here
                System.out.println("Parsing: " + content);
                FormulaNode rootNode = FormulaParser.parse(content);
                if (rootNode == null){
                    cellContent = new TextContent("#ERROR");
                }
                else{
                    cellContent = new FormulaContent(rootNode);
                }

//            } catch (Exception e) {
//                System.out.println("Error parsing formula: " + e.getMessage());
//                return;
//            }
        } else if (isNumeric(content)) {
            cellContent = new NumericContent(Double.parseDouble(content));
        } else {
            cellContent = new TextContent(content);
        }
        spreadsheet.setCellContent(coordinate, cellContent);
    }

    private static void showCellContent(Scanner scanner) {
        System.out.print("Enter cell coordinate to view (e.g., A1): ");
        String coordinate = scanner.nextLine();
        Cell cell = spreadsheet.getCell(coordinate);
        if (cell != null) {
            System.out.println("Cell " + coordinate + " content: " + cell.getContentString());
        } else {
            System.out.println("Cell does not exist.");
        }
    }

    private static void saveSpreadsheet(Scanner scanner) {
        System.out.print("Enter filename to save: ");
        String filename = scanner.nextLine();
        try {
            SpreadsheetFileManager.saveSpreadsheet(spreadsheet, filename);
            System.out.println("Spreadsheet saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving spreadsheet: " + e.getMessage());
        }
    }

    private static void loadSpreadsheet(Scanner scanner) {
        System.out.print("Enter filename to load: ");
        String filename = scanner.nextLine();
        try {
            spreadsheet = SpreadsheetFileManager.loadSpreadsheet(filename);
            System.out.println("Spreadsheet loaded from " + filename);
        } catch (IOException e) {
            System.out.println("Error loading spreadsheet: " + e.getMessage());
        }
    }

    private static void displaySpreadsheet() {
        System.out.println("Current Spreadsheet Content:");
        spreadsheet.displaySpreadsheet();
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

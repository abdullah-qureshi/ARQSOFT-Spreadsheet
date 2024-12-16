package org.example;

import static org.junit.jupiter.api.Assertions.*;

class SpreadsheetTest {
    
    @org.junit.jupiter.api.Test
    void testSaveAndLoadSpreadsheet() throws Exception {
        Spreadsheet original = new Spreadsheet();
        original.setCellContent("A1", new NumericContent(42.0));
        original.setCellContent("B1", new TextContent("Hello"));
        original.setCellContent("C1", new FormulaContent(FormulaParser.parse("=A1+10")));

        String tempFile = "test_spreadsheet.s2v";
        SpreadsheetFileManager.saveSpreadsheet(original, tempFile);
        Spreadsheet loaded = SpreadsheetFileManager.loadSpreadsheet(tempFile);

        assertEquals("42.0", loaded.getCell("A1").getContentString());
        assertEquals("Hello", loaded.getCell("B1").getContentString());
        assertEquals(52.0, loaded.evaluateCell("C1"));

        // Clean up
        java.io.File file = new java.io.File(tempFile);
        file.delete();
    }

}
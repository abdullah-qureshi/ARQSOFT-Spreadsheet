package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FormulaParserTest {
    @Test
    void testInvalidFormula() {
        String input = "=10++5";
        FormulaNode node = FormulaParser.parse(input);
        assertNull(node, "Parser should return null for invalid formula");
    }

    @Test
    void testAddition() {
        String input = "=10+15";
        FormulaNode node = FormulaParser.parse(input);
        assertNotNull(node, "Parser should return a valid node");

        Spreadsheet spreadsheet = new Spreadsheet();
        double result = node.evaluate(spreadsheet);
        assertEquals(25.0, result, "10 + 15 should equal 25");
    }

    @Test
    void testMultiplication() {
        String input = "=10*5";
        FormulaNode node = FormulaParser.parse(input);
        assertNotNull(node, "Parser should return a valid node");

        Spreadsheet spreadsheet = new Spreadsheet();
        double result = node.evaluate(spreadsheet);
        assertEquals(50.0, result, "10 * 5 should equal 50");
    }

    @Test
    void testDivision() {
        String input = "=20/4";
        FormulaNode node = FormulaParser.parse(input);
        assertNotNull(node, "Parser should return a valid node");
        
        Spreadsheet spreadsheet = new Spreadsheet();
        double result = node.evaluate(spreadsheet);
        assertEquals(5.0, result, "20 / 4 should equal 5");
    }

    @Test
    void testSubtraction() {
        String input = "=30-12";
        FormulaNode node = FormulaParser.parse(input);
        assertNotNull(node, "Parser should return a valid node");
        
        Spreadsheet spreadsheet = new Spreadsheet();
        double result = node.evaluate(spreadsheet);
        assertEquals(18.0, result, "30 - 12 should equal 18");
    }

    @Test
    void testComplexExpression() {
        String input = "=(10+5)*2";
        FormulaNode node = FormulaParser.parse(input);
        assertNotNull(node, "Parser should return a valid node");
        
        Spreadsheet spreadsheet = new Spreadsheet();
        double result = node.evaluate(spreadsheet);
        assertEquals(30.0, result, "(10 + 5) * 2 should equal 30 (respecting operator precedence)");
    }

    @Test
    void testCellReference() {
        // Setup spreadsheet with a value in cell A1
        Spreadsheet spreadsheet = new Spreadsheet();
        spreadsheet.setCellContent("A1", new NumericContent(10.0));
        
        String input = "=A1+5";
        FormulaNode node = FormulaParser.parse(input);
        assertNotNull(node, "Parser should return a valid node");
        
        double result = node.evaluate(spreadsheet);
        assertEquals(15.0, result, "A1 (10) + 5 should equal 15");
    }
    @Test
    void testSUMAFunction() {
        // Setup spreadsheet with a value in cell A1
        Spreadsheet spreadsheet = new Spreadsheet();
        spreadsheet.setCellContent("A1", new NumericContent(10.0));
        spreadsheet.setCellContent("A2", new NumericContent(20.0));
        spreadsheet.setCellContent("A3", new NumericContent(30.0));

Add        String input = "=SUMA(A1:A3)";
        FormulaNode node = FormulaParser.parse(input);
        assertNotNull(node, "Parser should return a valid node");

        double result = node.evaluate(spreadsheet);
        assertEquals(60.0, result, "SUMA should return 10+20+30=60");
    }
}
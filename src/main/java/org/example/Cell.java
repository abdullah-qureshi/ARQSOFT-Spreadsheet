package org.example;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private String coordinate;
    private Content content;
    private List<Cell> dependents; // Use a List to store dependents

    public Cell(String coordinate) {
        this.coordinate = coordinate;
        this.content = new TextContent(""); // default empty text content
        this.dependents = new ArrayList<>();
    }

    public String getCoordinate() {
        return coordinate;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getContentString() {
        return content.toString();
    }

    // Add a dependant cell whose value is changed with this cell e.g. a formula
    public void addDependent(Cell dependent) {
        if (!dependents.contains(dependent)) { // Avoid duplicate entries
            dependents.add(dependent);
        }
    }

    public void removeDependent(Cell dependent) {
        dependents.remove(dependent);
    }

    public List<Cell> getDependents() {
        return dependents;
    }

    public void updateDependents(Spreadsheet spreadsheet) {
        for (Cell dependent : dependents) {
            Content dependentContent = dependent.getContent();
            if (dependentContent instanceof FormulaContent formulaContent) {
                formulaContent.evaluateFormula(spreadsheet); // Reevaluate the formula
                dependent.updateDependents(spreadsheet); // Recursively notify dependents
            }
        }
    }
}


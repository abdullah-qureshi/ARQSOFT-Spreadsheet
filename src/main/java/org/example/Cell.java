package org.example;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private String coordinate;
    private Content content;
    private FormulaContent Formula;
    private List<Cell> dependents;

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
        if (content instanceof FormulaContent) {
            this.Formula = (FormulaContent) content;
        }
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

    public void updateDependents(Spreadsheet spreadsheet, String coordinate) {
        for (Cell dependent : dependents) {
            Content dependentContent = dependent.getContent();
            if (dependentContent instanceof FormulaContent) {
                // Re-set the content to trigger a complete re-evaluation
                Content originalContent = dependent.getContent();
                dependent.setContent(originalContent);
                
                // Continue updating the chain of dependencies
                dependent.updateDependents(spreadsheet, coordinate);
            }
        }
    }

    public FormulaContent getOriginalFormula() {
        return Formula;
    }
}


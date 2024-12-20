package org.example;

import java.util.List;

class ValueNode extends FormulaNode {
    private final double value;

    public ValueNode(double value) {
        super(List.of()); // Pass an empty list as children
        this.value = value;
    }

    @Override
    public double evaluate(Spreadsheet spreadsheet) {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString(value); 
    }
}

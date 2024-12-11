package org.example;

class NumericContent extends Content {
    private double number;

    public NumericContent(double number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return Double.toString(number);
    }
}
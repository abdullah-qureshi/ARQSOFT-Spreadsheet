package org.example;

class TextContent extends Content {
    private String text;

    public TextContent(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
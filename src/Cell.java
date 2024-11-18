// Cell.java
public class Cell {
    private String coordinate;
    private Content content;

    public Cell(String coordinate) {
        this.coordinate = coordinate;
        this.content = new TextContent(""); // default empty text content
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
}

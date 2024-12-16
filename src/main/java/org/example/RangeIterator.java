package org.example;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RangeIterator implements Iterator<String> {
    private final char startCol, endCol;
    private final int startRow, endRow;
    private char currentCol;
    private int currentRow;
    private final Spreadsheet spreadsheet;

    public RangeIterator(String startCell, String endCell, Spreadsheet spreadsheet) {
        this.startCol = startCell.charAt(0);
        this.startRow = Integer.parseInt(startCell.substring(1));
        this.endCol = endCell.charAt(0);
        this.endRow = Integer.parseInt(endCell.substring(1));
        this.currentCol = this.startCol;
        this.currentRow = this.startRow;
        this.spreadsheet = spreadsheet;
        findNextNonEmptyCell(); 
    }

    private void findNextNonEmptyCell() {
        while (hasNext()) {
            String currentRef = String.valueOf(currentCol) + currentRow;
            Cell cell = spreadsheet.getCell(currentRef);
            if (cell != null && cell.getContent() != null) {
                return;
            }
            moveToNextPosition();
        }
    }

    private void moveToNextPosition() {
        currentRow++;
        if (currentRow > endRow) {
            currentRow = startRow;
            currentCol++;
        }
    }

    @Override
    public boolean hasNext() {
        return currentCol <= endCol && 
               !(currentCol == endCol && currentRow > endRow);
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        String currentRef = String.valueOf(currentCol) + currentRow;
        moveToNextPosition();
        findNextNonEmptyCell();
        return currentRef;
    }
}
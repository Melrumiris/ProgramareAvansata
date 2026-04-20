package org.example;

public class Cell {

    public final int row;
    public final int col;

    public boolean top;
    public boolean right;
    public boolean bottom;
    public boolean left;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        resetWalls();
    }

    public void resetWalls() {
        top = true;
        right = true;
        bottom = true;
        left = true;
    }
}

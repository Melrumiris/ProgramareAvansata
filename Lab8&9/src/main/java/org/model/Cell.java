package org.model;

import java.io.Serializable;

public class Cell implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int row;
    private final int col;

    private boolean topWall;
    private boolean rightWall;
    private boolean bottomWall;
    private boolean leftWall;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        resetWalls();
    }

    // --- Position (immutable) ---

    public int getRow() { return row; }
    public int getCol() { return col; }

    // --- Named wall accessors ---

    public boolean isTopWall()    { return topWall; }
    public boolean isRightWall()  { return rightWall; }
    public boolean isBottomWall() { return bottomWall; }
    public boolean isLeftWall()   { return leftWall; }

    public void setTopWall(boolean present)    { this.topWall    = present; }
    public void setRightWall(boolean present)  { this.rightWall  = present; }
    public void setBottomWall(boolean present) { this.bottomWall = present; }
    public void setLeftWall(boolean present)   { this.leftWall   = present; }

    // --- Generic wall accessors ---

    public boolean hasWall(Wall wall) {
        return switch (wall) {
            case TOP    -> topWall;
            case RIGHT  -> rightWall;
            case BOTTOM -> bottomWall;
            case LEFT   -> leftWall;
        };
    }

    public void setWall(Wall wall, boolean present) {
        switch (wall) {
            case TOP    -> topWall    = present;
            case RIGHT  -> rightWall  = present;
            case BOTTOM -> bottomWall = present;
            case LEFT   -> leftWall   = present;
        }
    }

    // --- Utility ---

    public void resetWalls() {
        topWall    = true;
        rightWall  = true;
        bottomWall = true;
        leftWall   = true;
    }

    @Override
    public String toString() {
        return String.format("Cell(%d,%d)[T=%b R=%b B=%b L=%b]",
                row, col, topWall, rightWall, bottomWall, leftWall);
    }
}


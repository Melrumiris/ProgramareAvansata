package org.model;

import java.io.Serializable;
import java.util.List;

public class MazeData implements Serializable {

    private static final long serialVersionUID = 1L;

    public final List<List<Cell>> grid;

    public final int startRow;

    public final int startCol;


    public final int endRow;

    public final int endCol;

    public MazeData(List<List<Cell>> grid,
                    int startRow, int startCol,
                    int endRow,   int endCol) {
        this.grid     = grid;
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow   = endRow;
        this.endCol   = endCol;
    }
}

